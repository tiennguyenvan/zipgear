package com.example.demo.service;
import java.net.URI;
import java.net.URL;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;
import org.json.JSONObject;


public class OAuth2TokenFetcher {    

    public static String getAccessToken() {
        try {
            // Use URI to avoid deprecated URL constructor
            URL url = URI.create("https://oauth2.googleapis.com/token").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Construct the POST body to match OAuth Playground request exactly
            String data = String.format(
                "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token",
				Env.GMAIL_CLIENT_ID,
				Env.GMAIL_CLIENT_SECRET,
                Env.GMAIL_REFRESH_TOKEN
            );
            // Send the request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes());
            }

            // Print the response code and message for debugging
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Message: " + conn.getResponseMessage());

            // Handle response
            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    System.out.println("Response Body: " + responseBody);
                    return extractAccessToken(responseBody);
                }
            } else {
                try (Scanner scanner = new Scanner(conn.getErrorStream())) {
                    String errorResponse = scanner.useDelimiter("\\A").next();
                    System.err.println("Error Response: " + errorResponse);
                }
                throw new RuntimeException("Failed to get access token. Response code: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching access token", e);
        }
    }

    // Extract access token from the JSON response
    private static String extractAccessToken(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        return json.getString("access_token");
    }
}
