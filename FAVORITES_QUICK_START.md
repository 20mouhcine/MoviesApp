# Favoris - Guide d'intégration rapide

## 🚀 Démarrage rapide

### 1. Initialiser FavoritesManager
```java
private FavoritesManager favoritesManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Initialiser le gestionnaire des favoris
    favoritesManager = new FavoritesManager();
}
```

### 2. Ajouter un film aux favoris
```java
private void addMovieToFavorites(int movieId, String movieName, String movieDate, String movieImage) {
    favoritesManager.addToFavorites(movieId, movieName, movieDate, movieImage,
        new FavoritesManager.FavoriteCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                updateFavoriteButton(true);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
}
```

### 3. Supprimer un film des favoris
```java
private void removeMovieFromFavorites(int movieId) {
    favoritesManager.removeFromFavorites(movieId,
        new FavoritesManager.FavoriteCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                updateFavoriteButton(false);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
}
```

### 4. Vérifier si un film est favori
```java
private void checkIfFavorite(int movieId) {
    favoritesManager.isFavorite(movieId, isFavorite -> {
        if (isFavorite) {
            updateFavoriteButton(true);  // Montrer l'état "★ Remove"
        } else {
            updateFavoriteButton(false); // Montrer l'état "☆ Add"
        }
    });
}

private void updateFavoriteButton(boolean isFavorite) {
    if (isFavorite) {
        favoriteButton.setText("★ Remove from Favorites");
        favoriteButton.setBackgroundTint(Color.parseColor("#FFC107"));
        favoriteButton.setTextColor(Color.BLACK);
    } else {
        favoriteButton.setText("☆ Add to Favorites");
        favoriteButton.setBackgroundTint(Color.parseColor("#666666"));
        favoriteButton.setTextColor(Color.WHITE);
    }
}
```

### 5. Charger la liste des favoris
```java
private void loadFavorites() {
    favoritesManager.getFavorites(new FavoritesManager.FavoritesListCallback() {
        @Override
        public void onSuccess(List<MyMovieData> favorites) {
            if (favorites.isEmpty()) {
                emptyStateView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyStateView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setMovies(favorites);
            }
        }

        @Override
        public void onError(String error) {
            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
        }
    });
}
```

### 6. Basculer le statut de favori (Toggle)
```java
private boolean isFavorite = false;

private void toggleFavorite(int movieId, String movieName, String movieDate, String movieImage) {
    if (isFavorite) {
        removeMovieFromFavorites(movieId);
        isFavorite = false;
    } else {
        addMovieToFavorites(movieId, movieName, movieDate, movieImage);
        isFavorite = true;
    }
}
```

---

## 📋 Callbacks disponibles

### FavoriteCallback
```java
public interface FavoriteCallback {
    void onSuccess(String message);     // Opération réussie
    void onError(String error);         // Erreur lors de l'opération
}
```

### FavoritesListCallback
```java
public interface FavoritesListCallback {
    void onSuccess(List<MyMovieData> favorites);  // Liste des favoris
    void onError(String error);                   // Erreur
}
```

### IsFavoriteCallback
```java
public interface IsFavoriteCallback {
    void onResult(boolean isFavorite);  // true = favori, false = non favori
}
```

---

## 🔧 Configuration Firestore

### Structure des données
```
users/
  └── {userId}/
      └── favorites/
          └── {movieId}/
              ├── movieId: 550
              ├── movieName: "Fight Club"
              ├── movieDate: "1999-10-15"
              ├── movieImage: "/pB8BM50VVRVIRP3EL6NSUWBPIMH.jpg"
              └── addedAt: 1673123456789
```

### Règles de sécurité (à ajouter dans Firebase Console)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/favorites/{document=**} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

---

## 🎨 Styling (activity_movie_detail.xml)

Le bouton favoris est stylisé comme suit :
```xml
<Button
    android:id="@+id/favoriteButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="8dp"
    android:text="☆ Add to Favorites"
    android:backgroundTint="#666666"
    android:textColor="#FFFFFF" />
```

Changements dynamiques en Java :
- **Avant d'ajouter** : `☆ Add to Favorites` (gris)
- **Après l'ajout** : `★ Remove from Favorites` (jaune/or)

---

## ❌ Gestion des erreurs

```java
favoritesManager.addToFavorites(movieId, name, date, image,
    new FavoritesManager.FavoriteCallback() {
        @Override
        public void onSuccess(String message) {
            Log.d(TAG, "Success: " + message);
        }

        @Override
        public void onError(String error) {
            if (error.contains("not authenticated")) {
                // Rediriger vers la page de connexion
                startActivity(new Intent(getContext(), LoginActivity.class));
            } else if (error.contains("permission")) {
                // Vérifier les règles Firestore
                Log.e(TAG, "Permission denied");
            } else {
                // Erreur générale
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
    });
```

---

## 🚨 Points importants

1. **Authentification obligatoire**
   - L'utilisateur doit être connecté
   - FavoritesManager utilise `FirebaseAuth.getInstance().getCurrentUser()`

2. **Firestore doit être actif**
   - Créer une base de données Firestore dans Firebase Console
   - Publier les règles de sécurité

3. **Réseau requis**
   - Opérations asynchrones
   - Vérifier la connexion Internet

4. **Permissions**
   - Les utilisateurs ne peuvent voir que leurs propres favoris
   - Les règles Firestore l'appliquent

5. **Performance**
   - `getFavorites()` trie par date d'ajout (descendant)
   - Utiliser `.setMovies()` de l'adapter pour une mise à jour complète

---

## 🐛 Débogage

### Logs disponibles
```java
// FavoritesManager log des opérations
Log.d(TAG, "Movie added to favorites: " + movieName);
Log.e(TAG, "Error adding to favorites", e);
Log.d(TAG, "Retrieved " + favorites.size() + " favorites");
```

### Vérifier Firebase Console
1. Allez à Firestore Database
2. Cliquez sur "Données"
3. Naviguez à `users > {userId} > favorites`
4. Vérifiez que les documents ont les bons champs

---

## 📞 Support

En cas de problème :
1. Vérifier que l'utilisateur est authentifié
2. Vérifier les règles Firestore
3. Vérifier les logs Firestore dans Firebase Console
4. Vérifier la connexion Internet
5. Vérifier les logs logcat pour les erreurs

---

**Bonne intégration ! 🚀**

