package org.example.vista;

import org.example.controlador.ControladorEstudiantes;
import org.example.modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VentanaGestionEstudiantes extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtCurso;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRecargar;
    private JTable tablaEstudiantes;

    private final ControladorEstudiantes controlador = new ControladorEstudiantes();
    private int idSeleccionado = 0;
    private String rutOriginal = "";

    public VentanaGestionEstudiantes() {
        setTitle("Biblioteca - Gestión de estudiantes");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(930, 620);
        setLocationRelativeTo(null);

        configurarTabla();
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRecargar.addActionListener(e -> cargarTabla());
        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        cargarTabla();
    }

    private void configurarTabla() {
        tablaEstudiantes.setModel(new DefaultTableModel(new Object[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarTabla() {
        try {
            List<Estudiante> lista = controlador.listar();
            DefaultTableModel modelo = (DefaultTableModel) tablaEstudiantes.getModel();
            modelo.setRowCount(0);
            for (Estudiante e : lista)
                modelo.addRow(new Object[]{e.getIdEstudiante(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cargarSeleccion() {
        int fila = tablaEstudiantes.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) tablaEstudiantes.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tablaEstudiantes.getValueAt(fila, 1)));
        rutOriginal = String.valueOf(tablaEstudiantes.getValueAt(fila, 2));
        txtRut.setText(rutOriginal);
        txtCurso.setText(String.valueOf(tablaEstudiantes.getValueAt(fila, 3)));
        txtCorreo.setText(String.valueOf(tablaEstudiantes.getValueAt(fila, 4)));
        txtContrasena.setText("");
    }

    private void registrar() {
        try {
            controlador.registrar(txtNombre.getText(), txtRut.getText(), txtCurso.getText(), txtCorreo.getText(), new String(txtContrasena.getPassword()));
            JOptionPane.showMessageDialog(this, "Estudiante y usuario registrados correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        try {
            controlador.actualizar(idSeleccionado, rutOriginal, txtNombre.getText(), txtRut.getText(), txtCurso.getText(), txtCorreo.getText(), new String(txtContrasena.getPassword()));
            JOptionPane.showMessageDialog(this, "Estudiante actualizado correctamente. Deje la contraseña vacía para conservarla.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) {
            mostrarError(new IllegalArgumentException("Seleccione un estudiante de la tabla."));
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el estudiante y su usuario de acceso?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        try {
            controlador.eliminar(idSeleccionado, rutOriginal);
            JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        rutOriginal = "";
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        tablaEstudiantes.clearSelection();
        txtNombre.requestFocus();
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
