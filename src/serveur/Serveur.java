package serveur;

import java.io.*;
import java.net.*;
import java.util.*;
import commun.Message;

public class Serveur {
    
    private int port;
    private ServerSocket serverSocket;
    private List<GestionnaireClient> clients;
    
    public Serveur(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }
    
    public void demarrer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("✅ Serveur démarré sur le port " + port);
            System.out.println("🔄 Le serveur tourne en continu...");
            System.out.println("👥 Prêt à accepter plusieurs clients simultanés !\n");
            
            while (true) {
                System.out.println("⏳ En attente d'un nouveau client...");
                
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ Nouvelle connexion détectée !");
                
                GestionnaireClient gestionnaire = new GestionnaireClient(clientSocket, this);
                gestionnaire.start();
                
                System.out.println("🚀 Thread créé pour gérer le client\n");
            }
            
        } catch (IOException e) {
            System.err.println("❌ Erreur serveur : " + e.getMessage());
        }
    }
    
    /**
     * Ajoute un client à la liste
     */
    public synchronized void ajouterClient(GestionnaireClient client) {
        clients.add(client);
        System.out.println("📋 Client ajouté. Total : " + clients.size() + " client(s) connecté(s)\n");
    }
    
    /**
     * Retire un client de la liste
     */
    public synchronized void retirerClient(GestionnaireClient client) {
        clients.remove(client);
        System.out.println("📋 Client retiré. Total : " + clients.size() + " client(s) connecté(s)\n");
    }
    
    /**
     * Diffuse un message à TOUS les clients (sauf l'expéditeur optionnel)
     */
    public synchronized void diffuserMessage(Message message, GestionnaireClient expediteur) {
        System.out.println("📡 Diffusion du message à " + clients.size() + " client(s)");
        
        for (GestionnaireClient client : clients) {
            if (expediteur == null || !client.equals(expediteur)) {
                client.envoyerMessage(message);
            }
        }
    }
    
    /**
     * Envoie un message privé à un client spécifique (NOUVELLE MÉTHODE)
     */
    public synchronized void envoyerMessagePrive(Message message) {
        String destinataire = message.getDestinataire();
        String expediteur = message.getExpediteur();
        
        // Trouver le destinataire
        GestionnaireClient clientDest = null;
        for (GestionnaireClient client : clients) {
            if (client.getPseudo().equals(destinataire)) {
                clientDest = client;
                break;
            }
        }
        
        if (clientDest != null) {
            // Envoyer au destinataire
            clientDest.envoyerMessage(message);
            
            // Envoyer aussi à l'expéditeur (pour confirmation)
            GestionnaireClient clientExp = null;
            for (GestionnaireClient client : clients) {
                if (client.getPseudo().equals(expediteur)) {
                    clientExp = client;
                    break;
                }
            }
            if (clientExp != null) {
                clientExp.envoyerMessage(message);
            }
            
            System.out.println("📨 Message privé transmis: " + expediteur + " → " + destinataire);
        } else {
            // Destinataire introuvable
            System.out.println("⚠️ Destinataire introuvable: " + destinataire);
            
            // Notifier l'expéditeur
            for (GestionnaireClient client : clients) {
                if (client.getPseudo().equals(expediteur)) {
                    Message erreur = new Message(
                        Message.Type.MESSAGE,
                        "SERVEUR",
                        "❌ Utilisateur '" + destinataire + "' introuvable."
                    );
                    client.envoyerMessage(erreur);
                    break;
                }
            }
        }
    }
    
    /**
     * Retourne le nombre de clients
     */
    public synchronized int getNombreClients() {
        return clients.size();
    }
    
    public static void main(String[] args) {
        Serveur serveur = new Serveur(12345);
        serveur.demarrer();
    }
}