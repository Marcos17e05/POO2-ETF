package org.example.vista;

import org.example.controlador.ControladorAutenticacion;
import org.example.modelo.Usuario;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class VentanaLogin extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtIdentificador;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JLabel lblEstado;

    private final ControladorAutenticacion controlador = new ControladorAutenticacion();

    public VentanaLogin() {
        setTitle("Biblioteca Escolar - Autenticacióon");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(470, 330);
        setResizable(false);
        setLocationRelativeTo(null);

        btnIngresar.addActionListener(e -> ingresar());
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) ingresar();
            }
        });
    }

    private void ingresar() {
        try {
            lblEstado.setText("Validando...");
            Usuario usuario = controlador.autenticar(txtIdentificador.getText(), new String(txtContrasena.getPassword()));
            VentanaPrincipal principal = new VentanaPrincipal(usuario);
            principal.setLocationRelativeTo(this);
            principal.setVisible(true);
            dispose();
        } catch (RuntimeException ex) {
            lblEstado.setText("No fue posible iniciar sesión.");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Autenticación", JOptionPane.WARNING_MESSAGE);
        }
    }
}
