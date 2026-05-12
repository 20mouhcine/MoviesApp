package com.example.moviesapp_latiris;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
public class VideoPlayer extends AppCompatActivity {
    private WebView webView;
    private String videoUrl;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Get the video URL from the intent
        videoUrl = getIntent().getStringExtra("videoUrl");

        // Initialize the WebView
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // Required for video playback
        webView.setWebViewClient(new WebViewClient());

        // Load the video URL in the WebView
        if (videoUrl != null) {
            webView.loadUrl(videoUrl);
        }
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Adjust the layout or configuration of your WebView here
        if (webView != null && videoUrl != null) {
            webView.loadUrl(videoUrl);
        }
    }
}
