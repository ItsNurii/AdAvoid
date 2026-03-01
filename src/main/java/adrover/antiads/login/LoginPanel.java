/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adrover.antiads.login;

import adrover.antiads.Main;
import adrover.antiads.use.AppColor;
import adrover.mediacomponent.MediaComponent;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Login panel of the AdVoid application.
 * <p>
 * This panel provides user authentication functionality, including:
 * </p>
 * <ul>
 * <li>Email and password input</li>
 * <li>Remember-me option</li>
 * <li>Token-based auto-login</li>
 * <li>Light and dark theme support</li>
 * </ul>
 *
 * <p>
 * Authentication tokens and credentials are stored locally in the user's home
 * directory.
 * </p>
 *
 * @author Nuria
 * @version 1.0
 */
public class LoginPanel extends JPanel {

    /**
     * Email input field.
     */
    private final JTextField emailField;

    /**
     * Password input field.
     */
    private final JPasswordField passwordField;

    /**
     * Checkbox to remember user credentials.
     */
    private final JCheckBox rememberCheck;

    /**
     * Button used to perform login.
     */
    private final JButton loginButton;

    /**
     * Label used to display status or error messages.
     */
    private final JLabel statusLabel;

    /**
     * Media component used to perform API authentication.
     */
    private final MediaComponent mediaComponent;

    /**
     * Reference to the main application window.
     */
    private Main parent;

    /**
     * Title label of the login panel.
     */
    private JLabel titleLabel;

    /**
     * Label for the email field (icon-based).
     */
    private JLabel emailLabel;

    /**
     * Label for the password field (icon-based).
     */
    private JLabel passwordLabel;

    // === Paths internos ===
    /**
     * Directory used to store authentication data.
     */
    private final Path TOKEN_DIR
            = Path.of(System.getProperty("user.home"), ".advoid");

    /**
     * File used to store the authentication token.
     */
    private final Path TOKEN_FILE = TOKEN_DIR.resolve("token.txt");

    /**
     * File used to store saved credentials.
     */
    private final Path CREDS_FILE = TOKEN_DIR.resolve("credentials.txt");

    /**
     * Creates a new {@code LoginPanel}.
     *
     * @param parent reference to the main application
     * @param mediaComponent media component responsible for API communication
     */
    public LoginPanel(Main parent, MediaComponent mediaComponent) {
        this.parent = parent;
        this.mediaComponent = mediaComponent;

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);

        titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.DARK_GRAY); // color por defecto
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        add(titleLabel, gc);
        gc.gridwidth = 1;

        emailLabel = new JLabel(loadIcon("/images/usuario.png"));
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailLabel.setForeground(Color.DARK_GRAY);
        gc.gridx = 0;
        gc.gridy = 1;
        add(emailLabel, gc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gc.gridx = 1;
        add(emailField, gc);

        passwordLabel = new JLabel(loadIcon("/images/cerrar.png"));
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.DARK_GRAY);
        gc.gridx = 0;
        gc.gridy = 2;
        add(passwordLabel, gc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gc.gridx = 1;
        add(passwordField, gc);

        // Remember me
        rememberCheck = new JCheckBox("Remember me");
        rememberCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 2;
        add(rememberCheck, gc);
        gc.gridwidth = 1;

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setEnabled(false);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        loginButton.setBackground(new Color(60, 150, 250));
        loginButton.setForeground(Color.WHITE);
        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridwidth = 2;
        add(loginButton, gc);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        gc.gridy = 5;
        add(statusLabel, gc);

        // Eventos
        DocumentListener docListener = new DocumentListener() {
            void update() {
                boolean enable = !emailField.getText().trim().isEmpty()
                        && passwordField.getPassword().length > 0;
                loginButton.setEnabled(enable);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        };
        emailField.getDocument().addDocumentListener(docListener);
        passwordField.getDocument().addDocumentListener(docListener);

        loginButton.addActionListener(e -> {
            if (emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this,
                        "You cant login with empty fields!",
                        "Empty fields", JOptionPane.WARNING_MESSAGE);
                return;
            }
            login();
        });

        // Auto-login si ya existe token
        autoLoginIfTokenExists();

        // Cargar email + password guardados
        loadSavedCredentials();
    }

    /**
     * Loads and scales an icon from the classpath.
     *
     * @param path resource path of the icon
     * @return loaded icon or {@code null} if not found
     */
    private ImageIcon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("No se pudo cargar el icono: " + path);
            return null;
        }

        Image img = new ImageIcon(url).getImage();
        Image scaled = img.getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Applies the selected theme to the login panel.
     *
     * @param darkMode {@code true} for dark theme, {@code false} for light
     * theme
     */
    public void applyTheme(boolean darkMode) {
        Color bg = darkMode ? AppColor.DARK_BG : AppColor.LIGHT_BG;
        Color panel = darkMode ? AppColor.DARK_PANEL : AppColor.LIGHT_PANEL;
        Color btn = darkMode ? new Color(80, 160, 255) : new Color(60, 150, 250);
        Color text = darkMode ? Color.WHITE : Color.DARK_GRAY;

        setBackground(bg);

        // Labels
        titleLabel.setForeground(text);
        emailLabel.setForeground(text);
        passwordLabel.setForeground(text);
        statusLabel.setForeground(Color.RED);

        // Campos
        emailField.setBackground(panel);
        emailField.setForeground(text);
        passwordField.setBackground(panel);
        passwordField.setForeground(text);

        // Checkbox
        rememberCheck.setBackground(bg);
        rememberCheck.setForeground(text);

        // Botón
        loginButton.setBackground(btn);
        loginButton.setForeground(Color.WHITE);
        repaint();
    }

    /**
     * Returns the stored authentication token, if available.
     *
     * @return stored token or {@code null}
     */
    public String getSavedToken() {
        return loadToken();
    }

    /**
     * Clears all saved authentication data.
     */
    public void clearSavedAuth() {
        deleteToken();
        deleteCredentials();
    }

    /**
     * Saves the authentication token locally.
     *
     * @param token authentication token
     */
    private void saveToken(String token) {
        try {
            Files.createDirectories(TOKEN_DIR);
            Files.writeString(TOKEN_FILE, token);
        } catch (IOException ignored) {
        }
    }

    /**
     * Loads the stored authentication token.
     *
     * @return token or {@code null} if not found
     */
    private String loadToken() {
        try {
            if (!Files.exists(TOKEN_FILE)) {
                return null;
            }
            return Files.readString(TOKEN_FILE).trim();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deletes the stored authentication token.
     */
    private void deleteToken() {
        try {
            Files.deleteIfExists(TOKEN_FILE);
        } catch (IOException ignored) {
        }
    }

    /**
     * Saves user credentials locally.
     *
     * @param email user email
     * @param password user password
     */
    private void saveCredentials(String email, String password) {
        try {
            Files.createDirectories(TOKEN_DIR);
            Files.writeString(CREDS_FILE, email + "\n" + password);
        } catch (IOException ignored) {
        }
    }

    /**
     * Loads saved credentials from file.
     *
     * @return array with email and password, or {@code null}
     */
    private String[] loadCredentialsFile() {
        try {
            if (!Files.exists(CREDS_FILE)) {
                return null;
            }

            var lines = Files.readAllLines(CREDS_FILE);
            if (lines.size() < 2) {
                return null;
            }

            return new String[]{lines.get(0), lines.get(1)};
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deletes saved credentials.
     */
    private void deleteCredentials() {
        try {
            Files.deleteIfExists(CREDS_FILE);
        } catch (IOException ignored) {
        }
    }

    // ===================================
    // 🔥 LÓGICA DE LOGIN Y AUTOLOGIN
    // ===================================
    /**
     * Automatically logs in the user if a valid token exists.
     */
    private void autoLoginIfTokenExists() {
        String token = loadToken();
        if (token != null && !token.isEmpty()) {
            mediaComponent.setToken(token);
            parent.startWithToken(token);
        }
    }

    /**
     * Loads saved credentials into the input fields.
     */
    private void loadSavedCredentials() {
        String[] creds = loadCredentialsFile();
        if (creds != null) {
            emailField.setText(creds[0]);
            passwordField.setText(creds[1]);
            rememberCheck.setSelected(true);
        }
    }

    /**
     * Performs the login operation using the media component API.
     */
    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.");
            return;
        }

        try {
            if (mediaComponent.getApiUrl() == null || mediaComponent.getApiUrl().isBlank()) {
                JOptionPane.showMessageDialog(this, "MediaComponent API URL not set!");
                return;
            }
            String token = mediaComponent.login(email, password);

            if (token != null && !token.isBlank()) {

                mediaComponent.setToken(token);

                if (rememberCheck.isSelected()) {
                    saveCredentials(email, password);
                } else {
                    deleteCredentials();
                }

                saveToken(token);
                parent.startWithToken(token);

            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
        }
    }
}
