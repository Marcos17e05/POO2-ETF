package org.example.vista;

import org.example.controlador.ControladorCategorias;
import org.example.modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VentanaGestionCategorias extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtNombre;
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRecargar;
    private JTable tablaCategorias;

    private final ControladorCategorias controlador = new ControladorCategorias();
    private int idSeleccionado = 0;

    public VentanaGestionCategorias() {
        setTitle("Biblioteca - Gestión de categorías");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 480);
        setLocationRelativeTo(null);

        configurarTabla();
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRecargar.addActionListener(e -> cargarTabla());
        tablaCategorias.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        cargarTabla();
    }

    private void configurarTabla() {
        tablaCategorias.setModel(new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarTabla() {
        try {
            List<Categoria> categorias = controlador.listar();
            DefaultTableModel modelo = (DefaultTableModel) tablaCategorias.getModel();
            modelo.setRowCount(0);
            for (Categoria c : categorias) modelo.addRow(new Object[]{c.getId(), c.getNombre()});
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cargarSeleccion() {
        int fila = tablaCategorias.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = (int) tablaCategorias.getValueAt(fila, 0);
            txtNombre.setText(String.valueOf(tablaCategorias.getValueAt(fila, 1)));
        }
    }

    private void registrar() {
        try {
            controlador.registrar(txtNombre.getText());
            JOptionPane.showMessageDialog(this, "Categoría registrada correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        try {
            controlador.actualizar(idSeleccionado, txtNombre.getText());
            JOptionPane.showMessageDialog(this, "Categoría actualizada correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) {
            mostrarError(new IllegalArgumentException("Seleccione una categoría de la tabla."));
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar la categoría seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        try {
            controlador.eliminar(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.setText("");
        tablaCategorias.clearSelection();
        txtNombre.requestFocus();
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
