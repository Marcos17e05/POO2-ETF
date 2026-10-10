package org.example.controlador;

import org.example.dao.EstudianteDAO;
import org.example.dao.UsuarioDAO;
import org.example.modelo.Estudiante;
import org.example.modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

public class ControladorEstudiantes extends ControladorBase {
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Estudiante registrar(String nombre, String rut, String curso, String correo, String contrasena) {
        String n = obligatorio(nombre, "nombre", 100);
        String r = validarRut(rut);
        String cur = obligatorio(curso, "curso", 20);
        String c = validarCorreo(correo);
        String clave = obligatorio(contrasena, "contraseña", 100);
        try {
            if (estudianteDAO.existeRut(r, null) || usuarioDAO.existeRut(r, null)) {
                throw new IllegalArgumentException("Ya existe un estudiante o usuario con ese RUT.");
            }
            if (usuarioDAO.existeCorreo(c, null)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
            }

            Estudiante estudiante = new Estudiante(n, r, cur, c, clave);
            usuarioDAO.guardar(estudiante);
            try {
                estudianteDAO.guardar(estudiante);
            } catch (SQLException ex) {
                usuarioDAO.eliminarPorRut(r);
                throw ex;
            }
            return estudiante;
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void actualizar(int idEstudiante, String rutOriginal, String nombre, String rut, String curso, String correo, String contrasena) {
        if (idEstudiante <= 0) throw new IllegalArgumentException("Seleccione un estudiante de la tabla.");
        String n = obligatorio(nombre, "nombre", 100);
        String r = validarRut(rut);
        String cur = obligatorio(curso, "curso", 20);
        String c = validarCorreo(correo);
        try {
            Estudiante actual = estudianteDAO.buscarPorId(idEstudiante);
            if (actual == null) throw new IllegalArgumentException("El estudiante seleccionado ya no existe.");
            if (estudianteDAO.existeRut(r, idEstudiante)) {
                throw new IllegalArgumentException("Ya existe otro estudiante con ese RUT.");
            }
            Usuario usuario = usuarioDAO.buscarPorRut(rutOriginal);
            Integer idUsuario = usuario == null ? null : usuario.getIdUsuario();
            if (usuarioDAO.existeRut(r, idUsuario))
                throw new IllegalArgumentException("Ya existe otro usuario con ese RUT.");
            if (usuarioDAO.existeCorreo(c, idUsuario))
                throw new IllegalArgumentException("Ya existe otro usuario con ese correo.");

            estudianteDAO.actualizar(new Estudiante(idEstudiante, actual.getIdUsuario(), n, r, cur, c, actual.getContrasena()));
            usuarioDAO.actualizarDatosPorRut(rutOriginal, n, r, c, contrasena);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void eliminar(int idEstudiante, String rut) {
        if (idEstudiante <= 0) throw new IllegalArgumentException("Seleccione un estudiante de la tabla.");
        try {
            estudianteDAO.eliminar(idEstudiante);
            usuarioDAO.eliminarPorRut(rut);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Estudiante> listar() {
        try {
            return estudianteDAO.listarTodos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

}
