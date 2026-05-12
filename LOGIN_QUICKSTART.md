# 🚀 DÉMARRAGE RAPIDE - LOGIN + FIREBASE

## ⚡ 5 étapes pour commencer maintenant!

### Étape 1: Créer le projet Firebase (5 min)
```
1. https://firebase.google.com → Console
2. Create project → Entrez "MoviesApp"
3. Finalisez
```

### Étape 2: Configurer Google Sign-In (2 min)
```
1. Firebase Console → Authentication
2. Get Started → Google → Enable
3. Entrez votre email de support
```

### Étape 3: Télécharger google-services.json (3 min)
```
1. Firebase Console → Project Settings → Apps
2. Sélectionnez Android
3. Entrez: com.example.moviesapp_latiris
4. Générez SHA1:
   Windows PowerShell:
   $Env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
   & "$Env:JAVA_HOME\bin\keytool" -list -v -keystore "$Env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
5. Download google-services.json → Mettez-le dans app/
```

### Étape 4: Vérifier la configuration (1 min)
```
1. File → Sync Now
2. Vérifiez: Pas d'erreurs de build
```

### Étape 5: Lancer et tester! (2 min)
```
1. Run → Run 'app'
2. Vous verrez LoginActivity
3. Testez connexion email ou Google
```

---

## ✅ C'est prêt!

Votre application a maintenant:
- ✅ LoginActivity avec email/password
- ✅ Google Sign-In
- ✅ Session management
- ✅ Navigation vers RegisterActivity
- ✅ Redirection automatique si connecté

---

## 🔑 Clés d'authentification déjà configurées

L'application utilise ce Client ID Google (à jour dans FirebaseAuthManager.java):
```java
public static final String GOOGLE_WEB_CLIENT_ID = 
    "422852147244-q9bjf9kntr17e98pqprc5fd3mqpfo22r.apps.googleusercontent.com";
```

> Vous pouvez mettre à jour ce Client ID si nécessaire après avoir obtenu vos propres credentials Firebase.

---

## 📱 Structures des écrans

### LoginActivity
```
┌─────────────────────────┐
│      LOGO               │
├─────────────────────────┤
│ Welcome Back            │
│ Sign in to continue     │
├─────────────────────────┤
│ Email: [            ]   │
│ Password: [         ]   │
│ Forgot Password?        │
├─────────────────────────┤
│ [ Sign In ]             │
├─────────────────────────┤
│ Or sign in with         │
│ [ Google ]              │
├─────────────────────────┤
│ Don't have account?     │
│ Register                │
└─────────────────────────┘
```

### RegisterActivity
```
┌─────────────────────────┐
│      LOGO               │
├─────────────────────────┤
│ Create Account          │
│ Register to continue    │
├─────────────────────────┤
│ Full Name: [        ]   │
│ Email: [            ]   │
│ Password: [         ]   │
│ Confirm Password: [ ]   │
├─────────────────────────┤
│ [ Register ]            │
├─────────────────────────┤
│ Or sign up with         │
│ [ Google ]              │
├─────────────────────────┤
│ Already have account?   │
│ Login                   │
└─────────────────────────┘
```

---

## 🔄 Navigation

```
START
  ↓
LoginActivity
  ├─ "Sign In" → Succès → MainActivity
  ├─ "Google" → Succès → MainActivity
  └─ "Register" → RegisterActivity
       ├─ "Register" → Succès → MainActivity
       ├─ "Google" → Succès → MainActivity
       └─ "Login" → LoginActivity
```

---

## 🧪 Tests rapides

### Test 1: App starts
```
Expect: LoginActivity appears
```

### Test 2: Email login
```
1. Email: test@example.com
2. Password: 123456
3. Click "Sign In"
Expect: Si email enregistré → MainActivity
```

### Test 3: Google login
```
1. Click "Google"
2. Select Google account
Expect: MainActivity
```

### Test 4: Register link
```
1. Click "Don't have account? Register"
Expect: RegisterActivity
```

### Test 5: Already logged in
```
1. Login une fois
2. Fermez et relancez l'app
Expect: Directement MainActivity (pas LoginActivity)
```

---

## ⚠️ Problèmes courants

### "Build failed"
→ File → Sync Now

### "Cannot resolve FirebaseAuth"
→ Attendez que gradle synce, puis Build → Clean Project

### "12501 error on Google Sign-In"
→ Testez sur device physique (pas emulator)

### "App crashes at startup"
→ Vérifiez google-services.json est dans app/

---

## 📞 Support

- 📖 [Firebase Docs](https://firebase.google.com/docs)
- 🔑 [Google Sign-In](https://developers.google.com/identity/sign-in/android)

---

**You're ready! Happy coding!** 🎉

