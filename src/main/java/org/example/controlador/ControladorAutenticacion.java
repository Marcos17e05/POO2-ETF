package org.example.controlador;

import org.example.dao.EstudianteDAO;
import org.example.dao.UsuarioDAO;
import org.example.modelo.Estudiante;
import org.example.modelo.Usuario;

import java.sql.SQLException;

public class ControladorAutenticacion extends ControladorBase {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public Usuario autenticar(String identificador, String contrasena) {
        String login = obligatorio(identificador, "RUT o correo", 100);
        String clave = obligatorio(contrasena, "contraseña", 100);

        try {
            Usuario usuario = usuarioDAO.autenticar(login, clave);

            if (usuario == null) {
                throw new IllegalArgumentException("RUT/correo o contraseña incorrectos.");
            }

            if (usuario.esEstudiante()) {
                Estudiante estudianteSesion = estudianteDAO.buscarPorRut(usuario.getRut());

                if (estudianteSesion == null) {
                    throw new IllegalArgumentException("El usuario posee rol estudiante, pero no existe una ficha de estudiante con el mismo RUT.");
                }

                return estudianteSesion;
            }

            return usuario;

        } catch (SQLException ex) {
            throw errorBD(ex);
        }
    }
}
