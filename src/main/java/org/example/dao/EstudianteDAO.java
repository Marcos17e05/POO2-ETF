package org.example.dao;

import org.example.modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public Estudiante guardar(Estudiante estudiante) throws SQLException {
        String sql = "INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getRut());
                ps.setString(3, estudiante.getCurso());
                ps.setString(4, estudiante.getCorreo());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) estudiante.setIdEstudiante(rs.getInt(1));
                }
                return estudiante;
            }
        }
    }

    public void actualizar(Estudiante estudiante) throws SQLException {
        String sql = "UPDATE estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getRut());
                ps.setString(3, estudiante.getCurso());
                ps.setString(4, estudiante.getCorreo());
                ps.setInt(5, estudiante.getIdEstudiante());
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró el estudiante a actualizar.");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM estudiantes WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró el estudiante a eliminar.");
            }
        }
    }

    public Estudiante buscarPorId(int id) throws SQLException {
        String sql = consultaBase() + " WHERE e.id = ?";
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

    public Estudiante buscarPorRut(String rut) throws SQLException {
        String sql = consultaBase() + " WHERE e.rut = ?";
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

    public List<Estudiante> listarTodos() throws SQLException {
        String sql = consultaBase() + " ORDER BY e.nombre";
        List<Estudiante> lista = new ArrayList<>();
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean existeRut(String rut, Integer excluirId) throws SQLException {
        String sql = excluirId == null ? "SELECT 1 FROM estudiantes WHERE rut = ? LIMIT 1" : "SELECT 1 FROM estudiantes WHERE rut = ? AND id <> ? LIMIT 1";
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

    private String consultaBase() {
        return "SELECT e.id AS id_estudiante, e.nombre, e.rut, e.curso, e.correo, " + "COALESCE(u.id, 0) AS id_usuario, COALESCE(u.contraseña, '') AS contraseña " + "FROM estudiantes e LEFT JOIN usuarios u ON u.rut = e.rut AND u.rol = 'estudiante'";
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(rs.getInt("id_estudiante"), rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("rut"), rs.getString("curso"), rs.getString("correo"), rs.getString("contraseña"));
    }
}
