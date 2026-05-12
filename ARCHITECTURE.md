# 📐 ARCHITECTURE - Supabase + Google Auth

## 🏗️ Architecture globale

```
╔════════════════════════════════════════════════════════════════════╗
║                      ANDROID APPLICATION                          ║
║  ┌──────────────────────────────────────────────────────────────┐ ║
║  │                    USER INTERFACE LAYER                      │ ║
║  │                                                              │ ║
║  │  ┌──────────────────┐        ┌──────────────────┐          │ ║
║  │  │  RegisterActivity│        │  LoginActivity   │          │ ║
║  │  │  (Email + Google)│        │  (Email)         │          │ ║
║  │  └────────┬─────────┘        └────────┬─────────┘          │ ║
║  │           │                           │                     │ ║
║  │           └─────────────┬─────────────┘                     │ ║
║  │                         │                                    │ ║
║  └─────────────────────────┼────────────────────────────────────┘ ║
║                            │                                      ║
║  ┌─────────────────────────▼────────────────────────────────────┐ ║
║  │              AUTHENTICATION LAYER                           │ ║
║  │  ┌────────────────────────────────────────────────────────┐│ ║
║  │  │ SupabaseManager (Config & Google Sign-In)             ││ ║
║  │  │  - initGoogleSignIn()                                 ││ ║
║  │  │  - handleGoogleSignInResult()                         ││ ║
║  │  │  - getCurrentGoogleAccount()                          ││ ║
║  │  │  - signOutGoogle()                                    ││ ║
║  │  └────────────────────────────────────────────────────────┘│ ║
║  │  ┌────────────────────────────────────────────────────────┐│ ║
║  │  │ SupabaseAuthHelper (API Calls)                        ││ ║
║  │  │  - registerWithEmail()                                ││ ║
║  │  │  - loginWithEmail()                                   ││ ║
║  │  │  - signInWithGoogleIdToken()                          ││ ║
║  │  │  - makePostRequest()                                  ││ ║
║  │  └────────────────────────────────────────────────────────┘│ ║
║  │  ┌────────────────────────────────────────────────────────┐│ ║
║  │  │ SessionManager (Persistence)                          ││ ║
║  │  │  - saveUserSession()                                  ││ ║
║  │  │  - isLoggedIn()                                       ││ ║
║  │  │  - clearSession()                                     ││ ║
║  │  └────────────────────────────────────────────────────────┘│ ║
║  └──────────────────────────────────────────────────────────────┘ ║
║                            │                                      ║
║  ┌─────────────────────────▼────────────────────────────────────┐ ║
║  │              PERSISTENCE LAYER                              │ ║
║  │  ┌────────────────────────────────────────────────────────┐│ ║
║  │  │ SharedPreferences                                      ││ ║
║  │  │  - user_id                                            ││ ║
║  │  │  - user_email                                         ││ ║
║  │  │  - user_name                                          ││ ║
║  │  │  - auth_token                                         ││ ║
║  │  │  - is_logged_in                                       ││ ║
║  │  └────────────────────────────────────────────────────────┘│ ║
║  └──────────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════════╝
                            │
                            │ HTTPS API Calls
                            │
    ┌───────────────────────┴───────────────────────┐
    │                                               │
    ▼                                               ▼
╔═════════════════════════╗        ╔═════════════════════════════╗
║   GOOGLE CLOUD AUTH     ║        ║  SUPABASE BACKEND           ║
║  ┌─────────────────────┐║        ║ ┌───────────────────────────┐║
║  │ OAuth 2.0           ││        ║ │ Supabase Auth             ││
║  │ - Sign-In           ││        ║ │ - JWT Tokens              ││
║  │ - ID Tokens         ││        ║ │ - Session Management      ││
║  │ - Profiles          ││        ║ │                           ││
║  └─────────────────────┘║        ║ ├───────────────────────────┤║
║                         ║        ║ │ PostgreSQL Database       ││
║                         ║        ║ │ - Users Table             ││
║                         ║        ║ │ - Auth Metadata           ││
║                         ║        ║ │ - Sessions                ││
║                         ║        ║ └───────────────────────────┘║
╚═════════════════════════╝        ╚═════════════════════════════╝
```

