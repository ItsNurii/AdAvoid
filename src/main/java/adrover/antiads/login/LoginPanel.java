/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adrover.antiads.login;

import javax.swing.*;
import java.awt.*;

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

    private final ApiClient apiClient;

    public interface LoginSuccessListener {

        void onLoginSuccess();
    }

    public LoginPanel(LoginSuccessListener listener) {

        apiClient = new ApiClient("https://dimedianetapi9.azurewebsites.net");

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

        // Email label
        gc.gridx = 0;
        gc.gridy = 1;
        add(new JLabel("Email:"), gc);

        emailField = new JTextField(20);
        gc.gridx = 1;
        add(emailField, gc);

        // Password label
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

        String[] creds = TokenManager.loadCredentials();
        if (creds != null) {
            emailField.setText(creds[0]);
            passwordField.setText(creds[1]);
            rememberCheck.setSelected(true);
        }
        
        loginButton.addActionListener(e -> login(listener));
    }

    private void login(LoginSuccessListener listener) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.");
            return;
        }

        try {
            String token = apiClient.login(email, password);

            if (token != null && !token.isBlank()) {
                JOptionPane.showMessageDialog(this, "Login success!");

                if (rememberCheck.isSelected()) {
                    TokenManager.saveToken(token);
                    TokenManager.saveCredentials(email, password);
                } else {
                    TokenManager.deleteToken();
                    TokenManager.deleteCredentials();
                }

                listener.onLoginSuccess();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed: empty token.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
