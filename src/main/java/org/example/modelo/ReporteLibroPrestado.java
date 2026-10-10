package org.example.modelo;

public class ReporteLibroPrestado {
    private final int idLibro;
    private final String titulo;
    private final String autor;
    private final int totalPrestamos;

    public ReporteLibroPrestado(int idLibro, String titulo, String autor, int totalPrestamos) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.totalPrestamos = totalPrestamos;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getTotalPrestamos() {
        return totalPrestamos;
    }
}
