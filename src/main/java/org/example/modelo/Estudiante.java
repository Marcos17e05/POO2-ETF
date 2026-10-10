package org.example.modelo;

public class Estudiante extends Usuario {
    private int idEstudiante;
    private String curso;

    public Estudiante(int idEstudiante, int idUsuario, String nombre, String rut, String curso, String correo, String contrasena) {
        super(idUsuario, nombre, rut, correo, contrasena, RolUsuario.ESTUDIANTE);
        this.idEstudiante = idEstudiante;
        this.curso = curso;
    }

    public Estudiante(int idEstudiante, String nombre, String rut, String curso, String correo) {
        this(idEstudiante, 0, nombre, rut, curso, correo, "");
    }

    public Estudiante(String nombre, String rut, String curso, String correo, String contrasena) {
        this(0, 0, nombre, rut, curso, correo, contrasena);
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "#" + idEstudiante + " - " + getNombre() + " - " + getRut() + " - " + curso;
    }
}
