package org.example.vista;

import org.example.modelo.Usuario;

import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    private JPanel panelPrincipal;
    private JLabel lblBienvenida;
    private JLabel lblRol;
    private JButton btnLibros;
    private JButton btnCategorias;
    private JButton btnEstudiantes;
    private JButton btnPrestamos;
    private JButton btnReportes;
    private JButton btnUsuarios;
    private JButton btnCerrarSesion;

    private final Usuario usuarioActual;

    public VentanaPrincipal(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        setTitle("Biblioteca Escolar - Menú principal");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(620, 560);
        setResizable(false);
        setLocationRelativeTo(null);

        lblBienvenida.setText("Bienvenido/a, " + usuarioActual.getNombre());
        lblRol.setText("Rol: " + usuarioActual.getRol().getValorBD());

        boolean bibliotecario = usuarioActual.esBibliotecario();
        btnLibros.setEnabled(bibliotecario);
        btnCategorias.setEnabled(bibliotecario);
        btnEstudiantes.setEnabled(bibliotecario);
        btnUsuarios.setEnabled(bibliotecario);
        btnReportes.setEnabled(bibliotecario);

        btnLibros.addActionListener(e -> abrir(new VentanaGestionLibros()));
        btnCategorias.addActionListener(e -> abrir(new VentanaGestionCategorias()));
        btnEstudiantes.addActionListener(e -> abrir(new VentanaGestionEstudiantes()));
        btnPrestamos.addActionListener(e -> abrir(new VentanaPrestamos(usuarioActual)));
        btnReportes.addActionListener(e -> abrir(new VentanaReportes()));
        btnUsuarios.addActionListener(e -> abrir(new VentanaGestionUsuarios()));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    private void abrir(JFrame ventana) {
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
    }

    private void cerrarSesion() {
        VentanaLogin login = new VentanaLogin();
        login.setLocationRelativeTo(this);
        login.setVisible(true);
        dispose();
    }
}
