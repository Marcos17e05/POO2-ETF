package org.example.vista;

import org.example.controlador.ControladorLibros;
import org.example.modelo.Categoria;
import org.example.modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VentanaGestionLibros extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtIsbn;
    private JTextField txtEditorial;
    private JTextField txtStock;
    private JComboBox<Categoria> comboCategoria;
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRecargar;
    private JTable tablaLibros;

    private final ControladorLibros controlador = new ControladorLibros();
    private int idSeleccionado = 0;

    public VentanaGestionLibros() {
        setTitle("Biblioteca - Gestión de libros");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 650);
        setLocationRelativeTo(null);

        configurarTabla();
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRecargar.addActionListener(e -> recargar());
        tablaLibros.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        recargar();
    }

    private void configurarTabla() {
        tablaLibros.setModel(new DefaultTableModel(new Object[]{"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void recargar() {
        try {
            comboCategoria.removeAllItems();
            for (Categoria c : controlador.listarCategorias()) comboCategoria.addItem(c);
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cargarTabla() {
        List<Libro> libros = controlador.listar();
        DefaultTableModel modelo = (DefaultTableModel) tablaLibros.getModel();
        modelo.setRowCount(0);
        for (Libro l : libros) {
            modelo.addRow(new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(), l.getEditorial(), l.getStock(), l.getCategoria().getNombre()});
        }
    }

    private void cargarSeleccion() {
        int fila = tablaLibros.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) tablaLibros.getValueAt(fila, 0);
        txtTitulo.setText(String.valueOf(tablaLibros.getValueAt(fila, 1)));
        txtAutor.setText(String.valueOf(tablaLibros.getValueAt(fila, 2)));
        txtIsbn.setText(String.valueOf(tablaLibros.getValueAt(fila, 3)));
        txtEditorial.setText(String.valueOf(tablaLibros.getValueAt(fila, 4)));
        txtStock.setText(String.valueOf(tablaLibros.getValueAt(fila, 5)));
        String categoria = String.valueOf(tablaLibros.getValueAt(fila, 6));
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).getNombre().equals(categoria)) {
                comboCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void registrar() {
        try {
            controlador.registrar(txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(), txtEditorial.getText(), txtStock.getText(), (Categoria) comboCategoria.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Libro registrado correctamente.");
            limpiar();
            recargar();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        try {
            controlador.actualizar(idSeleccionado, txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(), txtEditorial.getText(), txtStock.getText(), (Categoria) comboCategoria.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Libro actualizado correctamente.");
            limpiar();
            recargar();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) {
            mostrarError(new IllegalArgumentException("Seleccione un libro de la tabla."));
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el libro seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        try {
            controlador.eliminar(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.");
            limpiar();
            recargar();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        if (comboCategoria.getItemCount() > 0) comboCategoria.setSelectedIndex(0);
        tablaLibros.clearSelection();
        txtTitulo.requestFocus();
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
