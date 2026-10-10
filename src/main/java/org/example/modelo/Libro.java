package org.example.modelo;

public class Libro extends MaterialBiblioteca {
    private String isbn;
    private String editorial;
    private int stock;
    private Categoria categoria;

    public Libro(int id, String titulo, String autor, String isbn, String editorial, int stock, Categoria categoria) {
        super(id, titulo, autor);
        this.isbn = isbn;
        this.editorial = editorial;
        this.stock = stock;
        this.categoria = categoria;
    }

    public Libro(String titulo, String autor, String isbn, String editorial, int stock, Categoria categoria) {
        this(0, titulo, autor, isbn, editorial, stock, categoria);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String getTipoMaterial() {
        return "LIBRO";
    }

    @Override
    public String toString() {
        return "#" + getId() + " - " + getTitulo() + " (stock: " + stock + ")";
    }
}
