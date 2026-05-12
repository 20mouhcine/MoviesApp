package com.example.moviesapp_latiris;

/**
 * EXEMPLES D'UTILISATION DE L'AUTHENTIFICATION
 * 
 * Ce fichier contient des exemples prêts à utiliser pour
 * intégrer l'authentification dans vos activités.
 */

/* ============================================================
   EXEMPLE 1: ENREGISTREMENT PAR EMAIL
   ============================================================
   
   Utilisez cet exemple dans RegisterActivity.registerWithEmail()
*/

/*
public class RegisterActivity extends AppCompatActivity {
    
    private void registerWithEmail() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        
        // Validations...
        
        // Appeler Supabase
        SupabaseAuthHelper.registerWithEmail(email, password, 
            new SupabaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(String userId, String userEmail, 
                                    String name, String accessToken) {
                    // 1. Sauvegarder la session
                    SessionManager sessionManager = new SessionManager(
                        RegisterActivity.this);
                    sessionManager.saveUserSession(userId, userEmail, 
                        fullName, accessToken);
                    
                    // 2. Afficher un message
                    Toast.makeText(RegisterActivity.this, 
                        "Registration successful!", 
                        Toast.LENGTH_SHORT).show();
                    
                    // 3. Naviguer vers MainActivity
                    startActivity(new Intent(RegisterActivity.this, 
                        MainActivity.class));
                    finish();
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(RegisterActivity.this, 
                        "Error: " + error, 
                        Toast.LENGTH_SHORT).show();
                    Log.e("RegisterActivity", "Registration failed: " + error);
                }
            });
    }
}
*/

/* ============================================================
   EXEMPLE 2: CONNEXION PAR EMAIL (LoginActivity)
   ============================================================
   
   À implémenter dans LoginActivity
*/

/*
public class LoginActivity extends AppCompatActivity {
    
    private TextInputEditText etEmail, etPassword;
    
    private void loginWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        
        // Validations...
        
        SupabaseAuthHelper.loginWithEmail(email, password, 
            new SupabaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(String userId, String userEmail, 
                                    String name, String accessToken) {
                    // Sauvegarder la session
                    new SessionManager(LoginActivity.this)
                        .saveUserSession(userId, userEmail, name, accessToken);
                    
                    Toast.makeText(LoginActivity.this, 
                        "Login successful!", 
                        Toast.LENGTH_SHORT).show();
                    
                    startActivity(new Intent(LoginActivity.this, 
                        MainActivity.class));
                    finish();
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(LoginActivity.this, 
                        "Invalid email or password", 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}
*/

/* ============================================================
   EXEMPLE 3: GOOGLE SIGN-IN (RegisterActivity)
   ============================================================
   
   Cet exemple est déjà intégré dans RegisterActivity
   Mais voici la version avec Supabase integration
*/

/*
private void handleGoogleSignInSuccess(GoogleSignInAccount account) {
    String email = account.getEmail();
    String fullName = account.getDisplayName();
    String idToken = account.getIdToken();
    
    // Appeler Supabase avec le token Google
    SupabaseAuthHelper.signInWithGoogleIdToken(idToken, 
        new SupabaseAuthHelper.AuthCallback() {
            @Override
            public void onSuccess(String userId, String userEmail, 
                                String name, String accessToken) {
                // Sauvegarder la session
                new SessionManager(RegisterActivity.this)
                    .saveUserSession(userId, userEmail, fullName, accessToken);
                
                Toast.makeText(RegisterActivity.this, 
                    "Signed in as: " + fullName, 
                    Toast.LENGTH_SHORT).show();
                
                startActivity(new Intent(RegisterActivity.this, 
                    MainActivity.class));
                finish();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, 
                    "Google Sign-In failed: " + error, 
                    Toast.LENGTH_SHORT).show();
            }
        });
}
*/

/* ============================================================
   EXEMPLE 4: VÉRIFIER LA SESSION AU DÉMARRAGE
   ============================================================
   
   À utiliser dans MainActivity.onCreate()
*/

