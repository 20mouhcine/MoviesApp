# 🎬 Configuration de l'Authentification - MoviesApp

## ✅ Ce qui a été fait

### 1. **Dépendances ajoutées** ✓
   - Supabase Kotlin SDK
   - Google Play Services Auth

### 2. **Classes créées** ✓
   - `SupabaseManager.java` - Gestion centralisée de Google Sign-In
   - `SupabaseAuthHelper.java` - Appels API Supabase pour l'authentification
   - `SessionManager.java` - Gestion de la session utilisateur

### 3. **RegisterActivity mise à jour** ✓
   - ✅ Validation des champs
   - ✅ Google Sign-In intégré
   - ✅ Navigation automatique après connexion
   - ✅ Gestion des erreurs

### 4. **Layout activity_register.xml mis à jour** ✓
   - ✅ Bouton Google Sign-In ajouté
   - ✅ Texte "Or sign up with" ajouté

### 5. **AndroidManifest.xml mis à jour** ✓
   - ✅ Permission `GET_ACCOUNTS` ajoutée

---

## 🔧 Configuration requise

### Étape 1: Supabase
1. Allez sur https://supabase.com
2. Créez un nouveau projet
3. Dans **Settings → API**, copiez:
   - **Project URL** → Remplacez `SUPABASE_URL` dans `SupabaseManager.java`
   - **anon public key** → Remplacez `SUPABASE_ANON_KEY` dans `SupabaseManager.java`

### Étape 2: Google Cloud Console
1. Allez sur https://console.cloud.google.com
2. Créez un nouveau projet
3. Activez **Google+ API**
4. Allez à **APIs & Services → Credentials**
5. Créez un **OAuth 2.0 Client ID** pour Web
6. Copiez le Client ID → Remplacez `GOOGLE_WEB_CLIENT_ID` dans `SupabaseManager.java`
7. Dans la redirection, ajoutez: `https://your-project.supabase.co/auth/v1/callback`

### Étape 3: Code
Ouvrez `app/src/main/java/com/example/moviesapp_latiris/SupabaseManager.java`:

```java
public static final String SUPABASE_URL = "https://your-project.supabase.co";  // ← Remplacez
public static final String SUPABASE_ANON_KEY = "your-anon-key";               // ← Remplacez
public static final String GOOGLE_WEB_CLIENT_ID = "your-web-client-id.apps.googleusercontent.com"; // ← Remplacez
```

---

## 🧪 Test de l'application

### Sur émulateur
1. Assurez-vous que **Google Play Services** est installé
2. Lancez l'app
3. Cliquez sur "Google" pour tester Google Sign-In

### Sur appareil physique
1. L'appareil doit avoir Google Play Services
2. Lancez l'app
3. Testez Google Sign-In

---

## 📋 Fichiers créés/modifiés

| Fichier | Statut | Description |
|---------|--------|-------------|
| `SupabaseManager.java` | ✨ Créé | Configuration Google Sign-In |
| `SupabaseAuthHelper.java` | ✨ Créé | Appels API Supabase |
| `SessionManager.java` | ✨ Créé | Gestion de session |
| `RegisterActivity.java` | ✏️ Modifié | Authentification intégrée |
| `activity_register.xml` | ✏️ Modifié | Bouton Google Sign-In |
| `app/build.gradle.kts` | ✏️ Modifié | Dépendances ajoutées |
| `AndroidManifest.xml` | ✏️ Modifié | Permissions ajoutées |

---

## 🚀 Utilisation dans votre code

### Exemple 1: Enregistrement par email
```java
SupabaseAuthHelper.registerWithEmail(email, password, new SupabaseAuthHelper.AuthCallback() {
    @Override
    public void onSuccess(String userId, String email, String name, String accessToken) {
        // Sauvegarder la session
        new SessionManager(context).saveUserSession(userId, email, name, accessToken);
        // Naviguer vers MainActivity
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
    }
});
```

