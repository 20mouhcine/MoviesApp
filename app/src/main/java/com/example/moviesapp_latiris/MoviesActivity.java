package com.example.moviesapp_latiris;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity {
    private static final String TAG = "MoviesActivity";
    private static final String TMDB_API_KEY = "e21c9bd08ef733416fa4adc42dad2a14";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    
    private RecyclerView moviesRecyclerView;
    private RecyclerView genreRecyclerView;
    private EditText searchEditText;
    private ImageView searchButton;
    private ImageView voiceSearchButton;
    private MyMovieAdapter movieAdapter;
    private GenreAdapter genreAdapter;
    private RequestQueue requestQueue;

    private ActivityResultLauncher<String> audioPermissionLauncher;
    private ActivityResultLauncher<Intent> voiceSearchLauncher;

    private List<GenreData> genreList = new ArrayList<>();
    private int selectedGenreId = -1;
    
    // Pagination variables
    private int currentPage = 1;
    private boolean isLoading = false;
    private String currentQuery = null;

    // Search debounce
    private static final long SEARCH_DELAY_MS = 500; // Délai avant de faire la recherche
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        initViews();
        initVoiceSearch();
        requestQueue = Volley.newRequestQueue(this);
        
        setupMoviesRecyclerView();
        setupGenreRecyclerView();
        
        fetchGenres();
        fetchMovies(null, -1, 1); // Initial load: popular movies

        initBottomNavigation();
    }

    private void initViews() {
        moviesRecyclerView = findViewById(R.id.moviesRecyclerView);
        genreRecyclerView = findViewById(R.id.genreRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        voiceSearchButton = findViewById(R.id.voiceSearchButton);

        // Add TextWatcher for real-time search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove previous callback
                searchEditText.removeCallbacks(searchRunnable);

                // Create new search runnable with debounce
                searchRunnable = () -> performSearch();
                searchEditText.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        // Keep the search button for manual search
        searchButton.setOnClickListener(v -> {
            searchEditText.removeCallbacks(searchRunnable);
            performSearch();
        });

        voiceSearchButton.setOnClickListener(v -> startVoiceSearch());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchEditText.removeCallbacks(searchRunnable);
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void initVoiceSearch() {
        audioPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchVoiceSearchIntent();
                    } else {
                        Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        voiceSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            String spokenQuery = results.get(0).trim();
                            if (!spokenQuery.isEmpty()) {
                                searchEditText.setText(spokenQuery);
                                searchEditText.setSelection(spokenQuery.length());
                                searchEditText.removeCallbacks(searchRunnable);
                                performSearch();
                            }
                        }
                    }
                });
    }

    private void startVoiceSearch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            launchVoiceSearchIntent();
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void launchVoiceSearchIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Search movies");

        if (intent.resolveActivity(getPackageManager()) != null) {
            voiceSearchLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Voice search not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMoviesRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        moviesRecyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MyMovieAdapter(new ArrayList<>(), this);
        moviesRecyclerView.setAdapter(movieAdapter);

        // Add scroll listener for pagination
        moviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreMovies();
                    }
                }
            }
        });
    }

    private void setupGenreRecyclerView() {
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        genreAdapter = new GenreAdapter(genreList, genre -> {
            selectedGenreId = genre.getId();
            currentQuery = null;
            currentPage = 1;
            fetchMovies(null, selectedGenreId, currentPage);
        });
        genreRecyclerView.setAdapter(genreAdapter);
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            currentQuery = query;
            selectedGenreId = -1;
            currentPage = 1;
            fetchMovies(query, -1, currentPage);
            // Reset genre selection
            for (GenreData g : genreList) g.setSelected(false);
            genreAdapter.notifyDataSetChanged();
        }
    }

    private void loadMoreMovies() {
        currentPage++;
        fetchMovies(currentQuery, selectedGenreId, currentPage);
    }

    private void fetchGenres() {
        String url = BASE_URL + "/genre/movie/list?api_key=" + TMDB_API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray genres = response.getJSONArray("genres");
                        genreList.clear();
                        genreList.add(new GenreData(-1, "All")); // Default option
                        genreList.get(0).setSelected(true);
                        
                        for (int i = 0; i < genres.length(); i++) {
                            JSONObject obj = genres.getJSONObject(i);
                            genreList.add(new GenreData(obj.getInt("id"), obj.getString("name")));
                        }
                        genreAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Genre parse error", e);
                    }
                }, error -> Log.e(TAG, "Genre fetch error", error));
        requestQueue.add(request);
    }

    private void fetchMovies(String query, int genreId, int page) {
        isLoading = true;
        String url;
        if (query != null && !query.isEmpty()) {
            url = BASE_URL + "/search/movie?api_key=" + TMDB_API_KEY + "&query=" + query + "&page=" + page;
        } else if (genreId != -1) {
            url = BASE_URL + "/discover/movie?api_key=" + TMDB_API_KEY + "&with_genres=" + genreId + "&page=" + page;
        } else {
            url = BASE_URL + "/movie/popular?api_key=" + TMDB_API_KEY + "&page=" + page;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<MyMovieData> movies = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            movies.add(new MyMovieData(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.optString("release_date", "N/A"),
                                    obj.optString("poster_path", ""),
                                    obj.optDouble("vote_average", 0.0)
                            ));
                        }
                        
                        if (page == 1) {
                            movieAdapter.setMovies(movies);
                        } else {
                            movieAdapter.addMovies(movies);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Movie parse error", e);
                    } finally {
                        isLoading = false;
                    }
                }, error -> {
                    Log.e(TAG, "Movie fetch error", error);
                    isLoading = false;
                });
        requestQueue.add(request);
    }

    private void initBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        BottomNavHelper.setupBottomNavigation(this, bottomNavigationView, R.id.nav_movies);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            BottomNavHelper.updateSelectedItem(bottomNav, R.id.nav_movies);
        }
    }
}