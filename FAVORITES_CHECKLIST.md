# ✅ Checklist de mise en place - Favoris

## Phase 1 : Configuration Firebase

- [ ] Aller à https://console.firebase.google.com
- [ ] Sélectionner le projet "moviesapp-2d551"
- [ ] Vérifier que Firestore Database est activé
- [ ] Si absent, créer une nouvelle base de données

## Phase 2 : Règles de sécurité Firestore

- [ ] Aller à Firestore Database → Règles
- [ ] Copier les règles de `FIRESTORE_SETUP_GUIDE.md`
- [ ] Remplacer le contenu existant
- [ ] Cliquer "Publier"
- [ ] Attendre la confirmation

## Phase 3 : Authentification

- [ ] Aller à Authentication → Sign-in method
- [ ] Vérifier que "Email/Password" est activé
- [ ] Vérifier que "Google Sign-In" est activé
- [ ] Créer au moins un utilisateur de test

## Phase 4 : Build et compilation

- [ ] Ouvrir le projet dans Android Studio
- [ ] Synchroniser Gradle (File > Sync Now)
- [ ] Vérifier qu'il n'y a pas d'erreurs
- [ ] Compiler le projet (Build > Make Project)
- [ ] Vérifier qu'il n'y a pas d'erreurs de compilation

## Phase 5 : Tester sur émulateur

- [ ] Lancer l'émulateur Android (SDK 24+)
- [ ] Compiler et déployer l'app
- [ ] Se connecter avec un utilisateur de test

### Test 1 : Ajouter un film aux favoris
- [ ] Naviguer vers un film
- [ ] Voir le bouton "☆ Add to Favorites"
- [ ] Cliquer sur le bouton
- [ ] Voir un toast "Added to favorites"
- [ ] Voir le bouton devenir "★ Remove from Favorites" (jaune)
- [ ] Vérifier dans Firebase Console que le document a été créé

### Test 2 : Afficher les favoris
- [ ] Cliquer sur l'onglet "Favorites" dans le menu du bas
- [ ] Voir le film que vous venez d'ajouter
- [ ] Cliquer sur le film pour voir ses détails
- [ ] Vérifier que le bouton montre "★ Remove"

### Test 3 : Supprimer d'un film des favoris
- [ ] Depuis la page de détail du film, cliquer "★ Remove from Favorites"
- [ ] Voir un toast "Removed from favorites"
- [ ] Voir le bouton redevenir "☆ Add to Favorites" (gris)
- [ ] Retourner à la page des favoris
- [ ] Vérifier que le film a disparu
- [ ] Vérifier dans Firebase Console que le document a été supprimé

### Test 4 : Ajouter plusieurs favoris
- [ ] Ajouter 5-10 films aux favoris
- [ ] Aller à la page des favoris
- [ ] Vérifier que tous les films apparaissent
- [ ] Vérifier que les images se chargent correctement

### Test 5 : Message vide
- [ ] Supprimer tous les favoris
- [ ] Aller à la page des favoris
- [ ] Vérifier le message "No favorite movies yet"

## Phase 6 : Tester sur appareil réel

- [ ] Connecter un appareil Android
- [ ] Compiler et déployer l'app
- [ ] Répéter les tests 1-5
- [ ] Tester avec la 4G/WiFi
- [ ] Tester en mode hors ligne (favoris ne s'ajoutent pas)

## Phase 7 : Vérification Firebase Console

- [ ] Aller à Firestore Database → Données
- [ ] Vérifier la structure :
  ```
  users/
    └── {userId}/
        └── favorites/
            └── {movieId}/
  ```
- [ ] Vérifier que les champs sont corrects :
  - [ ] `movieId` (number)
  - [ ] `movieName` (string)
  - [ ] `movieDate` (string)
  - [ ] `movieImage` (string)
  - [ ] `addedAt` (timestamp)

## Phase 8 : Vérification des logs

- [ ] Ouvrir Logcat (View → Tool Windows → Logcat)
- [ ] Filtrer par "FavoritesManager"
- [ ] Ajouter un film
- [ ] Vérifier le log : "Movie added to favorites"
- [ ] Aller à la liste des favoris
- [ ] Vérifier le log : "Retrieved X favorites"
- [ ] Supprimer un film
- [ ] Vérifier le log : "Movie removed from favorites"

## Phase 9 : Test de permissions

- [ ] Créer deux comptes utilisateurs différents
- [ ] Ajouter des favoris avec le compte 1
- [ ] Se déconnecter
- [ ] Se connecter avec le compte 2
- [ ] Aller à la page des favoris
- [ ] Vérifier qu'aucun favori du compte 1 n'apparaît
- [ ] Vérifier le message vide
- [ ] Ajouter des favoris avec le compte 2
- [ ] Se reconnecter avec le compte 1
- [ ] Vérifier que les favoris du compte 1 réapparaissent

## Phase 10 : Documentation

- [ ] Lire `FAVORITES_IMPLEMENTATION.md`
- [ ] Lire `FIRESTORE_SETUP_GUIDE.md`
- [ ] Lire `FAVORITES_QUICK_START.md`
- [ ] Lire `FAVORITES_SUMMARY.md`
- [ ] Annoter les fichiers modifiés si nécessaire

## ✅ Vérification finale

- [ ] Pas d'erreurs de compilation
- [ ] Pas d'avertissements critiques
- [ ] Tous les tests passent
- [ ] Firebase Console montre les données correctement
- [ ] Les permissions sont appliquées
- [ ] La documentation est à jour
- [ ] Le code est propre et commenté

## 🚀 Déploiement prêt

Si tous les points sont cochés :
- ✅ La fonctionnalité est prête pour la production
- ✅ Les utilisateurs peuvent ajouter des films aux favoris
- ✅ Les favoris sont synchronisés avec Firebase
- ✅ Les permissions de sécurité sont appliquées

---

## 🆘 En cas de problème

### Les favoris ne s'ajoutent pas
1. Vérifier que l'utilisateur est authentifié
2. Vérifier les règles Firestore
3. Vérifier les logs Firestore Console
4. Vérifier Logcat pour les erreurs

### Les favoris ne s'affichent pas
1. Vérifier que Firestore est activé
2. Vérifier la structure des données
3. Vérifier les règles Firestore
4. Vérifier que l'utilisateur a les bonnes permissions

### Erreur "User not authenticated"
1. Vérifier que l'utilisateur est bien connecté
2. Vérifier le token d'authentification
3. Redémarrer l'app

### Erreur "PERMISSION_DENIED"
1. Vérifier les règles Firestore
2. Vérifier que `request.auth.uid == userId`
3. Redéployer les règles

---

**Checklist complète ! ✨**

