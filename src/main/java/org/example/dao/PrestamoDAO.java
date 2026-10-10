package org.example.dao;

import org.example.modelo.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    //No es necesario, pero dejarlo por cumplir con el crud
    public Prestamo guardar(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) " + "VALUES (?, ?, ?, ?, ?)";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                cargarParametros(ps, prestamo);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) prestamo.setId(rs.getInt(1));
                }
            }
        }
        return prestamo;
    }

    public void actualizar(Prestamo prestamo) throws SQLException {
        String sql = "UPDATE prestamos SET id_estudiante = ?, id_libro = ?, fecha_prestamo = ?, " + "fecha_devolucion = ?, devuelto = ? WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                cargarParametros(ps, prestamo);
                ps.setInt(6, prestamo.getId());
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró el préstamo a actualizar.");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) throw new SQLException("No se encontró el préstamo a eliminar.");
            }
        }
    }

    public Prestamo buscarPorId(int id) throws SQLException {
        String sql = consultaBase() + " WHERE p.id = ?";
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

    public List<Prestamo> listarTodos() throws SQLException {
        return listar(null, false);
    }

    public List<Prestamo> listarActivos() throws SQLException {
        return listar(null, true);
    }

    public List<Prestamo> listarPorEstudiante(int idEstudiante) throws SQLException {
        return listar(idEstudiante, false);
    }

    private List<Prestamo> listar(Integer idEstudiante, boolean soloActivos) throws SQLException {
        StringBuilder sql = new StringBuilder(consultaBase()).append(" WHERE 1=1");
        if (idEstudiante != null) sql.append(" AND p.id_estudiante = ?");
        if (soloActivos) sql.append(" AND p.devuelto = FALSE");
        sql.append(" ORDER BY p.fecha_prestamo DESC, p.id DESC");
        List<Prestamo> lista = new ArrayList<>();
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                if (idEstudiante != null) ps.setInt(1, idEstudiante);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public synchronized Prestamo registrarPrestamoConStock(Prestamo prestamo) throws SQLException {
        String sqlStock = "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";
        String sqlPrestamo = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) " + "VALUES (?, ?, ?, ?, FALSE)";

        synchronized (database) {
            Connection con = database.getConnection();
            boolean autoCommitOriginal = con.getAutoCommit();
            try {
                con.setAutoCommit(false);

                try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                    psStock.setInt(1, prestamo.getLibro().getId());
                    if (psStock.executeUpdate() == 0) {
                        throw new SQLException("No hay stock disponible para el libro seleccionado.");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlPrestamo, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, prestamo.getEstudiante().getIdEstudiante());
                    ps.setInt(2, prestamo.getLibro().getId());
                    ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                    ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) prestamo.setId(rs.getInt(1));
                    }
                }

                con.commit();
                return prestamo;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    public synchronized boolean devolverPrestamoConStock(int idPrestamo) throws SQLException {
        String sqlBuscar = "SELECT id_libro, devuelto FROM prestamos WHERE id = ? FOR UPDATE";
        String sqlDevolver = "UPDATE prestamos SET devuelto = TRUE WHERE id = ?";
        String sqlStock = "UPDATE libros SET stock = stock + 1 WHERE id = ?";

        synchronized (database) {
            Connection con = database.getConnection();
            boolean autoCommitOriginal = con.getAutoCommit();
            try {
                con.setAutoCommit(false);
                int idLibro;
                boolean yaDevuelto;

                try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
                    ps.setInt(1, idPrestamo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("No se encontró el préstamo seleccionado.");
                        idLibro = rs.getInt("id_libro");
                        yaDevuelto = rs.getBoolean("devuelto");
                    }
                }

                if (yaDevuelto) {
                    throw new SQLException("El préstamo seleccionado ya fue devuelto.");
                }

                try (PreparedStatement ps = con.prepareStatement(sqlDevolver)) {
                    ps.setInt(1, idPrestamo);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlStock)) {
                    ps.setInt(1, idLibro);
                    ps.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    public List<ReporteLibroPrestado> librosMasPrestados() throws SQLException {
        String sql = "SELECT l.id, l.titulo, l.autor, COUNT(p.id) AS total " + "FROM libros l LEFT JOIN prestamos p ON p.id_libro = l.id " + "GROUP BY l.id, l.titulo, l.autor ORDER BY total DESC, l.titulo";
        List<ReporteLibroPrestado> lista = new ArrayList<>();
        synchronized (database) {
            Connection con = database.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ReporteLibroPrestado(rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"), rs.getInt("total")));
                }
            }
        }
        return lista;
    }

    private void cargarParametros(PreparedStatement ps, Prestamo prestamo) throws SQLException {
        ps.setInt(1, prestamo.getEstudiante().getIdEstudiante());
        ps.setInt(2, prestamo.getLibro().getId());
        ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
        ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
        ps.setBoolean(5, prestamo.isDevuelto());
    }

    private String consultaBase() {
        return "SELECT p.id AS id_prestamo, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, " + "e.id AS id_estudiante, e.nombre AS estudiante_nombre, e.rut, e.curso, e.correo, " + "l.id AS id_libro, l.titulo, l.autor, l.isbn, l.editorial, l.stock, " + "c.id AS id_categoria, c.nombre AS categoria " + "FROM prestamos p " + "INNER JOIN estudiantes e ON e.id = p.id_estudiante " + "INNER JOIN libros l ON l.id = p.id_libro " + "INNER JOIN categorias c ON c.id = l.id_categoria";
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante(rs.getInt("id_estudiante"), rs.getString("estudiante_nombre"), rs.getString("rut"), rs.getString("curso"), rs.getString("correo"));
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("categoria"));
        Libro libro = new Libro(rs.getInt("id_libro"), rs.getString("titulo"), rs.getString("autor"), rs.getString("isbn"), rs.getString("editorial"), rs.getInt("stock"), categoria);
        Date fp = rs.getDate("fecha_prestamo");
        Date fd = rs.getDate("fecha_devolucion");
        LocalDate fechaPrestamo = fp == null ? null : fp.toLocalDate();
        LocalDate fechaDevolucion = fd == null ? null : fd.toLocalDate();
        return new Prestamo(rs.getInt("id_prestamo"), estudiante, libro, fechaPrestamo, fechaDevolucion, rs.getBoolean("devuelto"));
    }
}
