# 🔥 Configuration Firebase + Google Auth - Guide Complet

## ✅ Configuration terminée!

Votre application est maintenant configurée pour l'authentification avec **Firebase** et **Google Auth**.

---

## 📋 Fichiers créés/modifiés

### ✨ Nouveau
- `FirebaseAuthManager.java` - Gestion Firebase Auth

### ✏️ Modifiés
- `RegisterActivity.java` - Utilise maintenant Firebase
- `app/build.gradle.kts` - Firebase dépendances
- `build.gradle.kts` - Google Services plugin
- `gradle/libs.versions.toml` - Firebase versions

---

## 🚀 3 étapes essentielles pour démarrer

### Étape 1: Créer un projet Firebase (10 min)

```
1. Allez sur https://firebase.google.com
2. Cliquez "Go to console"
3. Cliquez "Add project"
4. Entrez le nom: "MoviesApp"
5. Finalisez la création
```

### Étape 2: Configurer Google Sign-In dans Firebase (5 min)

```
1. Dans Firebase Console
2. Allez à "Authentication"
3. Cliquez sur "Get started"
4. Cliquez sur "Google"
5. Activez Google Sign-In
6. Ajoutez votre email de support
7. Sauvegardez
```

### Étape 3: Obtenir votre Client ID et configurer (10 min)

**Pour Web:**
```
1. Dans Firebase → Project Settings → Web Apps
2. Copiez votre Web Client ID
3. Ouvrez: app/src/main/java/.../FirebaseAuthManager.java
4. Remplacez: GOOGLE_WEB_CLIENT_ID = "your-..."
```

**Pour Android (télécharger google-services.json):**
```
1. Dans Firebase Console → Project Settings
2. Onglet "Apps" → Select Android
3. Entrez: com.example.moviesapp_latiris
4. Pour SHA1: générez-le avec la commande ci-dessous
5. Téléchargez google-services.json
6. Mettez-le dans: app/ (répertoire root de l'app)
```

**Générer SHA1 (Windows PowerShell):**
```powershell
$Env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
& "$Env:JAVA_HOME\bin\keytool" -list -v -keystore "$Env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**Générer SHA1 (Mac/Linux):**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

---

## 📁 Fichier google-services.json

**IMPORTANT**: Ce fichier doit être à la racine du dossier `app/`

```
MoviesApp_Latiris/
└── app/
    ├── google-services.json ← ICI!
    ├── build.gradle.kts
    ├── src/
    │   └── main/
    └── ...
```

**Vous trouverez google-services.json dans Firebase Console:**
1. Project Settings (⚙️ en haut à gauche)
2. Onglet "Apps"
3. Sélectionnez votre app Android
4. Bouton "Download google-services.json"

---

## 🎯 Classe FirebaseAuthManager

### Utilisation basique:

```java
// Initialiser au démarrage
FirebaseAuthManager.init(context);

// Enregistrement par email
FirebaseAuthManager.registerWithEmail(email, password, new FirebaseAuthManager.AuthCallback() {
    @Override
    public void onSuccess(String userId, String email, String displayName, String token) {
        // Succès!
    }
    
    @Override
    public void onError(String error) {
        // Erreur
    }
});

// Login par email
FirebaseAuthManager.loginWithEmail(email, password, callback);

// Google Sign-In (automatique dans RegisterActivity)
// Cliquez simplement le bouton "Google"

// Vérifier si connecté
if (FirebaseAuthManager.isSignedIn()) {
    // Utilisateur connecté
}

// Se déconnecter
FirebaseAuthManager.signOut(context);
```

---

## 🔧 Configuration du projet Firebase Console

### 1. Authentification

**Authentication Settings:**
```
1. Allez à Authentication → Settings
2. Vous verrez les providers disponibles
3. Google devrait être "Enabled"
4. Email/Password: À activer aussi (si besoin)
```

### 2. Ajouter votre domaine de redirection

**Authorization redirect URIs:**
```
1. Allez à Authentication → Settings → Authorized domains
2. Vous verrez: localhost (si local)
3. Pour production, ajoutez votre domaine
```

### 3. Activer Email/Password Auth (optionnel)

```
1. Authentication → Sign-in method
2. Cliquez sur "Email/Password"
3. Activez "Enable"
4. Choisissez: "Email/Password" ou "Email link"
5. Sauvegardez
```

---

## 📱 Tester l'application

### Test 1: Build sans erreur
```bash
Android Studio → Build → Make Project
Résultat attendu: ✓ Build successful
```

### Test 2: Sync gradle
```bash
File → Sync Now
Résultat attendu: ✓ Sync successful
```

### Test 3: Lancer l'app
```bash
Run → Run 'app'
Sélectionnez: Emulator ou Device
Résultat attendu: ✓ App démarre, RegisterActivity visible
```

### Test 4: Tester Google Sign-In
```
1. Cliquez sur le bouton "Google"
2. Sélectionnez un compte Google
3. Acceptez les permissions
Résultat attendu: ✓ Redirection vers MainActivity
```

### Test 5: Tester Email Registration
```
1. Remplissez le formulaire
2. Cliquez "Register"
Résultat attendu: ✓ Enregistrement réussi, vers MainActivity
```

---

## 📚 Architecture Firebase

```
┌─────────────────────────────────────────┐
│      RegisterActivity                    │
│  ├─ Email Registration                  │
│  └─ Google Sign-In                      │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│  FirebaseAuthManager             │
│  ├─ registerWithEmail()         │
│  ├─ loginWithEmail()            │
│  ├─ handleGoogleSignInResult()  │
│  └─ signOut()                   │
└──────────┬──────────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│  Firebase Auth                    │
│  ├─ Email/Password Auth         │
│  ├─ Google OAuth 2.0            │
│  └─ JWT Tokens                  │
└──────────┬──────────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│  SessionManager                  │
│  └─ Persist User Session        │
└──────────────────────────────────┘
```

---

## 🔐 Flux d'authentification

### Google Sign-In

```
User clicks "Google"
        ↓
