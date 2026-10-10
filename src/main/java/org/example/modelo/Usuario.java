package org.example.modelo;

public abstract class Usuario {
    private int idUsuario;
    private String nombre;
    private String rut;
    private String correo;
    private String contrasena;
    private RolUsuario rol;

    protected Usuario(int idUsuario, String nombre, String rut, String correo, String contrasena, RolUsuario rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }

    protected void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public boolean esBibliotecario() {
        return rol == RolUsuario.BIBLIOTECARIO;
    }

    public boolean esEstudiante() {
        return rol == RolUsuario.ESTUDIANTE;
    }

    @Override
    public String toString() {
        return nombre + " - " + rut + " (" + rol.getValorBD() + ")";
    }
}