---

## 🔄 Flux d'authentification

### Scénario 1: Google Sign-In

```
┌─────────────────┐
│ RegisterActivity│
└────────┬────────┘
         │ Click "Google"
         ▼
┌──────────────────────┐
│ SupabaseManager      │
│ signInWithGoogle()   │
└────────┬─────────────┘
         │ Launch Google Sign-In
         ▼
┌──────────────────────┐
│ Google Play Services │
│ Google Sign-In UI    │
└────────┬─────────────┘
         │ User authentication + consent
         ▼
┌──────────────────────┐
│ Get ID Token         │
└────────┬─────────────┘
         │
         ▼
┌───────────────────────────────────┐
│ SupabaseAuthHelper                │
│ signInWithGoogleIdToken()         │
└────────┬────────────────────────┬─┘
         │ Send to Supabase       │
         ▼                        ▼
┌──────────────────────┐   ┌──────────────────┐
│ HTTP POST Request    │   │ JWT Token        │
└────────┬─────────────┘   └────────┬─────────┘
         │                          │
         ▼                          ▼
┌──────────────────────────────────────────┐
│ SessionManager.saveUserSession()         │
│ - Store userId, email, token             │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│ MainActivity                             │
│ User is authenticated                    │
└──────────────────────────────────────────┘
```

### Scénario 2: Email Registration

```
┌─────────────────┐
│ RegisterActivity│
└────────┬────────┘
         │ User enters data + validates
         ▼
┌──────────────────────┐
│ SupabaseAuthHelper   │
│ registerWithEmail()  │
└────────┬─────────────┘
         │ Create request body
         │ {email, password}
         ▼
┌──────────────────────────────────┐
│ HTTP POST Request                │
│ POST /auth/v1/signup             │
│ Headers: Content-Type, apikey    │
└────────┬────────────────────────┬┘
         │ Send to Supabase       │
         │                        ▼
         │                 ┌─────────────────┐
         │                 │ User Created    │
         │                 │ JWT Generated   │
         │                 └────────┬────────┘
         │                          │
         ▼                          ▼
┌──────────────────────────────────────────┐
│ AuthCallback.onSuccess()                 │
│ - Get userId, email, token               │
└────────┬───────────────────────────────┬─┘
         │                               │
         ▼                               ▼
┌──────────────────────┐      ┌──────────────────┐
│ SessionManager       │      │ MainActivity     │
│ saveUserSession()    │      │ Navigate         │
└──────────────────────┘      └──────────────────┘
```

### Scénario 3: Email Login

```
┌─────────────────┐
│ LoginActivity   │
└────────┬────────┘
         │ User enters email + password
         ▼
┌──────────────────────┐
│ SupabaseAuthHelper   │
│ loginWithEmail()     │
└────────┬─────────────┘
         │ Create request body
         │ {email, password, grant_type}
         ▼
┌──────────────────────────────────┐
│ HTTP POST Request                │
│ POST /auth/v1/token              │
│ ?grant_type=password             │
└────────┬────────────────────────┬┘
         │ Send to Supabase       │
         │                        ▼
         │                 ┌──────────────────────┐
         │                 │ Credentials Verified │
         │                 │ JWT Access Token     │
         │                 │ Refresh Token        │
         │                 └────────┬─────────────┘
         │                          │
         ▼                          ▼
┌──────────────────────────────────────────┐
│ AuthCallback.onSuccess()                 │
│ - Get userId, email, token               │
└────────┬────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│ SessionManager.saveUserSession()         │
│ MainActivity.startActivity()             │
└──────────────────────────────────────────┘
```

---

## 📦 Structure des données

### SharedPreferences Storage

```
MovieAppSession
├── user_id: String (UUID from Supabase)
├── user_email: String (user@example.com)
├── user_name: String (User Full Name)
├── auth_token: String (JWT Token)
└── is_logged_in: Boolean (true/false)
```

