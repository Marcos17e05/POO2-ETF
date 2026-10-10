package org.example.controlador;

import org.example.dao.EstudianteDAO;
import org.example.dao.LibroDAO;
import org.example.dao.PrestamoDAO;
import org.example.modelo.Estudiante;
import org.example.modelo.Libro;
import org.example.modelo.Prestamo;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ControladorPrestamos extends ControladorBase {
    public static final int DIAS_PRESTAMO = 7;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    private final ExecutorService ejecutor = Executors.newFixedThreadPool(3, tarea -> {
        Thread hilo = new Thread(tarea, "Biblioteca-Prestamos");
        hilo.setDaemon(true);
        return hilo;
    });

    public List<Estudiante> listarEstudiantes() {
        try {
            return estudianteDAO.listarTodos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public Estudiante buscarEstudiantePorRut(String rut) {
        try {
            return estudianteDAO.buscarPorRut(rut);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Libro> listarLibrosDisponibles() {
        try {
            return libroDAO.listarDisponibles();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Prestamo> listarPrestamos(Integer idEstudiante) {
        try {
            return idEstudiante == null ? prestamoDAO.listarTodos() : prestamoDAO.listarPorEstudiante(idEstudiante);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void registrarPrestamoAsync(Estudiante estudiante, Libro libro, Consumer<Prestamo> alCompletar, Consumer<Exception> alError) {
        if (estudiante == null) {
            alError.accept(new IllegalArgumentException("Seleccione un estudiante."));
            return;
        }
        if (libro == null) {
            alError.accept(new IllegalArgumentException("Seleccione un libro disponible."));
            return;
        }

        ejecutor.submit(() -> {
            try {
                LocalDate hoy = LocalDate.now();
                Prestamo prestamo = new Prestamo(estudiante, libro, hoy, hoy.plusDays(DIAS_PRESTAMO));
                Prestamo guardado = prestamoDAO.registrarPrestamoConStock(prestamo);
                SwingUtilities.invokeLater(() -> alCompletar.accept(guardado));
            } catch (Exception ex) {
                Exception procesada = ex instanceof SQLException ? errorBD((SQLException) ex) : ex;
                SwingUtilities.invokeLater(() -> alError.accept(procesada));
            }
        });
    }

    public void devolverPrestamoAsync(Prestamo prestamo, Consumer<String> alCompletar, Consumer<Exception> alError) {
        if (prestamo == null) {
            alError.accept(new IllegalArgumentException("Seleccione un préstamo de la tabla."));
            return;
        }
        if (prestamo.isDevuelto()) {
            alError.accept(new IllegalArgumentException("El préstamo seleccionado ya fue devuelto."));
            return;
        }

        ejecutor.submit(() -> {
            try {
                boolean atrasado = prestamo.getFechaDevolucion() != null && LocalDate.now().isAfter(prestamo.getFechaDevolucion());
                long dias = prestamo.diasAtraso();
                prestamoDAO.devolverPrestamoConStock(prestamo.getId());
                String mensaje = atrasado ? "Devolución registrada. El libro fue entregado con " + dias + " día(s) de atraso." : "Devolución registrada dentro del plazo.";
                SwingUtilities.invokeLater(() -> alCompletar.accept(mensaje));
            } catch (Exception ex) {
                Exception procesada = ex instanceof SQLException ? errorBD((SQLException) ex) : ex;
                SwingUtilities.invokeLater(() -> alError.accept(procesada));
            }
        });
    }
}
