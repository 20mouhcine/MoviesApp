# 🚀 FIREBASE QUICKSTART - 20 minutes

## 1️⃣ Créer un projet Firebase (5 min)

```
1. https://firebase.google.com → "Go to console"
2. "+ Add project"
3. Nom: "MoviesApp"
4. Analytics: Skip (optionnel)
5. "Create project"
6. Attendez...
```

## 2️⃣ Enregistrer votre app Android (5 min)

```
1. Firebase Console → "+ Add app"
2. Choisissez Android
3. Package name: com.example.moviesapp_latiris
4. App nickname: MoviesApp
5. SHA-1: générez-le (voir ci-dessous)
6. "Register app"
7. Téléchargez google-services.json
```

### Générer SHA-1:

**Windows (PowerShell):**
```powershell
$Env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
& "$Env:JAVA_HOME\bin\keytool" -list -v -keystore "$Env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**Mac/Linux:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Cherchez: `SHA1: XX:XX:XX:...`

## 3️⃣ Mettre le fichier google-services.json (5 min)

```
1. Ouvrez le fichier google-services.json téléchargé
2. Mettez-le dans: app/ (racine du dossier app)
3. File → Sync Now
```

## 4️⃣ Configurer le Client ID (5 min)

**Dans Firebase Console:**
```
1. Project Settings (⚙️ en haut à gauche)
2. Onglet "Apps" → Votre app Android
3. Onglet "Certificates" 
4. Cherchez "Google Services Settings" (vers le bas)
5. Vous verrez votre Web Application Client ID
```

**Dans le code:**
```
1. Ouvrez: app/src/main/java/.../FirebaseAuthManager.java
2. Ligne 16: GOOGLE_WEB_CLIENT_ID = "..."
3. Remplacez "your-..." par le Web Client ID
```

## 5️⃣ Lancer et tester (2 min)

```
1. Android Studio: Run → Run 'app'
2. Cliquez le bouton "Google"
3. Signez-vous avec votre compte Google
4. Vous devriez être redirigé vers MainActivity ✓
```

---

## ✅ Checklist rapide

- [ ] Projet Firebase créé
- [ ] App Android enregistrée dans Firebase
- [ ] SHA-1 fourni
- [ ] google-services.json téléchargé et mis dans app/
- [ ] Firebase gradle synced (File → Sync Now)
- [ ] GOOGLE_WEB_CLIENT_ID configuré
- [ ] App lancée sans erreur
- [ ] Google Sign-In fonctionne

---

**C'est tout!** 🎉

Consultez `FIREBASE_SETUP_GUIDE.md` pour plus de détails.

