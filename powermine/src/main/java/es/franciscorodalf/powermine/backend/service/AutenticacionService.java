package es.franciscorodalf.powermine.backend.service;

import es.franciscorodalf.powermine.backend.dao.UsuarioDAO;
import es.franciscorodalf.powermine.backend.model.Usuario;

public class AutenticacionService {

    private final UsuarioDAO usuarioDAO;

    public AutenticacionService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Intenta autenticar al usuario usando nombre de usuario o correo + contraseña.
     *
     * @param identificador nombre de usuario o correo
     * @param contrasenia   contraseña sin hashear
     * @return Usuario autenticado o null si las credenciales no coinciden
     */
    public Usuario iniciarSesion(String identificador, String contrasenia) {
        Usuario usuario = usuarioDAO.buscarPorCorreoONombre(identificador);

        if (usuario != null && usuario.getContrasenia().equals(contrasenia)) {
            return usuario;
        }

        return null;
    }

    /**
     * Registra un nuevo usuario si no existe el nombre o correo ya.
     *
     * @param usuario Usuario a registrar
     * @return true si se registró con éxito, false si ya existe
     */
    public boolean registrarUsuario(Usuario usuario) {
        Usuario existente = usuarioDAO.buscarPorCorreoONombre(usuario.getCorreo());

        if (existente != null) {
            return false; // Ya existe ese correo
        }

        existente = usuarioDAO.buscarPorCorreoONombre(usuario.getNombreUsuario());

        if (existente != null) {
            return false; // Ya existe ese nombre
        }

        return usuarioDAO.registrarUsuario(usuario);
    }

    /**
     * Permite actualizar la información de un usuario (nombre y/o contraseña).
     *
     * @param usuario Usuario con los nuevos datos ya seteados
     * @return true si se actualizó correctamente
     */
    public boolean actualizarUsuario(Usuario usuario) {
        return usuarioDAO.actualizarUsuario(usuario);
    }
}
