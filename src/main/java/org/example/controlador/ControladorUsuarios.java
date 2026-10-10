package org.example.controlador;

import org.example.dao.UsuarioDAO;
import org.example.modelo.Bibliotecario;
import org.example.modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

public class ControladorUsuarios extends ControladorBase {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario registrarBibliotecario(String nombre, String rut, String correo, String contrasena) {
        String n = obligatorio(nombre, "nombre", 100);
        String r = validarRut(rut);
        String c = validarCorreo(correo);
        String clave = obligatorio(contrasena, "contraseña", 100);
        try {
            if (usuarioDAO.existeRut(r, null)) throw new IllegalArgumentException("Ya existe un usuario con ese RUT.");
            if (usuarioDAO.existeCorreo(c, null))
                throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
            return usuarioDAO.guardar(new Bibliotecario(n, r, c, clave));
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void actualizarBibliotecario(int id, String nombre, String rut, String correo, String contrasena) {
        if (id <= 0) throw new IllegalArgumentException("Seleccione un usuario de la tabla.");
        String n = obligatorio(nombre, "nombre", 100);
        String r = validarRut(rut);
        String c = validarCorreo(correo);
        try {
            Usuario actual = usuarioDAO.buscarPorId(id);
            if (actual == null) throw new IllegalArgumentException("El usuario seleccionado ya no existe.");
            if (usuarioDAO.existeRut(r, id)) throw new IllegalArgumentException("Ya existe otro usuario con ese RUT.");
            if (usuarioDAO.existeCorreo(c, id))
                throw new IllegalArgumentException("Ya existe otro usuario con ese correo.");
            String clave = contrasena == null || contrasena.isBlank() ? actual.getContrasena() : contrasena.trim();
            usuarioDAO.actualizar(new Bibliotecario(id, n, r, c, clave));
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void eliminar(int id) {
        if (id <= 0) throw new IllegalArgumentException("Seleccione un usuario de la tabla.");
        try {
            usuarioDAO.eliminar(id);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Usuario> listar() {
        try {
            return usuarioDAO.listarTodos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }
}
