package org.example.dao;

import org.example.modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public Categoria guardar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, categoria.getNombre());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) categoria.setId(rs.getInt(1));
                }
            }
        }
        return categoria;
    }

    public void actualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, categoria.getNombre());
                ps.setInt(2, categoria.getId());
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró la categoría a actualizar.");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró la categoría a eliminar.");
            }
        }
    }

    public Categoria buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM categorias WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? new Categoria(rs.getInt("id"), rs.getString("nombre")) : null;
                }
            }
        }
    }

    public List<Categoria> listarTodos() throws SQLException {
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre";
        List<Categoria> lista = new ArrayList<>();
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    public boolean existeNombre(String nombre, Integer excluirId) throws SQLException {
        String sql = excluirId == null ? "SELECT 1 FROM categorias WHERE nombre = ? LIMIT 1" : "SELECT 1 FROM categorias WHERE nombre = ? AND id <> ? LIMIT 1";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                if (excluirId != null) ps.setInt(2, excluirId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }
}
