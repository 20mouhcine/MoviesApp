# Google Sign-In Account Selector Fix

## Problem
The Google Sign-In was automatically selecting an account without showing the account selection interface. This prevented users from choosing which Google account to use.

## Root Cause
The previous implementation was caching the Google Sign-In session, which caused the SDK to automatically select the last used account instead of showing the account picker dialog.

## Solution Applied

### 1. Modified FirebaseAuthManager.java
- Added `import android.content.Intent;`
- Enhanced the Google Sign-In Options configuration by adding `.requestProfile()`
- Added `getSignInIntentWithAccountPicker()` method for future extensibility

### 2. Modified LoginActivity.java
- Changed `signInWithGoogle()` method to call `revokeAccess()` BEFORE launching the sign-in intent
- This forces Google Play Services to clear the cached session
- Now the account selection UI will always be displayed

```java
public void signInWithGoogle(android.view.View view) {
    // Revoke access to force account selection UI
    googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    });
}
```

### 3. Modified RegisterActivity.java
- Applied the same fix as LoginActivity for consistency
- Ensures the account picker is shown during registration as well

## How It Works

1. When user clicks "Sign in with Google" or "Sign up with Google" button
2. The app calls `googleSignInClient.revokeAccess()` to clear the cached session
3. Once revoke completes, the sign-in intent is launched
4. Google Play Services now shows the account selection UI
5. User can select/add their desired Google account

## Testing Steps

1. Open the app and go to Login or Register screen
2. Click "Google" button
3. You should now see Google's account selection interface showing:
   - Available Google accounts
   - Option to add a new account
   - Option to use a different account

4. Select your desired Google account
5. Complete the authentication flow

## Files Modified

1. `app/src/main/java/com/example/moviesapp_latiris/FirebaseAuthManager.java`
2. `app/src/main/java/com/example/moviesapp_latiris/LoginActivity.java`
3. `app/src/main/java/com/example/moviesapp_latiris/RegisterActivity.java`

## Key Difference from Previous Implementation

| Aspect | Before | After |
|--------|--------|-------|
| Account Selection | Automatic (cached) | Always shown (revoked first) |
| User Control | Limited | Full control over account choice |
| Session Cache | Persistent | Cleared before each sign-in |
| Method Used | signOut() | revokeAccess() |

## Notes

- `revokeAccess()` is more powerful than `signOut()` - it completely revokes access
- This approach is more reliable across different Android versions
- The account picker will appear consistently every time the user attempts to sign in

