package org.example.controlador;

import org.example.dao.EstudianteDAO;
import org.example.dao.PrestamoDAO;
import org.example.modelo.Estudiante;
import org.example.modelo.Prestamo;
import org.example.modelo.ReporteLibroPrestado;

import java.sql.SQLException;
import java.util.List;

public class ControladorReportes extends ControladorBase {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public List<ReporteLibroPrestado> librosMasPrestados() {
        try {
            return prestamoDAO.librosMasPrestados();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Prestamo> historialEstudiante(int idEstudiante) {
        try {
            return prestamoDAO.listarPorEstudiante(idEstudiante);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Prestamo> prestamosActivos() {
        try {
            return prestamoDAO.listarActivos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Estudiante> listarEstudiantes() {
        try {
            return estudianteDAO.listarTodos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }
}
