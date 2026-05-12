package com.example.moviesapp_latiris;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

/**
 * Manager class for handling Supabase and Google Authentication
 */
public class SupabaseManager {
    private static final String TAG = "SupabaseManager";

    // TODO: Replace with your Supabase project URL and API key
    public static final String SUPABASE_URL = "https://your-project.supabase.co";
    public static final String SUPABASE_ANON_KEY = "your-anon-key";

    // TODO: Replace with your Google OAuth 2.0 Client ID (from Firebase Console or Google Cloud Console)
    public static final String GOOGLE_WEB_CLIENT_ID = "your-web-client-id.apps.googleusercontent.com";

    private static GoogleSignInClient googleSignInClient;

    /**
     * Initialize Google Sign-In client
     */
    public static void initGoogleSignIn(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    /**
     * Get the GoogleSignInClient instance
     */
    public static GoogleSignInClient getGoogleSignInClient() {
        if (googleSignInClient == null) {
            throw new IllegalStateException("GoogleSignInClient not initialized. Call initGoogleSignIn first.");
        }
        return googleSignInClient;
    }

    /**
     * Handle Google Sign-In result
     */
    public static GoogleSignInAccount handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(com.google.android.gms.common.api.ApiException.class);
            Log.d(TAG, "Google Sign-In successful: " + account.getEmail());
            return account;
        } catch (com.google.android.gms.common.api.ApiException e) {
            Log.w(TAG, "Google Sign-In failed with code: " + e.getStatusCode());
            return null;
        }
    }

    /**
     * Sign out from Google
     */
    public static void signOutGoogle() {
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
            Log.d(TAG, "Google Sign-Out successful");
        }
    }

    /**
     * Get current signed-in account
     */
    public static GoogleSignInAccount getCurrentGoogleAccount(Context context) {
        return GoogleSignIn.getLastSignedInAccount(context);
    }

    /**
     * Check if user is currently signed in with Google
     */
    public static boolean isSignedIn(Context context) {
        return getCurrentGoogleAccount(context) != null;
    }
}

