# 🧪 GUIDE DE TEST - Supabase + Google Auth

## ✅ Tests à effectuer après la configuration

### Test 1: Vérifier les imports (5 min)

**Fichier:** `RegisterActivity.java`
**Vérifier:**
```
✓ import com.google.android.gms.auth.api.signin.*
✓ import com.google.android.gms.tasks.*
✓ import androidx.activity.result.*
✓ import com.google.android.material.textfield.*
```

**Action:** Build → Make Project
**Résultat attendu:** Pas d'erreur de compilation

---

### Test 2: Vérifier les dépendances (5 min)

**Fichier:** `app/build.gradle.kts`
**Vérifier:**
```
✓ implementation(libs.supabase.kotlin)
✓ implementation(libs.google.play.services.auth)
```

**Action:** File → Sync Now
**Résultat attendu:** Gradle sync réussi

---

### Test 3: Vérifier la configuration (5 min)

**Fichier:** `SupabaseManager.java`
**Action:** 
```
1. Ouvrez le fichier
2. Vérifiez que les constantes TODO sont présentes:
   - SUPABASE_URL
   - SUPABASE_ANON_KEY
   - GOOGLE_WEB_CLIENT_ID
```
**Résultat attendu:** 3 constantes visibles (avec "your-" comme placeholders)

---

### Test 4: Test sur émulateur avec Google Play Services

**Prérequis:**
- Émulateur Android avec Google Play Services
- Google account configuré sur l'émulateur

**Étapes:**
```
1. Lancez l'émulateur
2. Allez à Settings → Accounts
3. Connectez-vous avec un compte Google
4. Lancez l'app (Run → Run 'app')
```

**Résultat attendu:**
```
✓ App démarre sans crash
✓ RegisterActivity s'affiche
✓ Bouton "Google" visible
```

---

### Test 5: Tester Google Sign-In (avant configuration)

**Action:**
```
1. Cliquez sur le bouton "Google"
2. Observez le comportement
```

**Résultat attendu (avant config):**
```
⚠️ Erreur Google Sign-In (normal - credentials manquants)
Log: "Google Sign-In failed"
```

**Résultat attendu (après config):**
```
✓ Google Sign-In dialog s'ouvre
✓ Sélectionnez un compte
✓ Écran de consentement s'affiche
✓ Après acceptation, redirection vers MainActivity
```

---

### Test 6: Vérifier les logs

**Étapes:**
```
1. Lancez l'app
2. Ouvrez Logcat (View → Tool Windows → Logcat)
3. Cliquez sur "Google"
4. Observez les logs
```

**Logs attendus:**
```
D/SupabaseManager: Google Sign-In successful: [email@example.com]
```

---

### Test 7: Validation des formulaires

**Email Registration:**
```
1. Laissez le champ Full Name vide → Click Register
   ✓ Erreur: "Full name is required"

2. Entrez "Test User" → Laissez Email vide → Click Register
   ✓ Erreur: "Email is required"

3. Entrez "invalid-email" → Click Register
   ✓ Erreur: "Invalid email format"

4. Entrez un email valide → Laissez password vide → Click Register
   ✓ Erreur: "Password is required"

5. Entrez un password de 3 caractères → Click Register
   ✓ Erreur: "Password must be at least 6 characters"

6. Password et Confirm Password différents → Click Register
   ✓ Erreur: "Passwords do not match"

7. Tous les champs corrects → Click Register
   ✓ Toast: "Registration with email coming soon!"
```

---

### Test 8: Vérifier SessionManager

**Créez un test file:** `SessionManagerTest.java`

```java
public class SessionManagerTest {
    
    public static void testSessionManager(Context context) {
        SessionManager manager = new SessionManager(context);
        
        // Test 1: Vérifier que la session est vide au démarrage
        assert !manager.isLoggedIn() : "Session should be empty on start";
        
        // Test 2: Sauvegarder une session
        manager.saveUserSession("123", "test@example.com", "Test User", "token-abc");
        assert manager.isLoggedIn() : "Session should be logged in";
        
        // Test 3: Vérifier les données
        assert "test@example.com".equals(manager.getUserEmail()) : "Email incorrect";
        assert "Test User".equals(manager.getUserName()) : "Name incorrect";
        assert "token-abc".equals(manager.getAuthToken()) : "Token incorrect";
        
        // Test 4: Effacer la session
        manager.clearSession();
        assert !manager.isLoggedIn() : "Session should be cleared";
        
        System.out.println("✓ Tous les tests SessionManager réussis");
    }
}
```

