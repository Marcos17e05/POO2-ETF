package org.example;

import org.example.vista.VentanaLogin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("No se pudo aplicar el Look & Feel del sistema.");
        }

        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventana = new VentanaLogin();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
