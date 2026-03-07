package commun;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe qui représente un message dans notre chat
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Types de messages
    public enum Type {
        CONNEXION,      // Quand un client se connecte
        DECONNEXION,    // Quand un client se déconnecte
        MESSAGE,        // Message public
        MESSAGE_PRIVE   // Message privé (NOUVEAU)
    }
    
    // Attributs
    private Type type;
    private String expediteur;
    private String destinataire;  // NOUVEAU
    private String contenu;
    private Date horodatage;
    
    // Constructeur pour message PUBLIC
    public Message(Type type, String expediteur, String contenu) {
        this.type = type;
        this.expediteur = expediteur;
        this.destinataire = "TOUS";
        this.contenu = contenu;
        this.horodatage = new Date();
    }
    
    // Constructeur pour message PRIVÉ (NOUVEAU)
    public Message(Type type, String expediteur, String destinataire, String contenu) {
        this.type = type;
        this.expediteur = expediteur;
        this.destinataire = destinataire;
        this.contenu = contenu;
        this.horodatage = new Date();
    }
    
    // Getters
    public Type getType() {
        return type;
    }
    
    public String getExpediteur() {
        return expediteur;
    }
    
    public String getDestinataire() {  // NOUVEAU
        return destinataire;
    }
    
    public String getContenu() {
        return contenu;
    }
    
    public Date getHorodatage() {
        return horodatage;
    }
    
    // Affichage
    @Override
    public String toString() {
        if (type == Type.MESSAGE_PRIVE) {
            return "🔒 [" + expediteur + " → " + destinataire + " (privé)]: " + contenu;
        } else {
            return "[" + expediteur + "]: " + contenu;
        }
    }
}