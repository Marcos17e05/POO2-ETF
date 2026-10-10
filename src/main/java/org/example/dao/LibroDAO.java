package org.example.dao;

import org.example.modelo.Categoria;
import org.example.modelo.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public Libro guardar(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                cargarParametros(ps, libro);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) libro.setId(rs.getInt(1));
                }
            }
        }
        return libro;
    }

    public void actualizar(Libro libro) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                cargarParametros(ps, libro);
                ps.setInt(7, libro.getId());
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró el libro a actualizar.");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró el libro a eliminar.");
            }
        }
    }

    public Libro buscarPorId(int id) throws SQLException {
        String sql = consultaBase() + " WHERE l.id = ?";
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

    public List<Libro> listarTodos() throws SQLException {
        String sql = consultaBase() + " ORDER BY l.titulo";
        List<Libro> lista = new ArrayList<>();
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Libro> listarDisponibles() throws SQLException {
        String sql = consultaBase() + " WHERE l.stock > 0 ORDER BY l.titulo";
        List<Libro> lista = new ArrayList<>();
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean existeIsbn(String isbn, Integer excluirId) throws SQLException {
        String sql = excluirId == null ? "SELECT 1 FROM libros WHERE isbn = ? LIMIT 1" : "SELECT 1 FROM libros WHERE isbn = ? AND id <> ? LIMIT 1";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, isbn);
                if (excluirId != null) ps.setInt(2, excluirId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    private void cargarParametros(PreparedStatement ps, Libro libro) throws SQLException {
        ps.setString(1, libro.getTitulo());
        ps.setString(2, libro.getAutor());
        ps.setString(3, libro.getIsbn());
        ps.setString(4, libro.getEditorial());
        ps.setInt(5, libro.getStock());
        ps.setInt(6, libro.getCategoria().getId());
    }

    private String consultaBase() {
        return "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, " + "c.id AS id_categoria, c.nombre AS categoria " + "FROM libros l INNER JOIN categorias c ON c.id = l.id_categoria";
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("categoria"));
        return new Libro(rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"), rs.getString("isbn"), rs.getString("editorial"), rs.getInt("stock"), categoria);
    }
}
