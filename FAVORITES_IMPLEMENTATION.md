# Implémentation des Favoris - Guide Complet

## Vue d'ensemble
Cette implémentation permet aux utilisateurs d'ajouter/supprimer des films à leur liste de favoris stockée dans Firebase Firestore.

## Architecture

### Composants créés/modifiés

1. **FavoritesManager.java** (NOUVEAU)
   - Classe centrale pour gérer les opérations Firebase Firestore
   - Méthodes principales:
     - `addToFavorites()` - Ajouter un film aux favoris
     - `removeFromFavorites()` - Supprimer un film des favoris
     - `isFavorite()` - Vérifier si un film est favori
     - `getFavorites()` - Récupérer tous les favoris de l'utilisateur
     - `clearAllFavorites()` - Supprimer tous les favoris

2. **MovieDetailActivity.java** (MODIFIÉ)
   - Ajout du bouton "Add to Favorites"
   - Intégration de FavoritesManager
   - Vérification du statut de favori et mise à jour de l'UI
   - Navigation vers FavorisActivity via le menu de fond

3. **FavorisActivity.java** (MODIFIÉ)
   - Affichage de la liste des films favoris
   - Utilise RecyclerView avec GridLayoutManager (2 colonnes)
   - Message vide quand aucun favori
   - Rechargement des favoris lors du retour à l'activité

4. **activity_movie_detail.xml** (MODIFIÉ)
   - Ajout du bouton favoris avec icône ☆/★

5. **activity_favoris.xml** (MODIFIÉ)
   - Layout complet avec RecyclerView pour afficher les favoris
   - Message d'état vide
   - Intégration de la navigation du bas

6. **BottomNavHelper.java** (MODIFIÉ)
   - Ajout du support de navigation vers FavorisActivity

## Structure Firestore

```
users/
  ├── {userId}/
  │   └── favorites/
  │       ├── {movieId}/
  │       │   ├── movieId: number
  │       │   ├── movieName: string
  │       │   ├── movieDate: string
  │       │   ├── movieImage: string
  │       │   └── addedAt: timestamp
```

## Configuration Firestore - Règles de sécurité

Allez à Firebase Console > Firestore > Règles et remplacez le contenu par:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow users to read/write their own favorites
    match /users/{userId}/favorites/{document=**} {
      allow read, write: if request.auth.uid == userId;
    }
    
    // Allow users to read/write their own profile data
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

## Utilisation

### Ajouter un film aux favoris
```java
FavoritesManager favoritesManager = new FavoritesManager();

favoritesManager.addToFavorites(
    movieId,           // ID du film (int)
    movieName,         // Nom du film (String)
    movieDate,         // Date du film (String)
    movieImage,        // URL de l'image (String)
    new FavoritesManager.FavoriteCallback() {
        @Override
        public void onSuccess(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(String error) {
            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
        }
    }
);
```

### Vérifier si un film est favori
```java
favoritesManager.isFavorite(movieId, new FavoritesManager.IsFavoriteCallback() {
    @Override
    public void onResult(boolean isFavorite) {
        if (isFavorite) {
            // Le film est dans les favoris
        } else {
            // Le film n'est pas dans les favoris
        }
    }
});
```

### Récupérer tous les favoris
```java
favoritesManager.getFavorites(new FavoritesManager.FavoritesListCallback() {
    @Override
    public void onSuccess(List<MyMovieData> favorites) {
        // Traiter la liste des favoris
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Error: " + error);
    }
});
```

## Fonctionnalités

✅ Ajouter/supprimer des films aux favoris
✅ Vérifier le statut de favori
✅ Afficher la liste complète des favoris
✅ Interface utilisateur dynamique (bouton change de couleur/texte)
✅ Synchronisation en temps réel avec Firebase
✅ Authentification obligatoire (utilisateur doit être connecté)
✅ Affichage du message "No favorites" quand la liste est vide

## Authentification requise

Tous les utilisateurs DOIVENT être authentifiés pour utiliser la fonctionnalité de favoris.
La classe `FavoritesManager` retournera une erreur si aucun utilisateur n'est connecté.

## Dépendances

- Firebase Firestore (déjà configuré)
- Firebase Auth (déjà configuré)
- RecyclerView (déjà configuré)
- Glide (déjà configuré)

## Tests

### Tester l'ajout aux favoris
1. Ouvrir un détail de film
2. Cliquer sur "Add to Favorites"
3. Vérifier que le bouton devient "★ Remove from Favorites" avec couleur jaune
4. Vérifier dans Firebase Console que le document a été créé

### Tester la suppression des favoris
1. Depuis la page des favoris ou détail du film, cliquer "Remove from Favorites"
2. Vérifier que le bouton revient à "☆ Add to Favorites" avec couleur grise
3. Vérifier dans Firebase Console que le document a été supprimé

### Tester l'affichage des favoris
1. Ajouter plusieurs films aux favoris
2. Cliquer sur l'onglet "Favorites" dans la navigation du bas
3. Vérifier que tous les films apparaissent dans la grille
4. Cliquer sur un film pour voir ses détails

## Problèmes potentiels et solutions

### Aucun film n'apparaît dans Favoris
- Vérifier que l'utilisateur est authentifié
- Vérifier les règles de sécurité Firestore
- Vérifier la console Firebase pour les erreurs

### Erreur: "User not authenticated"
- Assurez-vous que l'utilisateur est connecté avant d'ajouter des favoris
- L'utilisateur doit avoir une session active

### Données non synchronisées
- Vérifier la connexion Internet
- Vérifier les logs Firebase pour les erreurs
- S'assurer que Firestore est activé dans Firebase Console

## Fichiers modifiés

- `app/src/main/java/com/example/moviesapp_latiris/FavoritesManager.java` (NOUVEAU)
- `app/src/main/java/com/example/moviesapp_latiris/MovieDetailActivity.java`
- `app/src/main/java/com/example/moviesapp_latiris/FavorisActivity.java`
- `app/src/main/java/com/example/moviesapp_latiris/BottomNavHelper.java`
- `app/src/main/res/layout/activity_movie_detail.xml`
- `app/src/main/res/layout/activity_favoris.xml`

