package serveur;

import java.io.*;
import java.net.Socket;
import commun.Message;

/**
 * Thread qui gère la communication avec UN client spécifique
 */
public class GestionnaireClient extends Thread {
    
    private Socket socket;
    private Serveur serveur;
    private String pseudo;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean connecte;
    
    public GestionnaireClient(Socket socket, Serveur serveur) {
        this.socket = socket;
        this.serveur = serveur;
        this.connecte = true;
    }
    
    @Override
    public void run() {
        try {
            // 1. Créer les flux
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            // 2. Recevoir le message de connexion
            Message messageConnexion = (Message) in.readObject();
            this.pseudo = messageConnexion.getExpediteur();
            
            System.out.println("📨 " + pseudo + " s'est connecté");
            
            // 3. Ajouter ce client à la liste
            serveur.ajouterClient(this);
            
            // 4. Envoyer message de bienvenue
            Message bienvenue = new Message(
                Message.Type.MESSAGE,
                "SERVEUR",
                "Bienvenue " + pseudo + " ! 🎉 Il y a " + serveur.getNombreClients() + " client(s) connecté(s)."
            );
            envoyerMessage(bienvenue);
            
            // 5. Notifier TOUS les clients
            Message notification = new Message(
                Message.Type.MESSAGE,
                "SERVEUR",
                "📢 " + pseudo + " a rejoint le chat !"
            );
            serveur.diffuserMessage(notification, this);
            
            // 6. BOUCLE : Recevoir les messages
            while (connecte) {
                Message msg = (Message) in.readObject();
                
                if (msg.getType() == Message.Type.DECONNEXION) {
                    System.out.println("📴 " + pseudo + " s'est déconnecté");
                    connecte = false;
                    
                } else if (msg.getType() == Message.Type.MESSAGE_PRIVE) {
                    // MESSAGE PRIVÉ
                    System.out.println("🔒 [" + pseudo + " → " + msg.getDestinataire() + "]: " + msg.getContenu());
                    
                    // Envoyer SEULEMENT au destinataire
                    serveur.envoyerMessagePrive(msg);
                    
                } else {
                    // Message PUBLIC
                    System.out.println("💬 [" + pseudo + "]: " + msg.getContenu());
                    
                    // DIFFUSER à tous
                    serveur.diffuserMessage(msg, this);
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ Erreur avec " + pseudo + " : " + e.getMessage());
            connecte = false;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Erreur lecture message : " + e.getMessage());
            connecte = false;
        } finally {
            deconnecter();
        }
    }
    
    /**
     * Envoie un message à CE client
     */
    public void envoyerMessage(Message message) {
        try {
            if (out != null && connecte) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur envoi à " + pseudo + " : " + e.getMessage());
            connecte = false;
        }
    }
    
    /**
     * Déconnecte proprement ce client
     */
    private void deconnecter() {
        try {
            connecte = false;
            
            serveur.retirerClient(this);
            
            Message notification = new Message(
                Message.Type.MESSAGE,
                "SERVEUR",
                "📢 " + pseudo + " a quitté le chat."
            );
            serveur.diffuserMessage(notification, null);
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            
        } catch (IOException e) {
            System.err.println("❌ Erreur fermeture : " + e.getMessage());
        }
    }
    
    /**
     * Retourne le pseudo
     */
    public String getPseudo() {
        return pseudo;
    }
}