package com.example.moviesapp_latiris;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private final LatLng casablancaLocation = new LatLng(33.5731, -7.5898);

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        initBottomNavigation();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casablancaLocation, 12f));
        showCasablancaCinemas();
        requestLocationPermission();
    }

    private void showCasablancaCinemas() {
        if (mMap == null) return;

        mMap.clear();

        // Hardcoded Cinema coordinates in Casablanca
        addCinemaMarker("Megarama Casablanca", 33.5951, -7.6669, "Corniche, Casablanca");
        addCinemaMarker("IMAX Morocco Mall", 33.5786, -7.7056, "Morocco Mall, Casablanca");
        addCinemaMarker("Cinema Rialto", 33.5916, -7.6158, "Rue Mohammed El Qori, Casablanca");
        addCinemaMarker("Cinema Lynx", 33.5855, -7.6225, "Avenue Mers Sultan, Casablanca");
        addCinemaMarker("Cinema Eden", 33.5878, -7.6186, "Casablanca");
        addCinemaMarker("Cinema Pathé,",33.54420793321039, -7.639870532618674,"Pathé Californie Casablanca");
    }

    private void addCinemaMarker(String name, double lat, double lng, String snippet) {
        BitmapDescriptor cinemaIcon = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_cinema);
        LatLng position = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(name)
                .snippet(snippet)
                .icon(cinemaIcon));

    }

    // ─── Location ────────────────────────────────────────────────────────────────

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ─── Bottom Navigation ───────────────────────────────────────────────────────

    private void initBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        BottomNavHelper.setupBottomNavigation(this, bottomNavigationView, R.id.nav_explore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            BottomNavHelper.updateSelectedItem(bottomNav, R.id.nav_explore);
        }
    }
}
