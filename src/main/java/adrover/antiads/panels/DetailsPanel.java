/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package adrover.antiads.panels;

import adrover.antiads.Main;
import adrover.antiads.use.AppColor;
import adrover.mediacomponent.ApiClient.Media;
import adrover.mediacomponent.MediaComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author nuria
 */
public class DetailsPanel extends javax.swing.JPanel {

    private MediaComponent mediaComponent;
    private File downloadFolder;

    private final Dimension baseSize = new Dimension(800, 600);
    private final Map<Component, Rectangle> baseBounds = new HashMap<>();

    private final List<MediaItem> mediaItems = new ArrayList<>();
    private final MediaTableModel tableModel = new MediaTableModel();

    public DetailsPanel(MediaComponent mediaComponent, boolean darkMode) {
        this.mediaComponent = mediaComponent;
        this.setSize(800, 600);
        initComponents();
        enableAutoResize();
        jTable.setModel(tableModel);

        downloadFolder = new File(System.getProperty("user.home") + "\\Downloads");

        jTable.setModel(tableModel);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.setAutoCreateRowSorter(true);

        // 🔄 Refresh
        jButtonRefresh.addActionListener(e -> loadAllMedia());

        // 🗑️ Delete
        jButtonDelete.addActionListener(e -> deleteSelected());

        // ⬅️ Return
        jButtonReturn.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(DetailsPanel.this);
            if (topFrame instanceof Main mainFrame) {
                mainFrame.setContentPane(mainFrame.mainPanel);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });

        // 🎚️ Filter
        jComboBox1.addActionListener(e -> {
            String selected = (String) jComboBox1.getSelectedItem();
            filterTable(selected);
        });

        // ⬆️ Upload
        jButtonUpload.addActionListener(e -> uploadSelected());

        // ⬇️ Download
        jButtonDownload.addActionListener(e -> downloadSelected());

        // UX: enable/disable buttons
        jTable.getSelectionModel().addListSelectionListener(e -> updateButtons());

        updateButtons();

