package org.example.modelo;

public enum RolUsuario {
    BIBLIOTECARIO("bibliotecario"), ESTUDIANTE("estudiante");

    private final String valorBD;

    RolUsuario(String valorBD) {
        this.valorBD = valorBD;
    }

    public String getValorBD() {
        return valorBD;
    }

    public static RolUsuario desdeBD(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo.");
        }
        for (RolUsuario rol : values()) {
            if (rol.valorBD.equalsIgnoreCase(valor.trim())) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + valor);
    }
}
