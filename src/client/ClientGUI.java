package client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import commun.Message;

/**
 * Interface graphique du client de chat
 */
public class ClientGUI extends JFrame {
    
    // Composants graphiques
    private JTextArea zoneMessages;
    private JTextField champMessage;
    private JButton btnEnvoyer;
    private JButton btnActualiser;
    private JButton btnDeconnecter;
    private JList<String> listeUtilisateurs;
    private DefaultListModel<String> modeleUtilisateurs;
    private JLabel lblStatut;
    private JLabel lblNomUtilisateur;
    
    // Réseau
    private String host;
    private int port;
    private String pseudo;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ThreadReceptionGUI threadReception;
    private boolean connecte;
    
    /**
     * Constructeur
     */
    public ClientGUI(String host, int port, String pseudo) {
        this.host = host;
        this.port = port;
        this.pseudo = pseudo;
        this.connecte = false;
        
        // Configuration de la fenêtre
        setTitle("💬 Chat Client-Serveur");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Créer l'interface
        creerInterface();
        
        // Connecter au serveur
        connecter();
    }
    
    /**
     * Crée l'interface graphique
     */
    private void creerInterface() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelPrincipal.setBackground(new Color(245, 245, 245));
        
        // === HAUT : Info utilisateur ===
        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.setBackground(new Color(52, 152, 219));
        panelHaut.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        lblNomUtilisateur = new JLabel("Connecté en tant que: " + pseudo);
        lblNomUtilisateur.setFont(new Font("Arial", Font.BOLD, 14));
        lblNomUtilisateur.setForeground(Color.WHITE);
        
        lblStatut = new JLabel("🟢 En ligne");
        lblStatut.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatut.setForeground(Color.WHITE);
        
        panelHaut.add(lblNomUtilisateur, BorderLayout.WEST);
        panelHaut.add(lblStatut, BorderLayout.EAST);
        
        // === CENTRE : Zone messages + Liste utilisateurs ===
        JPanel panelCentre = new JPanel(new BorderLayout(10, 0));
        panelCentre.setBackground(new Color(245, 245, 245));
        
