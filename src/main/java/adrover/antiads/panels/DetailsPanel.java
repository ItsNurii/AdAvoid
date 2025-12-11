/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package adrover.antiads.panels;

import adrover.antiads.Main;
import adrover.mediacomponent.ApiClient.Media;
import adrover.mediacomponent.MediaComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author nuria
 */
public class DetailsPanel extends javax.swing.JPanel {

    private MediaComponent mediaComponent;
    private File downloadFolder;

    private final List<MediaItem> mediaItems = new ArrayList<>();
    private final MediaTableModel tableModel = new MediaTableModel();

    public DetailsPanel(MediaComponent mediaComponent) {
        this.mediaComponent = mediaComponent;
        this.setSize(800, 600);
        initComponents();

        jTable.setModel(tableModel);

        downloadFolder = new File(System.getProperty("user.home") + "\\Downloads");

        // 🔄 Refresh action
        jButtonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllMedia();
            }
        });

        // 🗑️ Delete selected
        jButtonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = jList1.getSelectedIndex();
                if (idx != -1) {
                    MediaItem item = mediaItems.get(idx);
                    File f = new File(downloadFolder, item.getName());
                    if (f.exists() && f.delete()) {
                        JOptionPane.showMessageDialog(DetailsPanel.this, "File deleted: " + f.getName());
                        loadAllMedia();
                    } else {
                        JOptionPane.showMessageDialog(DetailsPanel.this, "Cannot delete file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(DetailsPanel.this, "Select a file to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // ⬅️ Return to Main JFrame
        jButtonReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(DetailsPanel.this);
                if (topFrame instanceof Main mainFrame) {
                    mainFrame.setContentPane(mainFrame.mainPanel);
                    mainFrame.revalidate();
                    mainFrame.repaint();
                }
            }
        });

        // 🎚️ ComboBox filter
        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) jComboBox1.getSelectedItem();
                filterTable(selected);
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

        // 2️⃣ Network (USANDO MEDIA COMPONENT CORRECTO)
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

        // Update UI
        DefaultListModel<String> model = new DefaultListModel<>();
        for (MediaItem m : mediaItems) {
            model.addElement(m.getName());
        }
        jList1.setModel(model);

        filterTable((String) jComboBox1.getSelectedItem());
    }

    private void filterTable(String filter) {
        List<MediaItem> filtered = mediaItems.stream()
                .filter(new Predicate<MediaItem>() {
                    @Override
                    public boolean test(MediaItem item) {
                        return "All".equalsIgnoreCase(filter) || item.getType().equalsIgnoreCase(filter);
                    }
                })
                .toList();
        tableModel.setItems(filtered);
    }

    private void uploadSelected() {
        int idx = jList1.getSelectedIndex();
        if (idx == -1) {
            return;
        }

        MediaItem item = mediaItems.get(idx);

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
        int idx = jList1.getSelectedIndex();
        if (idx == -1) {
            return;
        }

        MediaItem item = mediaItems.get(idx);
        if (item.getState() == MediaItem.State.LOCAL_ONLY) {
            return;
        }

        try {
            File dest = new File(downloadFolder, item.getName());
            mediaComponent.download(item.getId(), dest);
            JOptionPane.showMessageDialog(this, "Downloaded: " + item.getName());
            loadAllMedia();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Download failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =======================================
    private static class MediaTableModel extends AbstractTableModel {

        private final String[] columns = {"Name", "Size (MB)", "Type", "Date Modified", "State"};
        private List<MediaItem> items = new ArrayList<>();
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        public void setItems(List<MediaItem> items) {
            this.items = items;
            fireTableDataChanged();
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
            switch (column) {
                case 0:
                    return item.getName();
                case 1:
                    return String.format("%.2f", item.getSize() / (1024.0 * 1024.0));
                case 2:
                    return item.getType();
                case 3:
                    return sdf.format(item.getModified());
                case 4:
                    return item.getState().name();
                default:
                    return null;
            }
        }

        public MediaItem getItemAt(int row) {
            return items.get(row);
        }
    }

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

        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButtonDelete = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButtonUpload = new javax.swing.JButton();
        jButtonDownload = new javax.swing.JButton();

        setLayout(null);

        jScrollPane2.setViewportView(jList1);

        add(jScrollPane2);
        jScrollPane2.setBounds(30, 20, 590, 190);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Video", "Audio" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        add(jComboBox1);
        jComboBox1.setBounds(660, 280, 80, 30);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        add(jButtonDelete);
        jButtonDelete.setBounds(660, 80, 72, 23);

        jButtonRefresh.setText("Refresh");
        add(jButtonRefresh);
        jButtonRefresh.setBounds(660, 40, 72, 23);

        jButtonReturn.setText("Return");
        add(jButtonReturn);
        jButtonReturn.setBounds(330, 440, 72, 23);

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
        jScrollPane3.setBounds(30, 240, 600, 190);

        jButtonUpload.setText("Upload");
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });
        add(jButtonUpload);
        jButtonUpload.setBounds(650, 180, 100, 23);

        jButtonDownload.setText("Download");
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });
        add(jButtonDownload);
        jButtonDownload.setBounds(650, 210, 100, 23);
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
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables
}
