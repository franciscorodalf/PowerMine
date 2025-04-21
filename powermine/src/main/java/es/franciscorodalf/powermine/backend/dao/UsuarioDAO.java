package es.franciscorodalf.powermine.backend.dao;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.model.abstractas.Conexion;

import java.sql.*;

public class UsuarioDAO extends Conexion {

    public UsuarioDAO() {
        super();
    }

    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_usuario, correo, contrasenia) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getContrasenia());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorCorreoONombre(String identificador) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? OR nombre_usuario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, identificador);
            stmt.setString(2, identificador);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre_usuario"),
                        rs.getString("correo"),
                        rs.getString("contrasenia"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar usuario: " + e.getMessage());
        }

        return null;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, contrasenia = ? WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getContrasenia());
            stmt.setInt(3, usuario.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
}