### Exemple 2: Connexion par email
```java
SupabaseAuthHelper.loginWithEmail(email, password, new SupabaseAuthHelper.AuthCallback() {
    @Override
    public void onSuccess(String userId, String email, String name, String accessToken) {
        new SessionManager(context).saveUserSession(userId, email, name, accessToken);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
    }
});
```

### Exemple 3: Google Sign-In via Token ID
```java
// Dans handleGoogleSignInSuccess() de RegisterActivity
GoogleSignInAccount account = ...;
String idToken = account.getIdToken();

SupabaseAuthHelper.signInWithGoogleIdToken(idToken, new SupabaseAuthHelper.AuthCallback() {
    @Override
    public void onSuccess(String userId, String email, String name, String accessToken) {
        new SessionManager(context).saveUserSession(userId, email, name, accessToken);
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
    }
});
```

---

## 🔐 Gestion de la session

```java
// Créer une instance
SessionManager sessionManager = new SessionManager(this);

// Sauvegarder après connexion
sessionManager.saveUserSession(userId, email, name, accessToken);

// Récupérer les données
String userEmail = sessionManager.getUserEmail();
String authToken = sessionManager.getAuthToken();

// Vérifier si connecté
if (sessionManager.isLoggedIn()) {
    // Utilisateur connecté
}

// Déconnecter
sessionManager.clearSession();
```

---

## 📚 Architecture

```
┌─────────────────────────────────────────┐
│      RegisterActivity / LoginActivity    │
│  - Formulaires d'authentification        │
│  - Google Sign-In                        │
└─────────────────────────────────────────┘
            │
            ↓
┌─────────────────────────────────────────┐
│       SupabaseAuthHelper                 │
│  - registerWithEmail()                   │
│  - loginWithEmail()                      │
│  - signInWithGoogleIdToken()            │
└─────────────────────────────────────────┘
            │
            ↓
┌─────────────────────────────────────────┐
│      SupabaseManager                    │
│  - Gestion Google Sign-In               │
│  - Constantes de configuration          │
└─────────────────────────────────────────┘
            │
            ↓
┌─────────────────────────────────────────┐
│       SessionManager                     │
│  - Persistance de session                │
│  - SharedPreferences                     │
└─────────────────────────────────────────┘
            │
            ↓
┌─────────────────────────────────────────┐
│       Supabase Backend                   │
│  - Authentification                      │
│  - Base de données                       │
│  - Stockage                              │
└─────────────────────────────────────────┘
```

---

## 🎯 Prochaines étapes recommandées

1. **Créer LoginActivity**
   ```bash
   Fichier: LoginActivity.java
   ```

2. **Ajouter un splash screen**
   ```bash
   Vérifier la session au démarrage
   ```

3. **Créer une table User dans Supabase**
   ```sql
   CREATE TABLE users (
       id UUID PRIMARY KEY,
       email VARCHAR(255) UNIQUE,
       full_name VARCHAR(255),
       avatar_url VARCHAR(500),
       created_at TIMESTAMP DEFAULT NOW()
   );
   ```

4. **Implémenter la vérification d'email**

5. **Ajouter un profil utilisateur**

6. **Intégrer avec vos APIs existantes (TMDB)**

---

## 🐛 Dépannage

| Problème | Solution |
|----------|----------|
| "GoogleSignInClient not initialized" | Appelez `SupabaseManager.initGoogleSignIn()` |
| Google Sign-In ne fonctionne pas | Vérifiez le SHA-1 fingerprint et les configurations |
| Erreur 12501 | Assurez-vous que Google Play Services est installé |
| "Callback URL mismatch" | Vérifiez les URLs de rappel dans Google Cloud et Supabase |

---

## 📞 Support

Pour plus d'aide:
- 📖 [Documentation Supabase](https://supabase.com/docs)
- 🔑 [Google Sign-In Docs](https://developers.google.com/identity/sign-in/android)
- 🚀 [Android Auth Best Practices](https://developer.android.com/training/sign-in)

---

**Configuration complète!** 🎉

Passez à la prochaine étape: Implémenter LoginActivity et la persistance de session.