/*
public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Vérifier la session
        SessionManager sessionManager = new SessionManager(this);
        
        if (!sessionManager.isLoggedIn()) {
            // Utilisateur non connecté, aller à RegisterActivity
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
            return;
        }
        
        // Utilisateur connecté, continuer
        String userEmail = sessionManager.getUserEmail();
        String userName = sessionManager.getUserName();
        
        // Utiliser les données utilisateur...
        Log.d("MainActivity", "User: " + userEmail);
        
        // Reste du code...
    }
}
*/

/* ============================================================
   EXEMPLE 5: BOUTON DE DÉCONNEXION
   ============================================================
   
   À ajouter dans votre menu ou settings
*/

/*
// Dans MainActivity ou SettingsActivity
private void logout() {
    // 1. Déconnecter de Google
    SupabaseManager.signOutGoogle();
    
    // 2. Effacer la session locale
    new SessionManager(this).clearSession();
    
    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    
    // 3. Naviguer vers RegisterActivity
    startActivity(new Intent(this, RegisterActivity.class));
    finish();
}
*/

/* ============================================================
   EXEMPLE 6: AFFICHER LES INFOS UTILISATEUR
   ============================================================
   
   À utiliser dans un ProfileActivity ou settings
*/

/*
public class ProfileActivity extends AppCompatActivity {
    
    private TextView tvUserEmail, tvUserName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserName = findViewById(R.id.tvUserName);
        
        SessionManager sessionManager = new SessionManager(this);
        
        // Afficher les infos
        String email = sessionManager.getUserEmail();
        String name = sessionManager.getUserName();
        
        if (email != null) {
            tvUserEmail.setText("Email: " + email);
        }
        
        if (name != null) {
            tvUserName.setText("Name: " + name);
        }
    }
}
*/

/* ============================================================
   EXEMPLE 7: UTILISER LE TOKEN POUR LES REQUÊTES API
   ============================================================
   
   Envoyer le token avec vos requêtes Volley/Retrofit
*/

/*
private void makeAuthenticatedRequest() {
    SessionManager sessionManager = new SessionManager(this);
    String accessToken = sessionManager.getAuthToken();
    
    JsonObjectRequest request = new JsonObjectRequest(
        Request.Method.GET,
        "https://api.example.com/user",
        null,
        response -> {
            // Succès
        },
        error -> {
            // Erreur
        }
    ) {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            headers.put("Content-Type", "application/json");
            return headers;
        }
    };
    
    Volley.newRequestQueue(this).add(request);
}
*/

/* ============================================================
   EXEMPLE 8: SPLASH SCREEN AVEC VÉRIFICATION SESSION
   ============================================================
   
   Créer SplashActivity.java
*/

/*
public class SplashActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Vérifier après 2 secondes
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);
            
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                // Aller à MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Aller à RegisterActivity
                intent = new Intent(SplashActivity.this, RegisterActivity.class);
            }
            
            startActivity(intent);
            finish();
        }, 2000);
    }
}

// Ajouter dans AndroidManifest.xml:
// <activity android:name=".SplashActivity" android:exported="true">
//     <intent-filter>
//         <action android:name="android.intent.action.MAIN" />
//         <category android:name="android.intent.category.LAUNCHER" />
//     </intent-filter>
// </activity>
*/

/* ============================================================
   CONSTANTES UTILES
   ============================================================
*/

/*
// Codes d'erreur courants
public class AuthErrorCodes {
    public static final String INVALID_EMAIL = "Invalid email format";
    public static final String WEAK_PASSWORD = "Password must be at least 6 characters";
    public static final String PASSWORD_MISMATCH = "Passwords do not match";
    public static final String EMAIL_EXISTS = "Email already registered";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String NETWORK_ERROR = "Network connection failed";
}
*/

/* ============================================================
   NOTES IMPORTANTES
   ============================================================
   
   1. Les mots de passe ne doivent JAMAIS être loggés
   2. Les tokens doivent être stockés de façon sécurisée
   3. Toujours valider l'entrée utilisateur
   4. Utiliser HTTPS pour les requêtes
   5. Implémenter le refresh token logic
   6. Ajouter la gestion des erreurs réseau
   7. Utiliser proGuard/R8 pour la sécurité en production
   
   ============================================================
*/