### HTTP Request Headers

```
POST /auth/v1/signup
Content-Type: application/json
apikey: [SUPABASE_ANON_KEY]
Authorization: Bearer [SUPABASE_ANON_KEY]

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

### HTTP Response

```
{
  "user": {
    "id": "uuid-string",
    "email": "user@example.com",
    "created_at": "2024-01-01T00:00:00Z"
  },
  "session": {
    "access_token": "eyJhbGc...",
    "token_type": "bearer",
    "expires_in": 3600
  }
}
```

---

## 🔗 Dépendances et classes

```
build.gradle.kts
├── libs.supabase.kotlin (v2.0.10)
└── libs.google.play.services.auth (v20.7.0)

SupabaseManager.java
├── GoogleSignInClient
├── GoogleSignInOptions
└── Constants:
    - SUPABASE_URL
    - SUPABASE_ANON_KEY
    - GOOGLE_WEB_CLIENT_ID

SupabaseAuthHelper.java
├── registerWithEmail()
├── loginWithEmail()
├── signInWithGoogleIdToken()
└── makePostRequest()

SessionManager.java
├── SharedPreferences
├── saveUserSession()
├── isLoggedIn()
└── clearSession()

RegisterActivity.java
├── SupabaseManager
├── ActivityResultLauncher
└── GoogleSignInClient

MainActivity.java
├── SessionManager check
└── Redirect logic
```

---

## 🔐 Sécurité

### Données sensibles

```
┌─────────────────────────────────────────┐
│ DONNÉES SENSIBLES STOCKÉES LOCALEMENT   │
├─────────────────────────────────────────┤
│ • JWT Access Token (à CHIFFRER)         │
│ • User Email (acceptable)               │
│ • User Name (acceptable)                │
│ • User ID (acceptable)                  │
└─────────────────────────────────────────┘

Amélioration recommandée:
- Utiliser EncryptedSharedPreferences
- Implémenter KeyStore
- Ajouter Refresh Token Logic
```

### Transport

```
┌──────────────────────────────────────────┐
│ TRANSPORT SECURE                        │
├──────────────────────────────────────────┤
│ ✓ HTTPS (implémenté par défaut)         │
│ ✓ Tokens dans Authorization Header      │
│ ✓ Validation SSL Certificate            │
│ ⚠️ HTTPS Pinning (À AJOUTER)            │
│ ⚠️ Certificate Pinning (À AJOUTER)      │
└──────────────────────────────────────────┘
```

---

## 📊 État actuel vs Production-Ready

```
ACTUEL (100% implémenté):
├── ✅ Google Sign-In
├── ✅ Email Registration (structure)
├── ✅ Email Login (structure)
├── ✅ Session Management
├── ✅ Input Validation
├── ✅ Error Handling
├── ✅ Documentation
└── ✅ Code Examples

PRODUCTION-READY (À AJOUTER):
├── ⚠️ EncryptedSharedPreferences
├── ⚠️ Refresh Token Logic
├── ⚠️ Email Verification
├── ⚠️ Password Reset
├── ⚠️ HTTPS Certificate Pinning
├── ⚠️ 2FA Support
├── ⚠️ Rate Limiting
└── ⚠️ Audit Logging
```

---

## 🎯 Intégration avec app existante

```
BEFORE (Ancien):
MainActivity
├── Volley API calls (Direct)
├── No authentication
└── No user session

AFTER (Nouveau):
RegisterActivity / LoginActivity
├── Supabase Authentication
├── Google Sign-In
└── SessionManager
    │
    ├── MainActivity (avec userId)
    │   ├── Volley API calls (Authentifiés)
    │   ├── Session persistence
    │   └── User context

ARCHITECTURE FINALE:
User → Auth Layer → Session → App Features
                              └── API calls (with token)
```

---

**Architecture documentée!** 📐

Pour plus de détails, consultez `AUTH_IMPLEMENTATION_GUIDE.md`

