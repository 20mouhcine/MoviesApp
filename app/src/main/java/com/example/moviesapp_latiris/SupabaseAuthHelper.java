package com.example.moviesapp_latiris;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Supabase Authentication Helper
 *
 * Cette classe gère les appels API à Supabase pour:
 * - Enregistrement d'utilisateur par email/password
 * - Connexion utilisateur par email/password
 * - Authentification via Google (via token ID)
 * - Gestion des tokens JWT
 */
public class SupabaseAuthHelper {
    private static final String TAG = "SupabaseAuthHelper";

    /**
     * Enregistrer un utilisateur avec email et mot de passe
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe
     * @param callback Callback pour le résultat
     */
    public static void registerWithEmail(String email, String password,
                                         AuthCallback callback) {
        new Thread(() -> {
            try {
                String url = SupabaseManager.SUPABASE_URL + "/auth/v1/signup";
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                String response = makePostRequest(url, body.toString(),
                    SupabaseManager.SUPABASE_ANON_KEY);

                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.has("user")) {
                        String userId = jsonResponse.getJSONObject("user").getString("id");
                        String accessToken = jsonResponse.getString("session").split(":")[0];
                        Log.d(TAG, "Registration successful: " + userId);
                        callback.onSuccess(userId, email, null, accessToken);
                    } else if (jsonResponse.has("error")) {
                        String error = jsonResponse.getString("error");
                        callback.onError(error);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Registration error", e);
                callback.onError("Registration failed: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Connexion utilisateur avec email et mot de passe
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe
     * @param callback Callback pour le résultat
     */
    public static void loginWithEmail(String email, String password,
                                      AuthCallback callback) {
        new Thread(() -> {
            try {
                String url = SupabaseManager.SUPABASE_URL + "/auth/v1/token?grant_type=password";
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                String response = makePostRequest(url, body.toString(),
                    SupabaseManager.SUPABASE_ANON_KEY);

                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.has("access_token")) {
                        String userId = jsonResponse.getJSONObject("user").getString("id");
                        String accessToken = jsonResponse.getString("access_token");
                        Log.d(TAG, "Login successful: " + userId);
                        callback.onSuccess(userId, email, null, accessToken);
                    } else if (jsonResponse.has("error")) {
                        String error = jsonResponse.getString("error");
                        callback.onError(error);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Login error", e);
                callback.onError("Login failed: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Authentifier via Google ID Token
     *
     * @param idToken Token ID de Google
     * @param callback Callback pour le résultat
     */
    public static void signInWithGoogleIdToken(String idToken,
                                               AuthCallback callback) {
        new Thread(() -> {
            try {
                String url = SupabaseManager.SUPABASE_URL + "/auth/v1/token?grant_type=id_token";
                JSONObject body = new JSONObject();
                body.put("provider", "google");
                body.put("id_token", idToken);

                String response = makePostRequest(url, body.toString(),
                    SupabaseManager.SUPABASE_ANON_KEY);

                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.has("access_token")) {
                        JSONObject user = jsonResponse.getJSONObject("user");
                        String userId = user.getString("id");
                        String email = user.getString("email");
                        String name = user.optString("user_metadata", "{}");
                        String accessToken = jsonResponse.getString("access_token");

                        Log.d(TAG, "Google Sign-In successful: " + email);
                        callback.onSuccess(userId, email, name, accessToken);
                    } else if (jsonResponse.has("error")) {
                        String error = jsonResponse.getString("error");
                        callback.onError(error);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Google Sign-In error", e);
                callback.onError("Google Sign-In failed: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Faire une requête POST à Supabase
     */
    private static String makePostRequest(String urlString, String body,
                                          String apiKey) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("apikey", apiKey);
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        String response;

        if (responseCode == HttpURLConnection.HTTP_OK ||
            responseCode == HttpURLConnection.HTTP_CREATED) {
            response = readResponse(conn.getInputStream());
        } else {
            response = readResponse(conn.getErrorStream());
        }

        conn.disconnect();
        return response;
    }

    /**
     * Lire la réponse HTTP
     */
    private static String readResponse(java.io.InputStream input)
            throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(input, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    /**
     * Interface pour les callbacks d'authentification
     */
    public interface AuthCallback {
        void onSuccess(String userId, String email, String name, String accessToken);
        void onError(String error);
    }
}

