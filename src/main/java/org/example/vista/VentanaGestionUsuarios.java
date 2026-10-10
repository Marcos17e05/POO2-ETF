package org.example.vista;

import org.example.controlador.ControladorUsuarios;
import org.example.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VentanaGestionUsuarios extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRecargar;
    private JTable tablaUsuarios;

    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private int idSeleccionado = 0;

    public VentanaGestionUsuarios() {
        setTitle("Biblioteca - Gestión de bibliotecarios");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(880, 590);
        setLocationRelativeTo(null);

        configurarTabla();
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRecargar.addActionListener(e -> cargarTabla());
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        cargarTabla();
    }

    private void configurarTabla() {
        tablaUsuarios.setModel(new DefaultTableModel(new Object[]{"ID", "Nombre", "RUT", "Correo", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarTabla() {
        try {
            List<Usuario> lista = controlador.listar();
            DefaultTableModel modelo = (DefaultTableModel) tablaUsuarios.getModel();
            modelo.setRowCount(0);
            for (Usuario u : lista) {
                if (u.esBibliotecario())
                    modelo.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getRut(), u.getCorreo(), u.getRol().getValorBD()});
            }
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cargarSeleccion() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) tablaUsuarios.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tablaUsuarios.getValueAt(fila, 1)));
        txtRut.setText(String.valueOf(tablaUsuarios.getValueAt(fila, 2)));
        txtCorreo.setText(String.valueOf(tablaUsuarios.getValueAt(fila, 3)));
        txtContrasena.setText("");
    }

    private void registrar() {
        try {
            controlador.registrarBibliotecario(txtNombre.getText(), txtRut.getText(), txtCorreo.getText(), new String(txtContrasena.getPassword()));
            JOptionPane.showMessageDialog(this, "Bibliotecario registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        try {
            controlador.actualizarBibliotecario(idSeleccionado, txtNombre.getText(), txtRut.getText(), txtCorreo.getText(), new String(txtContrasena.getPassword()));
            JOptionPane.showMessageDialog(this, "Bibliotecario actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) {
            mostrarError(new IllegalArgumentException("Seleccione un bibliotecario de la tabla."));
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el bibliotecario seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        try {
            controlador.eliminar(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Bibliotecario eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.setText("");
        txtRut.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        tablaUsuarios.clearSelection();
        txtNombre.requestFocus();
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