---

### Test 9: Vérifier les permissions

**Fichier:** `AndroidManifest.xml`
**Vérifier:**
```
✓ android.permission.INTERNET
✓ android.permission.ACCESS_NETWORK_STATE
✓ android.permission.GET_ACCOUNTS
```

**Action:** File → Sync Now

---

### Test 10: Test sur appareil physique

**Prérequis:**
- Appareil Android avec Google Play Services
- USB debugging activé
- ADB reconnaît l'appareil

**Étapes:**
```
1. Connectez l'appareil via USB
2. Run → Run 'app'
3. Sélectionnez l'appareil physique
4. Attendez que l'app soit lancée
```

**Résultat attendu:**
```
✓ App fonctionne sur appareil physique
✓ Google Sign-In fonctionne
✓ Aucun crash
```

---

## 📊 Matrice de test

| Test | Prérequis | Action | Résultat attendu | Status |
|------|-----------|--------|------------------|--------|
| Compilation | Android Studio | Build | Pas d'erreur | ⏳ |
| Sync Gradle | Dépendances | Sync | Succès | ⏳ |
| Emulateur | API 24+ | Run | App démarre | ⏳ |
| Google Sign-In | Credentials | Click | Dialog s'ouvre | ⏳ |
| Validation Email | Form | Input | Error messages | ⏳ |
| SessionManager | Context | Test | Save/Load OK | ⏳ |
| Permissions | Manifest | Check | Get_Accounts OK | ⏳ |
| Logs | Logcat | Filter | Logs visibles | ⏳ |
| Appareil physique | USB | Run | App fonctionne | ⏳ |
| Integration | Tout | E2E | Flux complet | ⏳ |

---

## 🔧 Déboguer les problèmes courants

### Problème: "Cannot resolve symbol 'SupabaseManager'"

**Cause:** Import manquant ou fichier non créé
**Solution:**
```
1. Vérifiez que SupabaseManager.java existe dans:
   app/src/main/java/com/example/moviesapp_latiris/
2. Vérifiez l'import:
   import com.example.moviesapp_latiris.SupabaseManager;
3. File → Invalidate Caches → Restart
```

---

### Problème: "GoogleSignInClient not initialized"

**Cause:** `initGoogleSignIn()` non appelé
**Solution:**
```
Dans RegisterActivity.onCreate():
✓ Ajouter: SupabaseManager.initGoogleSignIn(this);
✓ Ajouter: googleSignInClient = SupabaseManager.getGoogleSignInClient();
```

---

### Problème: "Google Sign-In: Internal error"

**Cause:** Émulateur sans Google Play Services
**Solution:**
```
1. Utilisez un émulateur avec "Google Play":
   - Créez un AVD
   - Choisissez "Google Play" dans System Image
2. Ou utilisez un appareil physique
```

---

### Problème: "12501 error"

**Cause:** Google Play Services non disponible
**Solution:**
```
1. Testez sur appareil physique
2. Ou utilisez un émulateur avec Google Play
3. Assurez-vous que Google est configuré sur l'appareil
```

---

## ✅ Checklist de test finale

- [ ] Code compile sans erreurs
- [ ] Gradle sync réussit
- [ ] App démarre sur émulateur
- [ ] RegisterActivity affichée
- [ ] Bouton "Google" visible
- [ ] Email validation fonctionne
- [ ] SessionManager fonctionne
- [ ] Permissions correctes
- [ ] Logs apparaissent dans Logcat
- [ ] App fonctionne sur appareil physique
- [ ] Aucun crash lors des tests
- [ ] Documentation lue et comprise

---

## 📝 Template de rapport de test

```
Date: [Date]
Testeur: [Votre nom]
Appareil: [Emulateur/Device] [Version Android]

Tests effectués:
- [ ] Compilation: _____ (OK/FAIL)
- [ ] Gradle sync: _____ (OK/FAIL)
- [ ] App launch: _____ (OK/FAIL)
- [ ] UI: _____ (OK/FAIL)
- [ ] Email validation: _____ (OK/FAIL)
- [ ] Google Sign-In: _____ (OK/FAIL)
- [ ] Session manager: _____ (OK/FAIL)

Bugs trouvés:
1. [Description]
2. [Description]

Notes:
[Observations]
```

---

## 🎉 Tous les tests passent?

Si oui, votre configuration est réussie! 🚀

Prochaine étape: Consulter `AUTH_IMPLEMENTATION_GUIDE.md` pour les cas d'utilisation avancés.

---

**Bon testing!** 🧪✨

