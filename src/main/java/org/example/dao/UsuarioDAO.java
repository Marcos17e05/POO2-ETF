package org.example.dao;

import org.example.modelo.Bibliotecario;
import org.example.modelo.Estudiante;
import org.example.modelo.RolUsuario;
import org.example.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public Usuario autenticar(String identificador, String contrasena) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, contraseña, rol FROM usuarios " + "WHERE (rut = ? OR correo = ?) AND contraseña = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, identificador);
                ps.setString(2, identificador);
                ps.setString(3, contrasena);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? mapear(rs) : null;
                }
            }
        }
    }

    public Usuario guardar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, rut, correo, contraseña, rol) VALUES (?, ?, ?, ?, ?)";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getRut());
                ps.setString(3, usuario.getCorreo());
                ps.setString(4, usuario.getContrasena());
                ps.setString(5, usuario.getRol().getValorBD());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setIdUsuario(rs.getInt(1));
                    }
                }
                return usuario;
            }
        }
    }

    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, contraseña = ?, rol = ? WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getRut());
                ps.setString(3, usuario.getCorreo());
                ps.setString(4, usuario.getContrasena());
                ps.setString(5, usuario.getRol().getValorBD());
                ps.setInt(6, usuario.getIdUsuario());
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No se encontró el usuario a actualizar.");
                }
            }
        }
    }

    public void actualizarDatosPorRut(String rutActual, String nombre, String nuevoRut, String correo, String contrasena) throws SQLException {
        String sqlConClave = "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, contraseña = ? WHERE rut = ?";
        String sqlSinClave = "UPDATE usuarios SET nombre = ?, rut = ?, correo = ? WHERE rut = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            boolean cambiarClave = contrasena != null && !contrasena.isBlank();
            try (PreparedStatement ps = con.prepareStatement(cambiarClave ? sqlConClave : sqlSinClave)) {
                ps.setString(1, nombre);
                ps.setString(2, nuevoRut);
                ps.setString(3, correo);
                if (cambiarClave) {
                    ps.setString(4, contrasena);
                    ps.setString(5, rutActual);
                } else {
                    ps.setString(4, rutActual);
                }
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No se encontró el usuario asociado al estudiante.");
                }
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No se encontró el usuario a eliminar.");
                }
            }
        }
    }

    public void eliminarPorRut(String rut) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE rut = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, rut);
                ps.executeUpdate();
            }
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, contraseña, rol FROM usuarios WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? mapear(rs) : null;
                }
            }
        }
    }

    public Usuario buscarPorRut(String rut) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, contraseña, rol FROM usuarios WHERE rut = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, rut);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? mapear(rs) : null;
                }
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, contraseña, rol FROM usuarios ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapear(rs));
                }
            }
        }
        return usuarios;
    }

    public boolean existeRut(String rut, Integer excluirId) throws SQLException {
        String sql = excluirId == null ? "SELECT 1 FROM usuarios WHERE rut = ? LIMIT 1" : "SELECT 1 FROM usuarios WHERE rut = ? AND id <> ? LIMIT 1";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, rut);
                if (excluirId != null) ps.setInt(2, excluirId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    public boolean existeCorreo(String correo, Integer excluirId) throws SQLException {
        String sql = excluirId == null ? "SELECT 1 FROM usuarios WHERE correo = ? LIMIT 1" : "SELECT 1 FROM usuarios WHERE correo = ? AND id <> ? LIMIT 1";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, correo);
                if (excluirId != null) ps.setInt(2, excluirId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String rut = rs.getString("rut");
        String correo = rs.getString("correo");
        String clave = rs.getString("contraseña");
        RolUsuario rol = RolUsuario.desdeBD(rs.getString("rol"));
        if (rol == RolUsuario.BIBLIOTECARIO) {
            return new Bibliotecario(id, nombre, rut, correo, clave);
        }
        return new Estudiante(0, id, nombre, rut, "", correo, clave);
    }
}
