package org.example.controlador;

import org.example.dao.CategoriaDAO;
import org.example.dao.LibroDAO;
import org.example.modelo.Categoria;
import org.example.modelo.Libro;

import java.sql.SQLException;
import java.util.List;

public class ControladorLibros extends ControladorBase {
    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public Libro registrar(String titulo, String autor, String isbn, String editorial, String stockTexto, Categoria categoria) {
        Libro libro = construir(0, titulo, autor, isbn, editorial, stockTexto, categoria);
        try {
            if (libroDAO.existeIsbn(libro.getIsbn(), null))
                throw new IllegalArgumentException("Ya existe un libro con ese ISBN.");
            return libroDAO.guardar(libro);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void actualizar(int id, String titulo, String autor, String isbn, String editorial, String stockTexto, Categoria categoria) {
        if (id <= 0) throw new IllegalArgumentException("Seleccione un libro de la tabla.");
        Libro libro = construir(id, titulo, autor, isbn, editorial, stockTexto, categoria);
        try {
            if (libroDAO.existeIsbn(libro.getIsbn(), id))
                throw new IllegalArgumentException("Ya existe otro libro con ese ISBN.");
            libroDAO.actualizar(libro);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void eliminar(int id) {
        if (id <= 0) throw new IllegalArgumentException("Seleccione un libro de la tabla.");
        try {
            libroDAO.eliminar(id);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Libro> listar() {
        try {
            return libroDAO.listarTodos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Libro> listarDisponibles() {
        try {
            return libroDAO.listarDisponibles();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Categoria> listarCategorias() {
        try {
            return categoriaDAO.listarTodos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    private Libro construir(int id, String titulo, String autor, String isbn, String editorial, String stockTexto, Categoria categoria) {
        String t = obligatorio(titulo, "título", 200);
        String a = obligatorio(autor, "autor", 100);
        String i = obligatorio(isbn, "ISBN", 20);
        String e = obligatorio(editorial, "editorial", 100);
        if (categoria == null) throw new IllegalArgumentException("Seleccione una categoría.");
        int stock;
        try {
            stock = Integer.parseInt(obligatorio(stockTexto, "stock", 9));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El stock debe ser un número entero.");
        }
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        return new Libro(id, t, a, i, e, stock, categoria);
    }
}
