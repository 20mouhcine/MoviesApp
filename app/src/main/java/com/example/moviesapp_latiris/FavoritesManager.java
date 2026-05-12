package com.example.moviesapp_latiris;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages favorite movies for users in Firebase Firestore
 */
public class FavoritesManager {
    private static final String TAG = "FavoritesManager";
    private static final String USERS_COLLECTION = "users";
    private static final String FAVORITES_COLLECTION = "favorites";

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    // Callbacks
    public interface FavoriteCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface FavoritesListCallback {
        void onSuccess(List<MyMovieData> favorites);
        void onError(String error);
    }

    public interface IsFavoriteCallback {
        void onResult(boolean isFavorite);
    }

    public FavoritesManager() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Get current user ID
     */
    private String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * Add a movie to favorites
     */
    public void addToFavorites(int movieId, String movieName, String movieDate, String movieImage,
                               FavoriteCallback callback) {
        addToFavorites(movieId, movieName, movieDate, movieImage, 0.0, callback);
    }

    /**
     * Add a movie to favorites with rating
     */
    public void addToFavorites(int movieId, String movieName, String movieDate, String movieImage,
                               double rating, FavoriteCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }

        Map<String, Object> favorite = new HashMap<>();
        favorite.put("movieId", movieId);
        favorite.put("movieName", movieName);
        favorite.put("movieDate", movieDate);
        favorite.put("movieImage", movieImage);
        favorite.put("rating", rating);
        favorite.put("addedAt", System.currentTimeMillis());

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .document(String.valueOf(movieId))
                .set(favorite)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Movie added to favorites: " + movieName);
                    callback.onSuccess("Added to favorites");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding to favorites", e);
                    callback.onError(e.getMessage() != null ? e.getMessage() : "Failed to add to favorites");
                });
    }

    /**
     * Remove a movie from favorites
     */
    public void removeFromFavorites(int movieId, FavoriteCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .document(String.valueOf(movieId))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Movie removed from favorites: " + movieId);
                    callback.onSuccess("Removed from favorites");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing from favorites", e);
                    callback.onError(e.getMessage() != null ? e.getMessage() : "Failed to remove from favorites");
                });
    }

    /**
     * Check if a movie is in favorites
     */
    public void isFavorite(int movieId, IsFavoriteCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onResult(false);
            return;
        }

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .document(String.valueOf(movieId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.onResult(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking favorite status", e);
                    callback.onResult(false);
                });
    }

     /**
     * Get all favorite movies for current user
     */
    public void getFavorites(FavoritesListCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MyMovieData> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        try {
                            int movieId = Math.toIntExact(doc.getLong("movieId") != null ? doc.getLong("movieId") : 0);
                            String movieName = doc.getString("movieName");
                            String movieDate = doc.getString("movieDate");
                            String movieImage = doc.getString("movieImage");
                            Double rating = doc.getDouble("rating");
                            if (rating == null) rating = 0.0;

                            favorites.add(new MyMovieData(movieId, movieName, movieDate, movieImage, rating));
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing favorite", e);
                        }
                    }
                    Log.d(TAG, "Retrieved " + favorites.size() + " favorites");
                    callback.onSuccess(favorites);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting favorites", e);
                    callback.onError(e.getMessage() != null ? e.getMessage() : "Failed to get favorites");
                });
    }

    /**
     * Clear all favorites for current user
     */
    public void clearAllFavorites(FavoriteCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                    Log.d(TAG, "All favorites cleared");
                    callback.onSuccess("All favorites cleared");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error clearing favorites", e);
                    callback.onError(e.getMessage() != null ? e.getMessage() : "Failed to clear favorites");
                });
    }
}

