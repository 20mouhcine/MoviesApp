package com.example.moviesapp_latiris;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Firebase Authentication Manager
 * Handles Google Sign-In and Firebase Auth integration
 */
public class FirebaseAuthManager {
    private static final String TAG = "FirebaseAuthManager";

    // TODO: Replace with your Google Web Client ID from Firebase Console
    public static final String GOOGLE_WEB_CLIENT_ID = "422852147244-q9bjf9kntr17e98pqprc5fd3mqpfo22r.apps.googleusercontent.com";

    private static GoogleSignInClient googleSignInClient;
    private static FirebaseAuth firebaseAuth;

    /**
     * Initialize Firebase and Google Sign-In
     */
    public static void init(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
        Log.d(TAG, "Firebase Auth initialized");
    }

    /**
     * Get Firebase Auth instance
     */
    public static FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    /**
     * Get Google Sign-In client
     */
    public static GoogleSignInClient getGoogleSignInClient() {
        if (googleSignInClient == null) {
            throw new IllegalStateException("GoogleSignInClient not initialized. Call init() first.");
        }
        return googleSignInClient;
    }

    /**
     * Get Google Sign-In intent that forces account selection UI
     */
    public static Intent getSignInIntentWithAccountPicker() {
        if (googleSignInClient == null) {
            throw new IllegalStateException("GoogleSignInClient not initialized. Call init() first.");
        }
        return googleSignInClient.getSignInIntent();
    }

    /**
     * Register user with email and password
     */
    public static void registerWithEmail(String email, String password,
                                         AuthCallback callback) {
        getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Registration successful: " + user.getEmail());
                            callback.onSuccess(user.getUid(), user.getEmail(),
                                user.getDisplayName(), null);
                        }
                    } else {
                        Log.w(TAG, "Registration failed", task.getException());
                        callback.onError(task.getException() != null ?
                            task.getException().getMessage() : "Registration failed");
                    }
                });
    }

    /**
     * Login with email and password
     */
    public static void loginWithEmail(String email, String password,
                                      AuthCallback callback) {
        getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Login successful: " + user.getEmail());
                            callback.onSuccess(user.getUid(), user.getEmail(),
                                user.getDisplayName(), null);
                        }
                    } else {
                        Log.w(TAG, "Login failed", task.getException());
                        callback.onError(task.getException() != null ?
                            task.getException().getMessage() : "Login failed");
                    }
                });
    }

    /**
     * Handle Google Sign-In result
     */
    public static void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask,
                                               AuthCallback callback) {
        try {
            GoogleSignInAccount account = completedTask.getResult(
                com.google.android.gms.common.api.ApiException.class);

            if (account != null && account.getIdToken() != null) {
                signInWithGoogle(account, callback);
            }
        } catch (com.google.android.gms.common.api.ApiException e) {
            Log.w(TAG, "Google Sign-In failed", e);
            callback.onError("Google Sign-In failed: " + e.getMessage());
        }
    }

    /**
     * Sign in to Firebase with Google credentials
     */
    private static void signInWithGoogle(GoogleSignInAccount account,
                                        AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(
            account.getIdToken(), null);

        getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Google Sign-In successful: " + user.getEmail());
                            callback.onSuccess(user.getUid(), user.getEmail(),
                                user.getDisplayName(), null);
                        }
                    } else {
                        Log.w(TAG, "Firebase sign-in failed", task.getException());
                        callback.onError(task.getException() != null ?
                            task.getException().getMessage() : "Sign-in failed");
                    }
                });
    }

    /**
     * Get current signed-in user
     */
    public static FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    /**
     * Check if user is signed in
     */
    public static boolean isSignedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Sign out from Firebase and Google (for forcing account selection UI)
     */
    public static void signOut(Context context) {
        getFirebaseAuth().signOut();

        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }

        Log.d(TAG, "User signed out");
    }

    /**
     * Clear Google Sign-In cache to force account selection UI
     */
    public static void clearGoogleSignInCache(Context context) {
        if (googleSignInClient != null) {
            googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
                Log.d(TAG, "Google Sign-In cache cleared");
            });
        }
    }

    /**
     * Callback interface for auth operations
     */
    public interface AuthCallback {
        void onSuccess(String userId, String email, String displayName, String token);
        void onError(String error);
    }
}

