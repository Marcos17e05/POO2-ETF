package org.example.controlador;

import java.sql.SQLException;
import java.util.regex.Pattern;

abstract class ControladorBase {
    //patron regex par validacion
    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern RUT = Pattern.compile("^\\d{7,8}-[0-9Kk]$");

    protected String obligatorio(String valor, String campo, int maximo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new IllegalArgumentException("El campo " + campo + " no puede superar " + maximo + " caracteres.");
        }
        return limpio;
    }

    protected String validarRut(String rut) {
        String limpio = obligatorio(rut, "RUT", 12).replace(".", "").toUpperCase();
        if (!RUT.matcher(limpio).matches()) {
            throw new IllegalArgumentException("El RUT debe tener formato 12345678-9.");
        }
        return limpio;
    }

    protected String validarCorreo(String correo) {
        String limpio = obligatorio(correo, "correo", 100);
        if (!CORREO.matcher(limpio).matches()) {
            throw new IllegalArgumentException("Ingrese un correo válido.");
        }
        return limpio;
    }

    protected IllegalStateException errorBD(SQLException ex) {
        String detalle = ex.getMessage() == null ? "Error SQL sin detalle." : ex.getMessage();
        String adicional = "";
        if (ex.getSQLState() != null && ex.getSQLState().startsWith("23")) {
            adicional = "\nLa operación viola una restricción o realción entre tablas.";
        }
        return new IllegalStateException("No fue posible completar la operación en MySQL." + adicional + "\nVerifique que la base 'biblioteca' exista y que las credenciales de DatabaseConnection sean correctas." + "\nDetalle: " + detalle, ex);
    }
}
