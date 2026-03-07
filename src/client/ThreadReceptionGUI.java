package client;

import java.io.*;
import commun.Message;

/**
 * Thread qui écoute les messages pour l'interface graphique
 */
public class ThreadReceptionGUI extends Thread {
    
    private ObjectInputStream in;
    private ClientGUI clientGUI;
    private boolean actif;
    
    public ThreadReceptionGUI(ObjectInputStream in, ClientGUI clientGUI) {
        this.in = in;
        this.clientGUI = clientGUI;
        this.actif = true;
    }
    
    @Override
    public void run() {
        try {
            while (actif) {
                Message message = (Message) in.readObject();
                clientGUI.afficherMessageRecu(message);
            }
        } catch (IOException e) {
            if (actif) {
                clientGUI.afficherMessage("❌ Connexion perdue\n");
            }
        } catch (ClassNotFoundException e) {
            clientGUI.afficherMessage("❌ Erreur de lecture\n");
        }
    }
    
    public void arreter() {
        actif = false;
    }
}