GoogleSignInClient.getSignInIntent()
        ↓
Google Account Selection
        ↓
Get ID Token
        ↓
FirebaseAuthManager.handleGoogleSignInResult()
        ↓
Google AuthProvider.getCredential()
        ↓
FirebaseAuth.signInWithCredential()
        ↓
SessionManager.saveUserSession()
        ↓
MainActivity
```

### Email Registration

```
User fills form + clicks "Register"
        ↓
Validation checks
        ↓
FirebaseAuthManager.registerWithEmail()
        ↓
FirebaseAuth.createUserWithEmailAndPassword()
        ↓
onSuccess: Get FirebaseUser
        ↓
SessionManager.saveUserSession()
        ↓
MainActivity
```

---

## ✔️ Checklist de configuration

### Firebase Console
- [ ] Projet créé sur firebase.google.com
- [ ] Google Sign-In activé
- [ ] Email/Password provider activé (optionnel)
- [ ] App Android enregistrée
- [ ] SHA1 fingerprint fourni
- [ ] google-services.json téléchargé

### Android Project
- [ ] google-services.json dans le dossier app/
- [ ] FirebaseAuthManager.java créé
- [ ] RegisterActivity mis à jour
- [ ] app/build.gradle.kts mis à jour
- [ ] build.gradle.kts mis à jour
- [ ] GOOGLE_WEB_CLIENT_ID configuré

### Tests
- [ ] Build sans erreurs
- [ ] App démarre
- [ ] Google Sign-In fonctionne
- [ ] Email Registration fonctionne
- [ ] Redirection vers MainActivity réussie

---

## 🆘 Dépannage courant

| Erreur | Cause | Solution |
|--------|-------|----------|
| "google-services.json missing" | Fichier non placé | Mettez-le dans app/ |
| "FirebaseApp not initialized" | Pas d'appel à init() | Appelez FirebaseAuthManager.init(this) |
| "Invalid Client ID" | GOOGLE_WEB_CLIENT_ID incorrect | Vérifiez dans FirebaseAuthManager.java |
| "12501 error" | Google Play Services manquant | Testez sur device physique |
| "Cannot find symbol FirebaseAuth" | Dépendances manquantes | Sync gradle |

---

## 📖 Exemple d'utilisation complet

```java
// Dans RegisterActivity

// 1. Initialiser Firebase
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    
    // Initialize Firebase
    FirebaseAuthManager.init(this);
    
    // ... rest of code
}

// 2. Enregistrement par email
private void registerWithEmail() {
    String email = etEmail.getText().toString();
    String password = etPassword.getText().toString();
    
    FirebaseAuthManager.registerWithEmail(email, password, 
        new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(String userId, String email, String displayName, String token) {
                // Sauvegarder la session
                new SessionManager(RegisterActivity.this)
                    .saveUserSession(userId, email, displayName, null);
                
                // Aller à MainActivity
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
}

// 3. Google Sign-In (depuis le layout via onClick)
public void signInWithGoogle(android.view.View view) {
    Intent signInIntent = FirebaseAuthManager.getGoogleSignInClient().getSignInIntent();
    googleSignInLauncher.launch(signInIntent);
}

// 4. Vérifier si connecté au démarrage
@Override
protected void onStart() {
    super.onStart();
    
    if (FirebaseAuthManager.isSignedIn()) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

// 5. Déconnexion
private void logout() {
    FirebaseAuthManager.signOut(this);
    startActivity(new Intent(this, RegisterActivity.class));
    finish();
}
```

---

## 🎓 Prochaines étapes

### Court terme (1-2 heures)
1. [ ] Télécharger google-services.json depuis Firebase
2. [ ] Mettre google-services.json dans app/
3. [ ] Configurer GOOGLE_WEB_CLIENT_ID
4. [ ] Tester l'app

### Moyen terme (1-2 jours)
5. [ ] Créer LoginActivity
6. [ ] Ajouter Forgot Password
7. [ ] Créer ProfileActivity
8. [ ] Ajouter Firestore Users collection

### Long terme (1-2 semaines)
9. [ ] Implémenter email verification
10. [ ] Ajouter 2FA
11. [ ] Intégrer avec votre app (TMDB API)
12. [ ] Synchroniser les données utilisateur

---

## 🔗 Ressources utiles

- 📖 [Firebase Documentation](https://firebase.google.com/docs)
- 🔑 [Google Sign-In Android](https://developers.google.com/identity/sign-in/android)
- 🚀 [Firebase Android Guide](https://firebase.google.com/docs/android/setup)

---

**Configuration Firebase réussie!** 🎉

Prochaine étape: Télécharger google-services.json et tester l'application!

