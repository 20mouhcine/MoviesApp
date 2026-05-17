package com.example.moviesapp_latiris;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExperimentalGetImage
public class MoodDetectionActivity extends AppCompatActivity {
    public static final String EXTRA_GENRE_ID = "extra_genre_id";
    public static final String EXTRA_MOOD_LABEL = "extra_mood_label";

    private static final String TAG = "MoodDetection";
    private static final int GENRE_COMEDY = 35;
    private static final int GENRE_DRAMA = 18;
    private static final int NEUTRAL_GENRE = -1;
    private static final long TIMEOUT_MS = 4000;
    private static final long CAMERA_START_DELAY_MS = 600;
    private static final long FACE_STABLE_MS = 1200;

    private PreviewView previewView;
    private TextView moodStatusText;
    private Button cancelButton;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private FaceDetector faceDetector;
    private ExecutorService cameraExecutor;
    private boolean moodResolved = false;
    private Handler mainHandler;
    private Runnable timeoutRunnable;
    private long firstFaceSeenAtMs = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_detection);

        previewView = findViewById(R.id.previewView);
        moodStatusText = findViewById(R.id.moodStatusText);
        cancelButton = findViewById(R.id.cancelButton);

        mainHandler = new Handler(Looper.getMainLooper());
        cameraExecutor = Executors.newSingleThreadExecutor();

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        faceDetector = FaceDetection.getClient(options);

        cancelButton.setOnClickListener(v -> finishWithMood("Neutral", NEUTRAL_GENRE));

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                        finishWithMood("Neutral", NEUTRAL_GENRE);
                    }
                });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mainHandler.postDelayed(this::startCamera, CAMERA_START_DELAY_MS);
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        timeoutRunnable = () -> finishWithMood("Neutral", NEUTRAL_GENRE);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

                // Start timeout only after camera is bound and preview is ready.
                mainHandler.removeCallbacks(timeoutRunnable);
                mainHandler.postDelayed(timeoutRunnable, TIMEOUT_MS);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera start failed", e);
                finishWithMood("Neutral", NEUTRAL_GENRE);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @ExperimentalGetImage
    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        if (moodResolved) {
            imageProxy.close();
            return;
        }

        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        faceDetector.process(image)
                .addOnSuccessListener(faces -> handleFaces(faces))
                .addOnFailureListener(e -> Log.w(TAG, "Face detection failed", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void handleFaces(List<Face> faces) {
        if (moodResolved || faces == null || faces.isEmpty()) {
            return;
        }

        long nowMs = android.os.SystemClock.elapsedRealtime();
        if (firstFaceSeenAtMs == 0L) {
            firstFaceSeenAtMs = nowMs;
            moodStatusText.setText("Hold still...");
            return;
        }

        if (nowMs - firstFaceSeenAtMs < FACE_STABLE_MS) {
            return;
        }

        Face face = faces.get(0);
        Float smileProbability = face.getSmilingProbability();
        if (smileProbability == null) {
            return;
        }

        if (smileProbability >= 0.6f) {
            finishWithMood("Happy", GENRE_COMEDY);
        } else if (smileProbability <= 0.2f) {
            finishWithMood("Sad", GENRE_DRAMA);
        } else {
            finishWithMood("Neutral", NEUTRAL_GENRE);
        }
    }

    private void finishWithMood(String label, int genreId) {
        if (moodResolved) {
            return;
        }
        moodResolved = true;
        mainHandler.removeCallbacks(timeoutRunnable);
        moodStatusText.setText("Mood: " + label);

        Intent result = new Intent();
        result.putExtra(EXTRA_MOOD_LABEL, label);
        result.putExtra(EXTRA_GENRE_ID, genreId);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceDetector != null) {
            faceDetector.close();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (mainHandler != null && timeoutRunnable != null) {
            mainHandler.removeCallbacks(timeoutRunnable);
        }
    }
}
