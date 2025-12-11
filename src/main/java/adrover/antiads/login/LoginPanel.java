/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adrover.antiads.login;

import adrover.antiads.Main;
import adrover.mediacomponent.MediaComponent;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author nuria
 */
public class LoginPanel extends JPanel {

    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JCheckBox rememberCheck;
    private final JButton loginButton;
    private final JLabel statusLabel;

    private final MediaComponent mediaComponent;
    private Main parent;

    // === Paths internos ===
    private final Path TOKEN_DIR = Path.of(System.getProperty("user.home"), ".advoid");
    private final Path TOKEN_FILE = TOKEN_DIR.resolve("token.txt");
    private final Path CREDS_FILE = TOKEN_DIR.resolve("credentials.txt");

    public LoginPanel(Main parent, MediaComponent mediaComponent) {
        this.parent = parent;
        this.mediaComponent = mediaComponent;

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        add(title, gc);
        gc.gridwidth = 1;

        // Email
        gc.gridx = 0;
        gc.gridy = 1;
        add(new JLabel("Email:"), gc);
        emailField = new JTextField(20);
        gc.gridx = 1;
        add(emailField, gc);

        // Password
        gc.gridx = 0;
        gc.gridy = 2;
        add(new JLabel("Password:"), gc);
        passwordField = new JPasswordField(20);
        gc.gridx = 1;
        add(passwordField, gc);

        // Remember me
        rememberCheck = new JCheckBox("Remember me");
        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 2;
        add(rememberCheck, gc);
        gc.gridwidth = 1;

        // Login button
        loginButton = new JButton("Login");
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
        loginButton.addActionListener(e -> login());

        // Auto-login si ya existe token
        autoLoginIfTokenExists();

        // Cargar email + password guardados
        loadSavedCredentials();
    }

    public String getSavedToken() {
        return loadToken();
    }

    public void clearSavedAuth() {
        deleteToken();
        deleteCredentials();
    }

    // ============================================================
    // 🔥 REEMPLAZO COMPLETO DE TOKENMANAGER (todo integrado aquí)
    // ============================================================
    private void saveToken(String token) {
        try {
            Files.createDirectories(TOKEN_DIR);
            Files.writeString(TOKEN_FILE, token);
        } catch (IOException ignored) {
        }
    }

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

    private void deleteToken() {
        try {
            Files.deleteIfExists(TOKEN_FILE);
        } catch (IOException ignored) {
        }
    }

    private void saveCredentials(String email, String password) {
        try {
            Files.createDirectories(TOKEN_DIR);
            Files.writeString(CREDS_FILE, email + "\n" + password);
        } catch (IOException ignored) {
        }
    }

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

    private void deleteCredentials() {
        try {
            Files.deleteIfExists(CREDS_FILE);
        } catch (IOException ignored) {
        }
    }

    // ============================================================
    // 🔥 LÓGICA DE LOGIN Y AUTOLOGIN
    // ============================================================
    private void autoLoginIfTokenExists() {
        String token = loadToken();
        if (token != null && !token.isEmpty()) {
            mediaComponent.setToken(token);
            parent.startWithToken(token);
        }
    }

    private void loadSavedCredentials() {
        String[] creds = loadCredentialsFile();
        if (creds != null) {
            emailField.setText(creds[0]);
            passwordField.setText(creds[1]);
            rememberCheck.setSelected(true);
        }
    }

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
