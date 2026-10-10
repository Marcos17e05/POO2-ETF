package org.example.vista;

import org.example.controlador.ControladorReportes;
import org.example.modelo.Estudiante;
import org.example.modelo.Prestamo;
import org.example.modelo.ReporteLibroPrestado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VentanaReportes extends JFrame {
    private JPanel panelPrincipal;
    private JComboBox<String> comboReporte;
    private JComboBox<Estudiante> comboEstudiante;
    private JButton btnGenerar;
    private JTable tablaReporte;

    private final ControladorReportes controlador = new ControladorReportes();

    public VentanaReportes() {
        setTitle("Biblioteca - Reportes");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);

        comboReporte.addItem("Libros más prestados");
        comboReporte.addItem("Historial por estudiante");
        comboReporte.addItem("Libros actualmente en préstamo");
        cargarEstudiantes();
        comboReporte.addActionListener(e -> actualizarControles());
        btnGenerar.addActionListener(e -> generar());
        actualizarControles();
    }

    private void cargarEstudiantes() {
        try {
            comboEstudiante.removeAllItems();
            for (Estudiante e : controlador.listarEstudiantes()) comboEstudiante.addItem(e);
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void actualizarControles() {
        boolean historial = comboReporte.getSelectedIndex() == 1;
        comboEstudiante.setEnabled(historial);
    }

    private void generar() {
        try {
            switch (comboReporte.getSelectedIndex()) {
                case 0 -> mostrarMasPrestados(controlador.librosMasPrestados());
                case 1 -> {
                    Estudiante e = (Estudiante) comboEstudiante.getSelectedItem();
                    if (e == null) throw new IllegalArgumentException("Seleccione un estudiante.");
                    mostrarPrestamos(controlador.historialEstudiante(e.getIdEstudiante()));
                }
                case 2 -> mostrarPrestamos(controlador.prestamosActivos());
                default -> throw new IllegalArgumentException("Seleccione un reporte válido.");
            }
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarMasPrestados(List<ReporteLibroPrestado> lista) {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID Libro", "Título", "Autor", "Total préstamos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (ReporteLibroPrestado r : lista)
            modelo.addRow(new Object[]{r.getIdLibro(), r.getTitulo(), r.getAutor(), r.getTotalPrestamos()});
        tablaReporte.setModel(modelo);
    }

    private void mostrarPrestamos(List<Prestamo> lista) {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Estudiante", "Libro", "Préstamo", "Vencimiento", "Devuelto", "Atraso"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Prestamo p : lista)
            modelo.addRow(new Object[]{p.getId(), p.getEstudiante().getNombre(), p.getLibro().getTitulo(), p.getFechaPrestamo(), p.getFechaDevolucion(), p.isDevuelto() ? "Sí" : "No", p.estaAtrasado() ? p.diasAtraso() + " día(s)" : "-"});
        tablaReporte.setModel(modelo);
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
