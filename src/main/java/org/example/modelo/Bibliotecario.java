package org.example.modelo;

public class Bibliotecario extends Usuario {

    public Bibliotecario(int idUsuario, String nombre, String rut, String correo, String contrasena) {
        super(idUsuario, nombre, rut, correo, contrasena, RolUsuario.BIBLIOTECARIO);
    }

    public Bibliotecario(String nombre, String rut, String correo, String contrasena) {
        this(0, nombre, rut, correo, contrasena);
    }
}
