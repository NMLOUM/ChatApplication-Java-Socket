package client;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TestSwing {
    public static void main(String[] args) {
        // Créer une fenêtre simple
        JFrame fenetre = new JFrame("Test Swing");
        fenetre.setSize(400, 300);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Ajouter un bouton
        JButton bouton = new JButton("Cliquez-moi !");
        bouton.addActionListener(e -> {
            JOptionPane.showMessageDialog(fenetre, "Swing fonctionne parfaitement ! ✅");
        });
        
        fenetre.add(bouton);
        fenetre.setVisible(true);
    }
}