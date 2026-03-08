# 💬 Application Chat Client-Serveur Java

Application de chat multi-utilisateurs en temps réel utilisant les sockets TCP/IP avec interface graphique Swing.

## Description

Application de messagerie instantanée développée en Java permettant à plusieurs utilisateurs de communiquer en temps réel via un réseau local ou distant.

### Fonctionnalités Principales

✅ **Communication multi-clients** (jusqu'à 50 utilisateurs simultanés)
✅ **Messages publics** (diffusion à tous)
✅ **Messages privés** (@pseudo)
✅ **Commandes avancées** (/help, /users, /quit)
✅ **Double interface** : Console et GUI (Swing)
✅ **Multi-threading** : Un thread par client
✅ **Architecture modulaire** : 3 packages bien organisés

## Technologies Utilisées

- **Langage** : Java
- **Protocole** : TCP/IP (Sockets)
- **Interface** : Swing + AWT
- **Multi-threading** : java.lang.Thread
- **Sérialisation** : ObjectInputStream/ObjectOutputStream

##  Structure du Projet
ChatApplication/
├── src/
│   ├── commun/
│   │   └── Message.java              # Classe de message sérialisable
│   ├── serveur/
│   │   ├── Serveur.java              # Serveur principal
│   │   └── GestionnaireClient.java   # Thread par client
│   └── client/
│       ├── Client.java               # Client console
│       ├── ThreadReception.java      # Réception console
│       ├── ClientGUI.java            # Client interface graphique
│       └── ThreadReceptionGUI.java   # Réception GUI

## Installation et Utilisation

### Prérequis

- Java JDK 8 ou supérieur
- IDE Java (Eclipse, IntelliJ IDEA, NetBeans) ou ligne de commande

### Compilation
# Compiler tous les fichiers
javac -d bin src/commun/*.java src/serveur/*.java src/client/*.java

### Exécution

1. Démarrer le serveur :
java -cp bin serveur.Serveur

2. Lancer un client (console) :
java -cp bin client.Client
```

**3. Lancer un client (interface graphique)
java -cp bin client.ClientGUI
```

## Guide d'Utilisation

### Commandes Disponibles

| Commande | Description |
|----------|-------------|
| `/help` | Affiche la liste des commandes |
| `/users` | Liste des utilisateurs connectés |
| `/quit` | Se déconnecter proprement |
| `@pseudo message` | Envoyer un message privé |
| `texte` | Message public à tous |

### Exemple d'Utilisation

1. Lancez le serveur (port 12345 par défaut)
2. Connectez 2 clients avec des pseudos différents
3. Tapez un message pour communiquer
4. Utilisez `@marie Salut !` pour un message privé
5. Tapez `/help` pour voir les commandes

## Architecture

### Modèle Client-Serveur
CLIENT 1 ──┐
CLIENT 2 ──┼──> SERVEUR (Port 12345) ──> Redistribution
CLIENT 3 ──┘

 Multi-threading

- Thread principal : Accepte les nouvelles connexions
-  thread par client: Gestion individuelle de chaque utilisateur
- Synchronisation : `synchronized` pour éviter les conflits

 Compétences Acquises

- Programmation réseau (Sockets TCP/IP)
- Multi-threading et synchronisation
- Architecture Client-Serveur
- Sérialisation d'objets Java
- Interface graphique Swing
- Gestion d'erreurs et exceptions

Améliorations Futures

- Chiffrement SSL/TLS
- Base de données (historique persistant)
- Salons de discussion (rooms)

  Auteur

  Ndeye Maguette Loum &
  Maty Diop
