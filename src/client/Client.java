package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import commun.Message;

public class Client {

    private String host;
    private int port;
    private String pseudo;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ThreadReception threadReception;

    public Client(String host, int port, String pseudo) {
        this.host = host;
        this.port = port;
        this.pseudo = pseudo;
    }

    public void connecter() {
        try {
            // 1. Se connecter au serveur
            System.out.println("🔌 Connexion au serveur " + host + ":" + port + "...");
            socket = new Socket(host, port);
            System.out.println("✅ Connecté au serveur !");

            // 2. Créer les flux
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // 3. Envoyer message de connexion
            Message messageConnexion = new Message(
                Message.Type.CONNEXION,
                pseudo,
                "Demande de connexion"
            );
            out.writeObject(messageConnexion);
            out.flush();
            System.out.println("✅ Message de connexion envoyé");

            // 4. Démarrer le thread de RÉCEPTION
            threadReception = new ThreadReception(in);
            threadReception.start();
            System.out.println("🎧 Thread de réception démarré");

            // 5. BOUCLE d'ENVOI de messages
            Scanner scanner = new Scanner(System.in);
            boolean continuer = true;
            
            System.out.println("\n💡 Tapez vos messages (tapez '/help' pour l'aide)\n");

            while (continuer) {
                System.out.print("💬 Vous : ");
                String texte = scanner.nextLine();

                if (texte.equalsIgnoreCase("/quit")) {
                    // Message de déconnexion
                    Message msgDeconnexion = new Message(
                        Message.Type.DECONNEXION,
                        pseudo,
                        "Au revoir"
                    );
                    out.writeObject(msgDeconnexion);
                    out.flush();
                    System.out.println("👋 Déconnexion...");
                    continuer = false;
                    
                } else if (texte.equalsIgnoreCase("/users")) {
                    // Commande /users (à implémenter plus tard)
                    System.out.println("⚠️ Commande /users pas encore implémentée");
                    
                } else if (texte.equalsIgnoreCase("/help")) {
                    // Commande /help
                    afficherAide();
                    
                } else if (texte.startsWith("@")) {
                    // MESSAGE PRIVÉ
                    // Format: @pseudo message
                    String[] parties = texte.substring(1).split(" ", 2);
                    
                    if (parties.length >= 2) {
                        String destinataire = parties[0];
                        String contenu = parties[1];
                        
                        Message msgPrive = new Message(
                            Message.Type.MESSAGE_PRIVE,
                            pseudo,
                            destinataire,
                            contenu
                        );
                        out.writeObject(msgPrive);
                        out.flush();
                    } else {
                        System.out.println("⚠️ Format incorrect. Utilisez: @pseudo votre message");
                    }
                    
                } else if (!texte.trim().isEmpty()) {
                    // Message PUBLIC normal
                    Message msg = new Message(
                        Message.Type.MESSAGE,
                        pseudo,
                        texte
                    );
                    out.writeObject(msg);
                    out.flush();
                }
            }

            // 6. Fermer tout
            threadReception.arreter();
            scanner.close();
            in.close();
            out.close();
            socket.close();

            System.out.println("✅ Déconnexion réussie");

        } catch (UnknownHostException e) {
            System.err.println("❌ Serveur introuvable : " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Erreur connexion : " + e.getMessage());
        }
    }
    
    /**
     * Affiche l'aide des commandes
     */
    private void afficherAide() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       COMMANDES DISPONIBLES            ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ message       - Envoyer un message     ║");
        System.out.println("║ @pseudo msg   - Message privé          ║");
        System.out.println("║ /users        - Liste des utilisateurs ║");
        System.out.println("║ /help         - Afficher cette aide    ║");
        System.out.println("║ /quit         - Se déconnecter         ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre pseudo : ");
        String pseudo = scanner.nextLine();

        Client client = new Client("localhost", 12345, pseudo);
        client.connecter();

        scanner.close();
    }
}