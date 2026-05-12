package com.example.moviesapp_latiris;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Helper class to manage BottomNavigationView consistently across activities
 */
public class BottomNavHelper {

    /**
     * Setup bottom navigation for the current activity
     * @param activity The current activity
     * @param bottomNav The BottomNavigationView
     * @param currentItemId The current activity's navigation item ID
     */
    public static void setupBottomNavigation(AppCompatActivity activity,
                                             BottomNavigationView bottomNav,
                                             int currentItemId) {

        // Set current selected item
        bottomNav.setSelectedItemId(currentItemId);

        // Navigation listener
        bottomNav.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            // If current item clicked again
            if (itemId == currentItemId) {
                return true;
            }

            // Navigate to activities
            if (itemId == R.id.nav_home) {
                navigateTo(activity, MainActivity.class);
                return true;

            } else if (itemId == R.id.nav_explore) {
                navigateTo(activity, MapActivity.class);
                return true;

            } else if (itemId == R.id.nav_movies) {
                navigateTo(activity, MoviesActivity.class);
                return true;

            } else if (itemId == R.id.nav_favoris) {
                navigateTo(activity, FavorisActivity.class);
                return true;

            } else if (itemId == R.id.nav_profile) {
                navigateTo(activity, ProfileActivity.class);
                return true;
            }

            return false;
        });
    }

    /**
     * Navigate to another activity
     */
    private static void navigateTo(AppCompatActivity activity,
                                   Class<?> targetClass) {

        Intent intent = new Intent(activity, targetClass);

        // Prevent multiple instances
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        activity.startActivity(intent);

        // Transition animation
        activity.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }

    /**
     * Update selected item on resume
     */
    public static void updateSelectedItem(BottomNavigationView bottomNav,
                                          int currentItemId) {

        if (bottomNav.getSelectedItemId() != currentItemId) {
            bottomNav.setSelectedItemId(currentItemId);
        }
    }
}