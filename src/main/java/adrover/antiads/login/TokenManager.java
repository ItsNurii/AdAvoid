/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adrover.antiads.login;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author nuria
 */
public class TokenManager {

    private static final Path TOKEN_DIR = Path.of(System.getProperty("user.home"), ".advoid");

    private static final Path TOKEN_FILE = TOKEN_DIR.resolve("token.txt");
    private static final Path CREDS_FILE = TOKEN_DIR.resolve("credentials.txt");

    //TOKEN
    public static void saveToken(String token) {
        try {
            Files.createDirectories(TOKEN_DIR);
            Files.writeString(TOKEN_FILE, token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadToken() {
        try {
            if (!Files.exists(TOKEN_FILE)) {
                return null;
            }
            return Files.readString(TOKEN_FILE).trim();
        } catch (IOException e) {
            return null;
        }
    }

    public static void deleteToken() {
        try {
            Files.deleteIfExists(TOKEN_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validate(String token) {
        try {
            ApiClient api = new ApiClient("https://dimedianetapi9.azurewebsites.net");
            HttpRequest request = api.requestBuilder("media", token).GET().build();
            HttpResponse<String> response = api.client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    //CREDENTIALS
    public static void saveCredentials(String email, String password) {
        try {
            Files.createDirectories(TOKEN_DIR);
            Files.writeString(CREDS_FILE, email + "\n" + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] loadCredentials() {
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

    public static void deleteCredentials() {
        try {
            Files.deleteIfExists(CREDS_FILE);
        } catch (IOException ignored) {
        }
    }
}
