package com.example.moviesapp_latiris;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MovieDetailActivity";
    private static final String TMDB_API_KEY = "e21c9bd08ef733416fa4adc42dad2a14";
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private final LatLng casablancaLocation = new LatLng(33.5731, -7.5898);

    private TextView movieNameTextView;
    private TextView movieDescriptionTextView;
    private ImageView moviePosterImageView;
    private RatingBar movieDetailRating;
    private TextView movieDetailRatingScore;

    private Button playButton;
    private Button favoriteButton;

    private GoogleMap mMap;

    private RequestQueue requestQueue;

    private String trailerKey = "";
    private String posterPath = "";
    private String releaseDate = "";
    private String movieName = "";
    private double movieRating = 0.0;

    private int currentMovieId = -1;

    private FavoritesManager favoritesManager;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        favoritesManager = new FavoritesManager();

        initViews();

        requestQueue = Volley.newRequestQueue(this);

        int movieId = getIntent().getIntExtra("movieId", -1);
        String movieNameExtra = getIntent().getStringExtra("movieName");
        String movieDateExtra = getIntent().getStringExtra("movieDate");
        String movieImageExtra = getIntent().getStringExtra("movieImage");
        double movieRatingExtra = getIntent().getDoubleExtra("movieRating", 0.0);

        if (movieNameExtra != null && !movieNameExtra.isEmpty()) {
            movieName = movieNameExtra;
            movieNameTextView.setText(movieNameExtra);
        }
        if (movieDateExtra != null && !movieDateExtra.isEmpty()) {
            releaseDate = movieDateExtra;
        }
        if (movieImageExtra != null && !movieImageExtra.isEmpty()) {
            posterPath = movieImageExtra;
            Glide.with(this)
                    .load(IMAGE_BASE_URL + movieImageExtra)
                    .into(moviePosterImageView);
        }
        if (movieRatingExtra > 0.0) {
            movieRating = movieRatingExtra;
            float ratingValue = (float) (movieRatingExtra / 2.0);
            movieDetailRating.setRating(Math.min(5, ratingValue));
            movieDetailRatingScore.setText(String.format("%.1f/10", movieRatingExtra));
        }

        if (movieId != -1) {
            currentMovieId = movieId;
            fetchMovieDetails(movieId);
        } else {
            movieDescriptionTextView.setText(R.string.no_movie_id);
        }

        initMap();
        initBottomNavigation();
    }

    private void initViews() {

        movieNameTextView = findViewById(R.id.textName);
        movieDescriptionTextView = findViewById(R.id.Details);
        moviePosterImageView = findViewById(R.id.imageview);
        movieDetailRating = findViewById(R.id.movieDetailRating);
        movieDetailRatingScore = findViewById(R.id.movieDetailRatingScore);

        playButton = findViewById(R.id.playButton);
        favoriteButton = findViewById(R.id.favoriteButton);

        playButton.setOnClickListener(v -> playTrailer());

        favoriteButton.setOnClickListener(v -> toggleFavorite());
    }

    private void initMap() {

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initBottomNavigation() {

        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_movies);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(new Intent(this, MainActivity.class));

                return true;

            } else if (id == R.id.nav_explore) {

                startActivity(new Intent(this, MapActivity.class));

                return true;

            } else if (id == R.id.nav_movies) {

                startActivity(new Intent(this, MoviesActivity.class));

                return true;

            } else if (id == R.id.nav_favoris) {

                startActivity(new Intent(this, FavorisActivity.class));

                return true;

            } else if (id == R.id.nav_profile) {

                startActivity(new Intent(this, ProfileActivity.class));

                return true;
            }

            return false;
        });
    }

    private void fetchMovieDetails(int movieId) {

        String detailsUrl =
                TMDB_BASE_URL + movieId + "?api_key=" + TMDB_API_KEY;

        String videosUrl =
                TMDB_BASE_URL + movieId + "/videos?api_key=" + TMDB_API_KEY;

        JsonObjectRequest detailsRequest =
                new JsonObjectRequest(
                        Request.Method.GET,
                        detailsUrl,
                        null,
                        this::handleMovieDetailsResponse,
                        error -> {

                            Log.e(TAG,
                                    "Error fetching movie details: "
                                            + error.getMessage());

                            movieDescriptionTextView.setText(
                                    R.string.failed_to_fetch_details
                            );
                        });

        JsonObjectRequest videosRequest =
                new JsonObjectRequest(
                        Request.Method.GET,
                        videosUrl,
                        null,
                        this::handleMovieVideosResponse,
                        error -> Log.e(TAG,
                                "Error fetching movie videos: "
                                        + error.getMessage())
                );

        requestQueue.add(detailsRequest);
        requestQueue.add(videosRequest);
    }

    private void handleMovieDetailsResponse(JSONObject response) {

        try {

            movieName = response.optString("title", "Unknown");

            posterPath = response.optString("poster_path", "");

            releaseDate = response.optString("release_date", "Unknown");
            
            movieRating = response.optDouble("vote_average", 0.0);

            String overview =
                    response.optString("overview", "No description");

            movieNameTextView.setText(movieName);

            movieDescriptionTextView.setText(overview);
            
            // Set rating
            float ratingValue = (float) (movieRating / 2.0); // Convert from 10-point to 5-point scale
            movieDetailRating.setRating(Math.min(5, ratingValue));
            movieDetailRatingScore.setText(String.format("%.1f/10", movieRating));

            Glide.with(this)
                    .load(IMAGE_BASE_URL + posterPath)
                    .into(moviePosterImageView);

            checkAndUpdateFavoriteButton();

        } catch (Exception e) {

            Log.e(TAG,
                    "Error parsing movie details: "
                            + e.getMessage());
        }
    }

    private void handleMovieVideosResponse(JSONObject response) {

        try {

            if (!response.has("results")) return;

            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                JSONObject video = results.getJSONObject(i);

                if ("Trailer".equals(video.getString("type"))) {

                    trailerKey = video.getString("key");

                    break;
                }
            }

        } catch (JSONException e) {

            Log.e(TAG,
                    "Error parsing movie videos: "
                            + e.getMessage());
        }
    }

    private void playTrailer() {

        if (trailerKey != null && !trailerKey.isEmpty()) {

            Intent intent = new Intent(this, VideoPlayer.class);

            intent.putExtra(
                    "videoUrl",
                    "https://www.youtube.com/embed/" + trailerKey
            );

            startActivity(intent);

        } else {

            Toast.makeText(
                    this,
                    R.string.error_trailer_unavailable,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        casablancaLocation,
                        12f
                )
        );

        showCasablancaCinemas();

        requestLocationPermission();
    }

    private void showCasablancaCinemas() {

        if (mMap == null) return;

        mMap.clear();

        addCinemaMarker(
                "Megarama Casablanca",
                33.5951,
                -7.6669,
                "Corniche, Casablanca"
        );

        addCinemaMarker(
                "IMAX Morocco Mall",
                33.5786,
                -7.7056,
                "Morocco Mall, Casablanca"
        );

        addCinemaMarker(
                "Cinema Rialto",
                33.5916,
                -7.6158,
                "Rue Mohammed El Qori"
        );
    }

    private void addCinemaMarker(
            String name,
            double lat,
            double lng,
            String snippet
    ) {

        BitmapDescriptor cinemaIcon =
                BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_cinema
                );

        LatLng position = new LatLng(lat, lng);

        mMap.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title(name)
                        .snippet(snippet)
                        .icon(cinemaIcon)
        );
    }

    private void requestLocationPermission() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );

            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {

                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    private void checkAndUpdateFavoriteButton() {

        if (currentMovieId == -1) return;

        favoritesManager.isFavorite(currentMovieId, isFav -> {

            isFavorite = isFav;

            updateFavoriteButtonUI();
        });
    }

    private void updateFavoriteButtonUI() {

        if (isFavorite) {

            favoriteButton.setText("★ Remove from Favorites");

            favoriteButton.setBackgroundColor(
                    Color.parseColor("#FFC107")
            );

            favoriteButton.setTextColor(Color.BLACK);

        } else {

            favoriteButton.setText("☆ Add to Favorites");

            favoriteButton.setBackgroundColor(
                    Color.parseColor("#666666")
            );

            favoriteButton.setTextColor(Color.WHITE);
        }
    }

    private void toggleFavorite() {

        if (currentMovieId == -1) {

            Toast.makeText(
                    this,
                    "Movie information not available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String movieDate = releaseDate;
        String movieImage = posterPath;

        if (isFavorite) {

            favoritesManager.removeFromFavorites(
                    currentMovieId,
                    new FavoritesManager.FavoriteCallback() {

                        @Override
                        public void onSuccess(String message) {

                            isFavorite = false;

                            updateFavoriteButtonUI();

                            Toast.makeText(
                                    MovieDetailActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onError(String error) {

                            Toast.makeText(
                                    MovieDetailActivity.this,
                                    error,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

        } else {

            favoritesManager.addToFavorites(
                    currentMovieId,
                    movieName,
                    movieDate,
                    movieImage,
                    movieRating,
                    new FavoritesManager.FavoriteCallback() {

                        @Override
                        public void onSuccess(String message) {

                            isFavorite = true;

                            updateFavoriteButtonUI();

                            Toast.makeText(
                                    MovieDetailActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onError(String error) {

                            Toast.makeText(
                                    MovieDetailActivity.this,
                                    error,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        }
    }
}
