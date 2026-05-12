# Configuration Firestore pour les Favoris

## Étape 1 : Vérifier que Firestore est activé

1. Allez à [Firebase Console](https://console.firebase.google.com)
2. Sélectionnez votre projet "moviesapp-2d551"
3. Dans le menu de gauche, cliquez sur "Firestore Database"
4. Si vous voyez "Create database", cliquez et créez une base de données en mode de test (pour le développement)

## Étape 2 : Configurer les règles de sécurité

1. Dans Firestore Database, allez à l'onglet "Règles"
2. Remplacez tout le contenu par les règles ci-dessous :

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Permet aux utilisateurs de lire/écrire leurs propres favoris
    match /users/{userId}/favorites/{document=**} {
      allow read, write: if request.auth.uid == userId;
    }
    
    // Permet aux utilisateurs de lire/écrire leurs propres données de profil
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

3. Cliquez sur "Publier"

## Étape 3 : Activer l'authentification Firebase

1. Dans le menu de gauche, cliquez sur "Authentication"
2. Allez à l'onglet "Sign-in method"
3. Assurez-vous que les méthodes suivantes sont activées :
   - Email/Password
   - Google Sign-In

## Vérification de la structure

Après l'ajout d'un film aux favoris, vous devriez voir :

```
Firestore Database
└── users (collection)
    └── {userId} (document - ID de l'utilisateur authentifié)
        └── favorites (collection)
            └── {movieId} (document - ID du film)
                ├── movieId: 550
                ├── movieName: "Fight Club"
                ├── movieDate: "1999-10-15"
                ├── movieImage: "/pB8BM50VVRVIRP3EL6NSUWBPIMH.jpg"
                └── addedAt: Timestamp
```

## Mode de développement vs Production

### Mode de test (Développement)
Utilisez les règles ci-dessus avec `request.auth.uid == userId` pour permettre uniquement aux utilisateurs authentifiés.

### Mode production
Assurez-vous que les règles de sécurité sont correctement configurées et que personne ne peut accéder aux données des autres utilisateurs.

## Dépannage

### Erreur : "Missing or insufficient permissions"
- Vérifiez que vous êtes connecté
- Vérifiez que les règles Firestore permettent l'accès pour votre UID
- Vérifiez les logs Firestore pour plus de détails

### Erreur : "PERMISSION_DENIED"
- Assurez-vous que les règles autorisent `request.auth.uid == userId`
- Redémarrez l'application
- Vérifiez que votre token d'authentification n'a pas expiré

### Les favoris ne s'affichent pas
- Vérifiez que la collection `users/{userId}/favorites` existe
- Vérifiez les données manuellement dans Firestore Console
- Vérifiez les logs logcat pour les erreurs

## Test manuel dans Firestore Console

1. Allez à Firestore Database > Données
2. Créez une collection `users`
3. Créez un document avec l'ID de votre utilisateur (trouvez-le dans Authentication > Users)
4. Dans ce document, créez une collection `favorites`
5. Ajoutez manuellement un document de film pour tester l'affichage

## Ressources

- [Documentation Firestore](https://firebase.google.com/docs/firestore)
- [Règles de sécurité Firestore](https://firebase.google.com/docs/firestore/security)
- [Guide d'authentification Firebase](https://firebase.google.com/docs/auth)