        // Zone des messages (à gauche)
        zoneMessages = new JTextArea();
        zoneMessages.setEditable(false);
        zoneMessages.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        zoneMessages.setLineWrap(true);
        zoneMessages.setWrapStyleWord(true);
        zoneMessages.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollMessages = new JScrollPane(zoneMessages);
        scrollMessages.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Messages",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 152, 219)
        ));
        
        // Panel des utilisateurs (à droite)
        JPanel panelUtilisateurs = new JPanel(new BorderLayout(5, 5));
        panelUtilisateurs.setPreferredSize(new Dimension(200, 0));
        panelUtilisateurs.setBackground(new Color(245, 245, 245));
        
        modeleUtilisateurs = new DefaultListModel<>();
        modeleUtilisateurs.addElement("• " + pseudo + " (vous)");
        
        listeUtilisateurs = new JList<>(modeleUtilisateurs);
        listeUtilisateurs.setFont(new Font("Arial", Font.PLAIN, 12));
        listeUtilisateurs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollUtilisateurs = new JScrollPane(listeUtilisateurs);
        scrollUtilisateurs.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Utilisateurs",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(46, 204, 113)
        ));
        
        // Boutons utilisateurs
        JPanel panelBoutonsUsers = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBoutonsUsers.setBackground(new Color(245, 245, 245));
        
        btnActualiser = new JButton("🔄 Actualiser");
        btnActualiser.setFont(new Font("Arial", Font.PLAIN, 11));
        btnActualiser.setFocusPainted(false);
        btnActualiser.addActionListener(e -> demanderListeUtilisateurs());
        
        btnDeconnecter = new JButton("🚪 Déconnecter");
        btnDeconnecter.setFont(new Font("Arial", Font.PLAIN, 11));
        btnDeconnecter.setFocusPainted(false);
        btnDeconnecter.setBackground(new Color(231, 76, 60));
        btnDeconnecter.setForeground(Color.WHITE);
        btnDeconnecter.addActionListener(e -> deconnecter());
        
        panelBoutonsUsers.add(btnActualiser);
        panelBoutonsUsers.add(btnDeconnecter);
        
        panelUtilisateurs.add(scrollUtilisateurs, BorderLayout.CENTER);
        panelUtilisateurs.add(panelBoutonsUsers, BorderLayout.SOUTH);
        
        panelCentre.add(scrollMessages, BorderLayout.CENTER);
        panelCentre.add(panelUtilisateurs, BorderLayout.EAST);
        
        // === BAS : Zone de saisie ===
        JPanel panelBas = new JPanel(new BorderLayout(10, 0));
        panelBas.setBackground(new Color(245, 245, 245));
        panelBas.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        champMessage = new JTextField();
        champMessage.setFont(new Font("Arial", Font.PLAIN, 13));
        champMessage.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        champMessage.addActionListener(e -> envoyerMessage());
        
        btnEnvoyer = new JButton("📤 Envoyer");
        btnEnvoyer.setFont(new Font("Arial", Font.BOLD, 13));
        btnEnvoyer.setPreferredSize(new Dimension(120, 35));
        btnEnvoyer.setBackground(new Color(52, 152, 219));
        btnEnvoyer.setForeground(Color.WHITE);
        btnEnvoyer.setFocusPainted(false);
        btnEnvoyer.addActionListener(e -> envoyerMessage());
        
        panelBas.add(new JLabel("Message: "), BorderLayout.WEST);
        panelBas.add(champMessage, BorderLayout.CENTER);
        panelBas.add(btnEnvoyer, BorderLayout.EAST);
        
        // Assembler tout
        panelPrincipal.add(panelHaut, BorderLayout.NORTH);
        panelPrincipal.add(panelCentre, BorderLayout.CENTER);
        panelPrincipal.add(panelBas, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    /**
     * Connecte au serveur
     */
    private void connecter() {
        try {
            afficherMessage("🔌 Connexion au serveur " + host + ":" + port + "...\n");
            
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            connecte = true;
            afficherMessage("✅ Connecté au serveur !\n");
            
            // Envoyer message de connexion
            Message msgConnexion = new Message(
                Message.Type.CONNEXION,
                pseudo,
                "Demande de connexion"
            );
            out.writeObject(msgConnexion);
            out.flush();
            
            // Démarrer thread de réception
            threadReception = new ThreadReceptionGUI(in, this);
            threadReception.start();
            
        } catch (IOException e) {
            afficherMessage("❌ Erreur de connexion: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(this,
                "Impossible de se connecter au serveur.\nVérifiez que le serveur est démarré.",
                "Erreur de connexion",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    /**
     * Envoie un message
     */
    private void envoyerMessage() {
        String texte = champMessage.getText().trim();
        
        if (texte.isEmpty()) {
            return;
        }
        
        try {
            if (texte.equalsIgnoreCase("/quit")) {
                deconnecter();
                return;
            }
         // Commande /help
            if (texte.equalsIgnoreCase("/help")) {
                afficherAide();
                champMessage.setText("");
                return;
            }
            
            if (texte.equalsIgnoreCase("/users")) {
                demanderListeUtilisateurs();
                champMessage.setText("");
                return;
            }
            
            if (texte.startsWith("@")) {
                // Message privé
                String[] parties = texte.substring(1).split(" ", 2);
                if (parties.length >= 2) {
                    String destinataire = parties[0];
                    String contenu = parties[1];
                    
                    Message msg = new Message(
                        Message.Type.MESSAGE_PRIVE,
                        pseudo,
                        destinataire,
                        contenu
                    );
                    out.writeObject(msg);
                    out.flush();
                } else {
                    afficherMessage("⚠️ Format: @pseudo message\n");
                }
            } else {
                // Message public
                Message msg = new Message(
                    Message.Type.MESSAGE,
                    pseudo,
                    texte
                );
                out.writeObject(msg);
                out.flush();
            }
            
            champMessage.setText("");
            
        } catch (IOException e) {
            afficherMessage("❌ Erreur d'envoi: " + e.getMessage() + "\n");
        }
    }
    
    private void afficherAide() {
		// TODO Auto-generated method stub
		
	}

    private void demanderListeUtilisateurs() {
        afficherMessage("\n📋 Liste des utilisateurs connectés :\n");
        for (int i = 0; i < modeleUtilisateurs.getSize(); i++) {
            afficherMessage("   " + modeleUtilisateurs.getElementAt(i) + "\n");
        }
        afficherMessage("\n");
    }
    /**
     * Affiche l'aide des commandes
     */
    private void afficherAide1() {
        afficherMessage("\n╔════════════════════════════════════════╗\n");
        afficherMessage("║         AIDE - COMMANDES              ║\n");
        afficherMessage("╠════════════════════════════════════════╣\n");
        afficherMessage("║ /help            Afficher cette aide  ║\n");
        afficherMessage("║ /users           Liste des utilisateurs║\n");
        afficherMessage("║ /quit            Se déconnecter       ║\n");
        afficherMessage("║ @pseudo message  Message privé        ║\n");
        afficherMessage("║ texte            Message public       ║\n");
        afficherMessage("╚════════════════════════════════════════╝\n\n");
    }
    
    /**
     * Déconnexion
     */
    private void deconnecter() {
        try {
            if (connecte) {
                Message msgDeconnexion = new Message(
                    Message.Type.DECONNEXION,
                    pseudo,
                    "Au revoir"
                );
                out.writeObject(msgDeconnexion);
                out.flush();
                
                connecte = false;
                lblStatut.setText("🔴 Déconnecté");
                afficherMessage("👋 Déconnexion...\n");
                
                if (threadReception != null) {
                    threadReception.arreter();
                }
                
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
                
                btnEnvoyer.setEnabled(false);
                champMessage.setEnabled(false);
                btnActualiser.setEnabled(false);
            }
        } catch (IOException e) {
            afficherMessage("❌ Erreur déconnexion: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Affiche un message dans la zone de texte
     */
    public void afficherMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String heure = new SimpleDateFormat("HH:mm").format(new Date());
            zoneMessages.append("[" + heure + "] " + message);
            zoneMessages.setCaretPosition(zoneMessages.getDocument().getLength());
        });
    }
    
    /**
     * Affiche un message reçu
     */
    public void afficherMessageRecu(Message message) {
        SwingUtilities.invokeLater(() -> {
            String heure = new SimpleDateFormat("HH:mm").format(message.getHorodatage());
            String texte = "[" + heure + "] " + message.toString() + "\n";
            zoneMessages.append(texte);
            zoneMessages.setCaretPosition(zoneMessages.getDocument().getLength());
        });
    }
    
    /**
     * Point d'entrée
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Demander les informations de connexion
            JTextField champPseudo = new JTextField();
            JTextField champServeur = new JTextField("localhost");
            JTextField champPort = new JTextField("12345");
            
            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("Pseudo:"));
            panel.add(champPseudo);
            panel.add(new JLabel("Serveur:"));
            panel.add(champServeur);
            panel.add(new JLabel("Port:"));
            panel.add(champPort);
            
            int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Connexion au Chat",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String pseudo = champPseudo.getText().trim();
                String serveur = champServeur.getText().trim();
                int port = Integer.parseInt(champPort.getText().trim());
                
                if (!pseudo.isEmpty()) {
                    ClientGUI client = new ClientGUI(serveur, port, pseudo);
                    client.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Le pseudo est obligatoire !");
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        });
    }
}