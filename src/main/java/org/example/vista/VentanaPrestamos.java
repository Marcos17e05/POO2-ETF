package org.example.vista;

import org.example.controlador.ControladorPrestamos;
import org.example.modelo.Estudiante;
import org.example.modelo.Libro;
import org.example.modelo.Prestamo;
import org.example.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrestamos extends JFrame {
    private JPanel panelPrincipal;
    private JComboBox<Estudiante> comboEstudiante;
    private JComboBox<Libro> comboLibro;
    private JButton btnRegistrarPrestamo;
    private JButton btnDevolver;
    private JButton btnRecargar;
    private JTable tablaPrestamos;
    private JProgressBar progreso;
    private JLabel lblEstadoProceso;
    private JLabel lblVencimiento;

    private final ControladorPrestamos controlador = new ControladorPrestamos();
    private final Usuario usuarioActual;
    private final List<Prestamo> prestamosTabla = new ArrayList<>();
    private Estudiante estudianteSesion;

    public VentanaPrestamos(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;

        setTitle("Biblioteca - Préstamos y devoluciones");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080, 650);
        setLocationRelativeTo(null);

        configurarTabla();
        configurarComboEstudiantes();

        progreso.setIndeterminate(true);
        progreso.setVisible(false);
        lblVencimiento.setText("Plazo automático: " + ControladorPrestamos.DIAS_PRESTAMO + " días.");

        btnRegistrarPrestamo.addActionListener(e -> registrarPrestamo());
        btnDevolver.addActionListener(e -> devolverPrestamo());
        btnRecargar.addActionListener(e -> recargar());

        cargarEstudianteDeLaSesion();
        configurarPermisos();
        recargar();
    }

    private void configurarTabla() {
        tablaPrestamos.setModel(new DefaultTableModel(new Object[]{"ID", "Estudiante", "RUT", "Libro", "Préstamo", "Vencimiento", "Estado", "Atraso"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void configurarComboEstudiantes() {
        comboEstudiante.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Estudiante estudiante) {
                    if (usuarioActual.esEstudiante()) {
                        setText(estudiante.getNombre());
                    } else {
                        setText(estudiante.getNombre() + " - " + estudiante.getRut());
                    }
                }
                return this;
            }
        });
    }

    private void cargarEstudianteDeLaSesion() {
        if (!usuarioActual.esEstudiante()) {
            estudianteSesion = null;
            return;
        }

        if (usuarioActual instanceof Estudiante estudianteAutenticado && estudianteAutenticado.getIdEstudiante() > 0) {
            estudianteSesion = estudianteAutenticado;
            return;
        }

        estudianteSesion = controlador.buscarEstudiantePorRut(usuarioActual.getRut());
    }

    private void configurarPermisos() {
        if (usuarioActual.esEstudiante()) {
            comboEstudiante.setEnabled(false);

            if (estudianteSesion == null) {
                btnRegistrarPrestamo.setEnabled(false);
                btnDevolver.setEnabled(false);
                lblEstadoProceso.setText("No existe una ficha de estudiante asociada al RUT " + usuarioActual.getRut() + ".");
            } else {
                btnRegistrarPrestamo.setEnabled(true);
                btnDevolver.setEnabled(true);
                lblEstadoProceso.setText("Sesión estudiante: " + estudianteSesion.getNombre() + ".");
            }
        } else {
            comboEstudiante.setEnabled(true);
        }
    }

    private void recargar() {
        try {
            comboEstudiante.removeAllItems();

            if (usuarioActual.esEstudiante()) {
                if (estudianteSesion == null) {
                    cargarEstudianteDeLaSesion();
                }

                if (estudianteSesion != null) {
                    comboEstudiante.addItem(estudianteSesion);
                    comboEstudiante.setSelectedItem(estudianteSesion);
                    comboEstudiante.setEnabled(false);
                }
            } else {
                for (Estudiante estudiante : controlador.listarEstudiantes()) {
                    comboEstudiante.addItem(estudiante);
                }
                comboEstudiante.setEnabled(true);
            }

            comboLibro.removeAllItems();
            for (Libro libro : controlador.listarLibrosDisponibles()) {
                comboLibro.addItem(libro);
            }

            cargarPrestamos();

            if (usuarioActual.esEstudiante() && estudianteSesion != null && !progreso.isVisible()) {
                lblEstadoProceso.setText("Sesión estudiante: " + estudianteSesion.getNombre() + ".");
            }
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cargarPrestamos() {
        Integer idEstudiante = null;

        if (usuarioActual.esEstudiante() && estudianteSesion != null) {
            // Un estudiante sólo consulta su propio historial.
            idEstudiante = estudianteSesion.getIdEstudiante();
        }

        List<Prestamo> lista = controlador.listarPrestamos(idEstudiante);
        prestamosTabla.clear();
        prestamosTabla.addAll(lista);

        DefaultTableModel modelo = (DefaultTableModel) tablaPrestamos.getModel();
        modelo.setRowCount(0);

        for (Prestamo p : lista) {
            String estado = p.isDevuelto() ? "DEVUELTO" : "EN PRÉSTAMO";
            String atraso = p.estaAtrasado() ? p.diasAtraso() + " día(s)" : "-";

            modelo.addRow(new Object[]{p.getId(), p.getEstudiante().getNombre(), p.getEstudiante().getRut(), p.getLibro().getTitulo(), p.getFechaPrestamo(), p.getFechaDevolucion(), estado, atraso});
        }
    }

    private void registrarPrestamo() {

        Estudiante estudiante = usuarioActual.esEstudiante() ? estudianteSesion : (Estudiante) comboEstudiante.getSelectedItem();

        Libro libro = (Libro) comboLibro.getSelectedItem();

        if (usuarioActual.esEstudiante() && estudiante == null) {
            mostrarError(new IllegalArgumentException("No se encontró su ficha de estudiante. Verifique que su RUT exista también en la tabla estudiantes."));
            return;
        }

        establecerOcupado(true, "Registrando préstamo y actualizando stock en segundo plano...");

        controlador.registrarPrestamoAsync(estudiante, libro, prestamo -> {
            establecerOcupado(false, "Préstamo registrado correctamente.");
            JOptionPane.showMessageDialog(this, "Préstamo registrado a nombre de " + estudiante.getNombre() + ".\nVence el " + prestamo.getFechaDevolucion() + ".");
            recargar();
        }, ex -> {
            establecerOcupado(false, "No fue posible registrar el préstamo.");
            mostrarError(ex);
        });
    }

    private void devolverPrestamo() {
        int fila = tablaPrestamos.getSelectedRow();
        Prestamo prestamo = fila >= 0 && fila < prestamosTabla.size() ? prestamosTabla.get(fila) : null;

        if (prestamo == null) {
            mostrarError(new IllegalArgumentException("Seleccione un préstamo de la tabla."));
            return;
        }
        // Validacion extra, para no devolver prestamo de otra cuenta
        if (usuarioActual.esEstudiante()) {
            if (estudianteSesion == null || prestamo.getEstudiante().getIdEstudiante() != estudianteSesion.getIdEstudiante()) {
                mostrarError(new IllegalArgumentException("Sólo puede devolver préstamos asociados a su propia cuenta."));
                return;
            }
        }

        establecerOcupado(true, "Registrando devolución y restituyendo stock en segundo plano...");

        controlador.devolverPrestamoAsync(prestamo, mensaje -> {
            establecerOcupado(false, mensaje);
            JOptionPane.showMessageDialog(this, mensaje);
            recargar();
        }, ex -> {
            establecerOcupado(false, "No fue posible registrar la devolución.");
            mostrarError(ex);
        });
    }

    private void establecerOcupado(boolean ocupado, String mensaje) {
        progreso.setVisible(ocupado);
        boolean puedeOperar = !ocupado && estudianteSesionValido();
        btnRegistrarPrestamo.setEnabled(puedeOperar);
        btnDevolver.setEnabled(puedeOperar);
        btnRecargar.setEnabled(!ocupado);
        comboLibro.setEnabled(!ocupado);
        comboEstudiante.setEnabled(!ocupado && usuarioActual.esBibliotecario());
        lblEstadoProceso.setText(mensaje);
    }

    private boolean estudianteSesionValido() {
        return usuarioActual.esBibliotecario() || estudianteSesion != null;
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
