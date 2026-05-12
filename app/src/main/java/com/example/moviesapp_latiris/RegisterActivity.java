package com.example.moviesapp_latiris;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView txtLogin;
    private GoogleSignInClient googleSignInClient;
    
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        com.google.android.gms.tasks.Task<GoogleSignInAccount> task = 
                                com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        FirebaseAuthManager.handleGoogleSignInResult(task,
                            new FirebaseAuthManager.AuthCallback() {
                                @Override
                                public void onSuccess(String userId, String email, String displayName, String token) {
                                    handleGoogleSignInSuccess(userId, email, displayName);
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(RegisterActivity.this, "Sign-In failed: " + error,
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                    } catch (Exception e) {
                        Log.e(TAG, "Google Sign-In error", e);
                        Toast.makeText(this, "Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize Firebase and Google Sign-In
        FirebaseAuthManager.init(this);
        googleSignInClient = FirebaseAuthManager.getGoogleSignInClient();

        // Initialize views
        initViews();
        
        // Setup click listeners
        setupClickListeners();
    }
    
    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> registerWithEmail());
        txtLogin.setOnClickListener(v -> goToLogin());
    }
    
    private void registerWithEmail() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        
        // Validation
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            return;
        }
        
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return;
        }
        
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }
        
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        
        // Register with Firebase
        FirebaseAuthManager.registerWithEmail(email, password,
            new FirebaseAuthManager.AuthCallback() {
                @Override
                public void onSuccess(String userId, String userEmail, String displayName, String token) {
                    SessionManager sessionManager = new SessionManager(RegisterActivity.this);
                    sessionManager.saveUserSession(userId, userEmail, fullName, null);

                    Toast.makeText(RegisterActivity.this, "Registration successful!",
                        Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(RegisterActivity.this, "Error: " + error,
                        Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Registration failed: " + error);
                }
            });
    }
    
    public void signInWithGoogle(android.view.View view) {
        // Revoke access to force account selection UI
        googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }
    
    private void handleGoogleSignInSuccess(String userId, String email, String displayName) {
        Log.d(TAG, "Google Sign-In Success: " + email);
        
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.saveUserSession(userId, email, displayName, null);

        Toast.makeText(this, "Signed in as: " + displayName, Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    
    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        // Check if user is already signed in
        if (FirebaseAuthManager.isSignedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
