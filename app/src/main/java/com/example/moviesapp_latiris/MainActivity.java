package com.example.moviesapp_latiris;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TMDB_API_KEY = "e21c9bd08ef733416fa4adc42dad2a14";
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String TAG = "MainActivity";
    
    private RecyclerView recyclerView;
    private MyMovieAdapter myMovieAdapter;
    private ImageView featuredImageView;
    private TextView featuredMovieName;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        
        fetchPopularMovies();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        featuredImageView = findViewById(R.id.movieImage);
        featuredMovieName = findViewById(R.id.movieName);
        playButton = findViewById(R.id.playButton);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        myMovieAdapter = new MyMovieAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(myMovieAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        BottomNavHelper.setupBottomNavigation(this, bottomNavigationView, R.id.nav_home);
    }

    private void fetchPopularMovies() {
        String url = BASE_URL + "?api_key=" + TMDB_API_KEY;

        Volley.newRequestQueue(this).add(new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<MyMovieData> movies = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            movies.add(new MyMovieData(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.optString("release_date", ""),
                                    obj.optString("poster_path", ""),
                                    obj.optDouble("vote_average", 0.0)
                            ));
                        }

                        if (!movies.isEmpty()) {
                            updateFeaturedMovie(movies.get(0));
                            myMovieAdapter.setMovies(movies);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Parsing error", e);
                    }
                },
                error -> Log.e(TAG, "Volley error", error)));
    }

    private void updateFeaturedMovie(MyMovieData movie) {
        featuredMovieName.setText(movie.getMovieName());
        Glide.with(this)
                .load(IMAGE_BASE_URL + movie.getMovieImage())
                .into(featuredImageView);

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            BottomNavHelper.updateSelectedItem(bottomNav, R.id.nav_home);
        }
    }
}
