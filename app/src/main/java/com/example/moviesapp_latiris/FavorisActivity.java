package com.example.moviesapp_latiris;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.moviesapp_latiris.BottomNavHelper;
import java.util.ArrayList;
import java.util.List;

public class FavorisActivity extends AppCompatActivity {
    private static final String TAG = "FavorisActivity";
    
    private RecyclerView favoritesRecyclerView;
    private MyMovieAdapter favoritesAdapter;
    private TextView emptyStateTextView;
    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoris);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        favoritesManager = new FavoritesManager();
        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites when returning to this activity
        loadFavorites();

        // Update BottomNav indicator
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            BottomNavHelper.updateSelectedItem(bottomNav, R.id.nav_favoris);
        }
    }

    private void initViews() {
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateText);
    }

    private void setupRecyclerView() {
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoritesAdapter = new MyMovieAdapter(new ArrayList<>(), this);
        favoritesRecyclerView.setAdapter(favoritesAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        BottomNavHelper.setupBottomNavigation(this, bottomNavigationView, R.id.nav_favoris);
    }

    private void loadFavorites() {
        favoritesManager.getFavorites(new FavoritesManager.FavoritesListCallback() {
            @Override
            public void onSuccess(List<MyMovieData> favorites) {
                if (favorites.isEmpty()) {
                    emptyStateTextView.setVisibility(android.view.View.VISIBLE);
                    favoritesRecyclerView.setVisibility(android.view.View.GONE);
                } else {
                    emptyStateTextView.setVisibility(android.view.View.GONE);
                    favoritesRecyclerView.setVisibility(android.view.View.VISIBLE);
                    favoritesAdapter.setMovies(favorites);
                }
                Log.d(TAG, "Loaded " + favorites.size() + " favorite movies");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading favorites: " + error);
                emptyStateTextView.setText("Error loading favorites: " + error);
                emptyStateTextView.setVisibility(android.view.View.VISIBLE);
            }
        });
    }
}