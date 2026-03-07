package client;

import java.io.*;
import commun.Message;

/**
 * Thread qui écoute en permanence les messages du serveur
 */
public class ThreadReception extends Thread {
    
    private ObjectInputStream in;
    private boolean actif;
    
    public ThreadReception(ObjectInputStream in) {
        this.in = in;
        this.actif = true;
    }
    
    @Override
    public void run() {
        try {
            while (actif) {
                Message message = (Message) in.readObject();
                System.out.println("\n📨 " + message.toString());
                System.out.print("💬 Vous : ");
            }
            
        } catch (IOException e) {
            if (actif) {
                System.err.println("\n❌ Connexion perdue avec le serveur");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("\n❌ Erreur de lecture");
        }
    }
    
    public void arreter() {
        actif = false;
    }
}
