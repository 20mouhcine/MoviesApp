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

/**
 * LoginActivity - User login with email/password or Google Sign-In
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView txtRegister, txtForgotPassword;
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
                                    Toast.makeText(LoginActivity.this, "Sign-In failed: " + error,
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
        setContentView(R.layout.activity_login);
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
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> loginWithEmail());
        txtRegister.setOnClickListener(v -> goToRegister());
        txtForgotPassword.setOnClickListener(v -> goToForgotPassword());
    }

    /**
     * Login with email and password
     */
    private void loginWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validation
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

        // Disable button and show loading state
        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        // Login with Firebase
        FirebaseAuthManager.loginWithEmail(email, password,
            new FirebaseAuthManager.AuthCallback() {
                @Override
                public void onSuccess(String userId, String userEmail, String displayName, String token) {
                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                    sessionManager.saveUserSession(userId, userEmail, displayName, null);

                    Toast.makeText(LoginActivity.this, "Login successful!",
                        Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Sign In");
                    Toast.makeText(LoginActivity.this, "Error: " + error,
                        Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Login failed: " + error);
                }
            });
    }

    /**
     * Handle Google Sign-In
     */
    public void signInWithGoogle(android.view.View view) {
        // Revoke access to force account selection UI
        googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    /**
     * Handle Google Sign-In success
     */
    private void handleGoogleSignInSuccess(String userId, String email, String displayName) {
        Log.d(TAG, "Google Sign-In Success: " + email);

        SessionManager sessionManager = new SessionManager(this);
        sessionManager.saveUserSession(userId, email, displayName, null);

        Toast.makeText(this, "Signed in as: " + displayName, Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * Navigate to RegisterActivity
     */
    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * Handle forgot password (placeholder for future implementation)
     */
    private void goToForgotPassword() {
        Toast.makeText(this, "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show();
        // TODO: Implement forgot password functionality
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