        applyTheme(darkMode);

    }

    public void applyTheme(boolean darkMode) {
        Color bg = darkMode ? AppColor.DARK_BG : AppColor.LIGHT_BG;
        Color panel = darkMode ? AppColor.DARK_PANEL : AppColor.LIGHT_PANEL;
        Color text = darkMode ? AppColor.DARK_TEXT : AppColor.LIGHT_TEXT;

        // Fondo del panel
        setBackground(bg);

        // Tabla y scroll
        jTable.setBackground(panel);
        jTable.setForeground(text);
        jTable.setGridColor(text);
        
        jScrollPane3.getViewport().setBackground(panel);


        // Combobox
        jComboBox1.setBackground(panel);
        jComboBox1.setForeground(text);

        repaint();
    }

    private void enableAutoResize() {

        // Guardar bounds originales de todos los componentes
        for (Component c : getComponents()) {
            baseBounds.put(c, c.getBounds());
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                double scaleX = getWidth() / (double) baseSize.width;
                double scaleY = getHeight() / (double) baseSize.height;

                for (Map.Entry<Component, Rectangle> entry : baseBounds.entrySet()) {

                    Component c = entry.getKey();
                    Rectangle r = entry.getValue();

                    int newX = (int) (r.x * scaleX);
                    int newY = (int) (r.y * scaleY);
                    int newW = (int) (r.width * scaleX);
                    int newH = (int) (r.height * scaleY);

                    c.setBounds(newX, newY, newW, newH);
                }

                revalidate();
                repaint();
            }
        });
    }

    public void loadAllMedia() {
        if (mediaComponent.getToken() == null || mediaComponent.getToken().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No estás autenticado. Por favor, inicia sesión primero.",
                    "Error 401", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mediaItems.clear();

        // 1️⃣ Local
        File[] localFiles = downloadFolder.listFiles(f
                -> f.getName().endsWith(".mp4") || f.getName().endsWith(".mp3"));

        if (localFiles != null) {
            for (File f : localFiles) {
                mediaItems.add(new MediaItem(
                        0,
                        f.getName(),
                        f.length(),
                        f.getName().endsWith(".mp4") ? "Video" : "Audio",
                        new Date(f.lastModified()),
                        MediaItem.State.LOCAL_ONLY
                ));
            }
        }

        // 2️⃣ Network
        try {
            List<Media> apiMedia = mediaComponent.getAllMedia();

            for (Media m : apiMedia) {
                File local = new File(downloadFolder, m.mediaFileName);

                MediaItem.State state = local.exists()
                        ? MediaItem.State.BOTH
                        : MediaItem.State.NETWORK_ONLY;

                String type = "Unknown";
                if (m.mediaMimeType != null) {
                    if (m.mediaMimeType.startsWith("audio")) {
                        type = "Audio";
                    }
                    if (m.mediaMimeType.startsWith("video")) {
                        type = "Video";
                    }
                }

                mediaItems.add(new MediaItem(
                        m.id,
                        m.mediaFileName,
                        local.exists() ? local.length() : 0,
                        type,
                        local.exists() ? new Date(local.lastModified()) : new Date(),
                        state
                ));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching network media:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        filterTable((String) jComboBox1.getSelectedItem());
    }

    private void filterTable(String filter) {
        List<MediaItem> filtered = mediaItems.stream()
                .filter(item
                        -> "All".equalsIgnoreCase(filter)
                || item.getType().equalsIgnoreCase(filter))
                .toList();

        tableModel.setItems(filtered);
    }

    private MediaItem getSelectedItemFromTable() {
        int row = jTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        int modelRow = jTable.convertRowIndexToModel(row);
        return tableModel.getItemAt(modelRow);
    }

    private void deleteSelected() {
        MediaItem item = getSelectedItemFromTable();

        if (item == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a file to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        File f = new File(downloadFolder, item.getName());
        if (f.exists() && f.delete()) {
            JOptionPane.showMessageDialog(this, "File deleted: " + f.getName());
            loadAllMedia();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete file.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void uploadSelected() {
        MediaItem item = getSelectedItemFromTable();
        if (item == null) {
            return;
        }

        if (item.getState() == MediaItem.State.NETWORK_ONLY) {
            return;
        }

        try {
            File f = new File(downloadFolder, item.getName());
            mediaComponent.uploadFileMultipart(f, null);

            JOptionPane.showMessageDialog(this, "Uploaded: " + item.getName());
            loadAllMedia();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Upload failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void downloadSelected() {
        MediaItem item = getSelectedItemFromTable();
        if (item == null) {
            return;
        }

        if (item.getState() == MediaItem.State.LOCAL_ONLY) {
            return;
        }

        try {
            File dest = new File(downloadFolder, item.getName());
            mediaComponent.download(item.getId(), dest);

            JOptionPane.showMessageDialog(this, "Downloaded: " + item.getName());
            loadAllMedia();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Download failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateButtons() {
        MediaItem item = getSelectedItemFromTable();
        boolean hasSelection = item != null;

        jButtonDelete.setEnabled(hasSelection && item.getState() != MediaItem.State.NETWORK_ONLY);
        jButtonUpload.setEnabled(hasSelection && item.getState() != MediaItem.State.NETWORK_ONLY);
        jButtonDownload.setEnabled(hasSelection && item.getState() != MediaItem.State.LOCAL_ONLY);
    }

    // ================= Table Model =================
    private static class MediaTableModel extends AbstractTableModel {

        private final String[] columns = {"Name", "Size (MB)", "Type", "Date Modified", "State"};
        private List<MediaItem> items = new ArrayList<>();
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        public void setItems(List<MediaItem> items) {
            this.items = items;
            fireTableDataChanged();
        }

        public MediaItem getItemAt(int row) {
            return items.get(row);
        }

        @Override
        public int getRowCount() {
            return items.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int row, int column) {
            MediaItem item = items.get(row);
            return switch (column) {
                case 0 ->
                    item.getName();
                case 1 ->
                    String.format("%.2f", item.getSize() / (1024.0 * 1024.0));
                case 2 ->
                    item.getType();
                case 3 ->
                    sdf.format(item.getModified());
                case 4 ->
                    item.getState().name();
                default ->
                    null;
            };
        }
    }

    // ================= Model =================
    public static class MediaItem {

        public enum State {
            LOCAL_ONLY, NETWORK_ONLY, BOTH
        }

        private final int id;
        private final String name;
        private final long size;
        private final String type;
        private final Date modified;
        private State state;

        public MediaItem(int id, String name, long size, String type, Date modified, State state) {
            this.id = id;
            this.name = name;
            this.size = size;
            this.type = type;
            this.modified = modified;
            this.state = state;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public String getType() {
            return type;
        }

        public Date getModified() {
            return modified;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jButtonDelete = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButtonUpload = new javax.swing.JButton();
        jButtonDownload = new javax.swing.JButton();

        setLayout(null);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Video", "Audio" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        add(jComboBox1);
        jComboBox1.setBounds(660, 340, 80, 30);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        add(jButtonDelete);
        jButtonDelete.setBounds(660, 120, 80, 23);

        jButtonRefresh.setText("Refresh");
        add(jButtonRefresh);
        jButtonRefresh.setBounds(660, 80, 80, 23);

        jButtonReturn.setText("Return");
        add(jButtonReturn);
        jButtonReturn.setBounds(330, 550, 72, 23);

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Size", "Myme type", "Date"
            }
        ));
        jScrollPane3.setViewportView(jTable);

        add(jScrollPane3);
        jScrollPane3.setBounds(30, 30, 600, 500);

        jButtonUpload.setText("Upload");
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });
        add(jButtonUpload);
        jButtonUpload.setBounds(650, 200, 100, 23);

        jButtonDownload.setText("Download");
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });
        add(jButtonDownload);
        jButtonDownload.setBounds(650, 240, 100, 23);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed
        uploadSelected();
    }//GEN-LAST:event_jButtonUploadActionPerformed

    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed
        downloadSelected();
    }//GEN-LAST:event_jButtonDownloadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables
}
