// ============================================================
// CONFIGURATION SUPABASE + GOOGLE AUTH
// ============================================================
// 
// IMPORTANTE: Remplacez les valeurs de TODO ci-dessous
//
// ============================================================

// ============================================================
// 1. SUPABASE CREDENTIALS
// ============================================================
// 
// Trouvez ces valeurs dans:
// Supabase Dashboard → Settings → API
//
// SUPABASE_URL:
// Location: SupabaseManager.java (ligne ~20)
// Format: https://[PROJECT_ID].supabase.co
// Exemple: https://abcdefgh123456.supabase.co
//
// SUPABASE_ANON_KEY:
// Location: SupabaseManager.java (ligne ~21)
// Format: Long string starting with 'eyJ...'
// Longueur: ~100+ caractères

// ============================================================
// 2. GOOGLE AUTH CREDENTIALS
// ============================================================
//
// Trouvez ces valeurs dans:
// Google Cloud Console → APIs & Services → Credentials
//
// GOOGLE_WEB_CLIENT_ID:
// Location: SupabaseManager.java (ligne ~24)
// Format: [NUMERIC_ID].apps.googleusercontent.com
// Exemple: 123456789-abcdefghijklmnopqrstuvwxyz.apps.googleusercontent.com
//
// Étapes pour obtenir le Client ID Web:
// 1. Allez à Google Cloud Console
// 2. Créez un nouveau projet
// 3. Activez Google+ API
// 4. Allez à Credentials → Create OAuth 2.0 Client ID
// 5. Sélectionnez "Web application"
// 6. Ajoutez l'URL de rappel Supabase:
//    https://[PROJECT_ID].supabase.co/auth/v1/callback
// 7. Copiez le Client ID généré

// ============================================================
// 3. ANDROID SHA-1 FINGERPRINT
// ============================================================
//
// Pour créer le Client ID Android dans Google Cloud Console,
// vous aurez besoin du SHA-1 fingerprint de votre debug.keystore
//
// Générez-le avec:
//
// Windows (PowerShell):
// $Env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
// & "$Env:JAVA_HOME\bin\keytool" -list -v -keystore "$Env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
//
// Mac/Linux (Terminal):
// keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
//
// Cherchez: "SHA1: XX:XX:XX:XX:..."

// ============================================================
// 4. FICHIERS À MODIFIER
// ============================================================
//
// 1. app/src/main/java/com/example/moviesapp_latiris/SupabaseManager.java
//    - Ligne 20: Remplacez SUPABASE_URL
//    - Ligne 21: Remplacez SUPABASE_ANON_KEY
//    - Ligne 24: Remplacez GOOGLE_WEB_CLIENT_ID
//
// 2. app/build.gradle.kts
//    - Les dépendances sont déjà ajoutées ✓
//
// 3. AndroidManifest.xml
//    - Les permissions sont déjà ajoutées ✓

// ============================================================
// 5. VÉRIFICATION POST-CONFIGURATION
// ============================================================
//
// ✓ Étape 1: Ouvrez SupabaseManager.java
// ✓ Étape 2: Remplacez les 3 constantes TODO
// ✓ Étape 3: Lancez l'app dans un émulateur avec Google Play Services
// ✓ Étape 4: Testez Google Sign-In
// ✓ Étape 5: Vérifiez les logs: "Google Sign-In successful"

// ============================================================
// 6. STRUCTURE DE L'APPLICATION
// ============================================================
//
// ├── RegisterActivity
// │   ├── Email Registration (validation, TODO: Supabase integration)
// │   └── Google Sign-In (implémenté ✓)
// │
// ├── SupabaseManager (Configuration centralisée)
// │   ├── initGoogleSignIn()
// │   ├── handleGoogleSignInResult()
// │   └── signOutGoogle()
// │
// ├── SessionManager (Gestion de session)
// │   ├── saveUserSession()
// │   ├── isLoggedIn()
// │   └── clearSession()
// │
// └── MainActivity (Après connexion réussie)

// ============================================================
// 7. PROCHAINES ÉTAPES RECOMMANDÉES
// ============================================================
//
// 1. Créer LoginActivity pour les utilisateurs existants
// 2. Intégrer Supabase Auth pour l'enregistrement par email
// 3. Ajouter la persistance du token JWT
// 4. Créer une table User dans Supabase
// 5. Implémenter la vérification d'email
// 6. Ajouter les fonctionnalités de profil utilisateur

// ============================================================
// 8. RESSOURCES UTILES
// ============================================================
//
// Supabase Android: https://supabase.com/docs/guides/auth
// Google Sign-In: https://developers.google.com/identity/sign-in/android
// Documentation complète: Voir SUPABASE_AUTH_SETUP.md

// ============================================================

