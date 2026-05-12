# Configuration Guide: Supabase + Google Auth

Ce guide explique comment configurer l'authentification avec Supabase et Google Auth dans votre application Android.

## 📋 Prérequis

- Compte Supabase (https://supabase.com)
- Compte Google Cloud Console (https://console.cloud.google.com)
- Android Studio avec le SDK Android configuré

## 🔧 Configuration Supabase

### Étape 1: Créer un projet Supabase

1. Allez sur https://supabase.com et connectez-vous
2. Cliquez sur "New project"
3. Remplissez les détails du projet
4. Une fois créé, allez à **Settings → API**
5. Copiez:
   - **Project URL** → `SUPABASE_URL`
   - **anon public** key → `SUPABASE_ANON_KEY`

### Étape 2: Activer Google Auth dans Supabase

1. Dans le dashboard Supabase, allez à **Authentication → Providers**
2. Cliquez sur **Google**
3. Activez Google
4. Vous obtiendrez un **Callback URL** qui ressemble à:
   ```
   https://your-project.supabase.co/auth/v1/callback
   ```
5. Gardez ce URL pour la configuration Google Cloud

## 🔑 Configuration Google Cloud Console

### Étape 1: Créer un projet Google Cloud

1. Allez sur https://console.cloud.google.com
2. Créez un nouveau projet
3. Activez **Google+ API**:
   - Allez à **APIs & Services → Library**
   - Recherchez "Google+ API"
   - Cliquez sur **Enable**

### Étape 2: Créer les identifiants OAuth

1. Allez à **APIs & Services → Credentials**
2. Cliquez sur **+ Create Credentials → OAuth client ID**
3. Si demandé, configurez l'écran de consentement OAuth d'abord:
   - Type d'utilisateur: **External**
   - Remplissez les informations requises
   - Ajoutez les scopes: `email`, `profile`

### Étape 3: Créer le Client ID Web

1. Type d'application: **Web application**
2. Noms autorisés: `http://localhost:3000`
3. URIs de redirection autorisés:
   ```
   https://your-project.supabase.co/auth/v1/callback
   ```
4. Cliquez sur **Create**
5. Copiez le **Client ID** → remplacez `GOOGLE_WEB_CLIENT_ID` dans `SupabaseManager.java`

### Étape 4: Créer le Client ID Android

1. Retournez à **Credentials**
2. Cliquez sur **+ Create Credentials → OAuth client ID**
3. Type d'application: **Android**
4. Pour obtenir votre **SHA-1 fingerprint**:

   Sur Windows (PowerShell):
   ```powershell
   $Env:JAVA_HOME = "C:\Program Files\Java\jdk-11"  # Ajustez le chemin si nécessaire
   & "$Env:JAVA_HOME\bin\keytool" -list -v -keystore "$Env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```

   Sur Mac/Linux:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```

5. Collez le SHA-1 fingerprint dans le formulaire
6. Entrez votre Package Name: `com.example.moviesapp_latiris`
7. Cliquez sur **Create**

## 📝 Mise à jour du code

Ouvrez le fichier `SupabaseManager.java` et remplacez:

```java
public static final String SUPABASE_URL = "https://your-project.supabase.co";
public static final String SUPABASE_ANON_KEY = "your-anon-key";
public static final String GOOGLE_WEB_CLIENT_ID = "your-web-client-id.apps.googleusercontent.com";
```

Par vos vraies valeurs obtenues précédemment.

## 🧪 Test de l'authentification

1. Lancez l'application dans un émulateur ou sur un appareil physique
2. Allez à RegisterActivity
3. Testez:
   - ✅ Inscription par email (validations)
   - ✅ Google Sign-In
   - ✅ Navigation vers MainActivity après connexion réussie

## 📱 Architecture de l'authentification

```
RegisterActivity
    ↓
    ├─→ Email Registration
    │   ├─ Validation
    │   └─ [TODO] Supabase Auth
    │
    └─→ Google Sign-In
        ├─ SupabaseManager.initGoogleSignIn()
        ├─ signInWithGoogle()
        └─ handleGoogleSignInSuccess()
```

## 🔐 Prochaines étapes

1. **Implémenter la persistance de session** - Sauvegarder le token JWT
2. **Créer une table User** dans Supabase pour stocker les données utilisateur
3. **Implémenter LoginActivity** pour les utilisateurs existants
4. **Ajouter la vérification d'email** pour la sécurité
5. **Gérer l'expiration du token** et le refresh

## 🚨 Erreurs courantes

| Erreur | Solution |
|--------|----------|
| `GoogleSignInClient not initialized` | Appelez `SupabaseManager.initGoogleSignIn()` dans onCreate |
| `StatusCode: 12501` | Google Play Services n'est pas disponible sur l'émulateur |
| `Callback URL mismatch` | Vérifiez que l'URL de rappel dans Google Cloud correspond à Supabase |
| `SHA-1 mismatch` | Régénérez le Client ID Android avec le bon SHA-1 |

## 📚 Ressources utiles

- [Documentation Supabase Android](https://supabase.com/docs/guides/auth/social-login/auth-google)
- [Google Sign-In pour Android](https://developers.google.com/identity/sign-in/android)
- [Supabase Auth API](https://supabase.com/docs/reference/auth)

