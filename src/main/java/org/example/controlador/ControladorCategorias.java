package org.example.controlador;

import org.example.dao.CategoriaDAO;
import org.example.modelo.Categoria;

import java.sql.SQLException;
import java.util.List;

public class ControladorCategorias extends ControladorBase {
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public Categoria registrar(String nombre) {
        String n = obligatorio(nombre, "nombre", 100);
        try {
            if (categoriaDAO.existeNombre(n, null)) throw new IllegalArgumentException("La categoría ya existe.");
            return categoriaDAO.guardar(new Categoria(n));
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void actualizar(int id, String nombre) {
        if (id <= 0) throw new IllegalArgumentException("Seleccinoe una categoría de la tabla.");
        String n = obligatorio(nombre, "nombre", 100);
        try {
            if (categoriaDAO.existeNombre(n, id))
                throw new IllegalArgumentException("Ya existe otra categoría con ese nombre.");
            categoriaDAO.actualizar(new Categoria(id, n));
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public void eliminar(int id) {
        if (id <= 0) throw new IllegalArgumentException("Seleccinoe una categoría de la tabla.");
        try {
            categoriaDAO.eliminar(id);
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }

    public List<Categoria> listar() {
        try {
            return categoriaDAO.listarTodos();
        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }
}
