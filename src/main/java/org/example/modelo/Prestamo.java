package org.example.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {
    private int id;
    private Estudiante estudiante;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    public Prestamo(int id, Estudiante estudiante, Libro libro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.estudiante = estudiante;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public Prestamo(Estudiante estudiante, Libro libro, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this(0, estudiante, libro, fechaPrestamo, fechaDevolucion, false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public boolean estaAtrasado() {
        return !devuelto && fechaDevolucion != null && LocalDate.now().isAfter(fechaDevolucion);
    }

    public long diasAtraso() {
        if (fechaDevolucion == null || !LocalDate.now().isAfter(fechaDevolucion)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaDevolucion, LocalDate.now());
    }
}
