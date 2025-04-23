package es.franciscorodalf.powermine.backend.dao;

import es.franciscorodalf.powermine.backend.model.RankingUsuario;
import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.model.abstractas.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public Usuario login(String identificador, String contrasenia) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? OR nombre_usuario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, identificador);
            stmt.setString(2, identificador);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String contraseniaEnBD = rs.getString("contrasenia");
                if (contraseniaEnBD.equals(contrasenia)) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre_usuario"),
                            rs.getString("correo"),
                            contraseniaEnBD);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al iniciar sesión: " + e.getMessage());
        }

        return null;
    }

    public List<RankingUsuario> obtenerRanking() {
        List<RankingUsuario> ranking = new ArrayList<>();
        String sql = """
            SELECT u.nombre_usuario, 
                   SUM(p.puntaje) as puntaje_total,
                   COUNT(CASE WHEN p.ganada = 1 THEN 1 END) as victorias
            FROM usuarios u
            LEFT JOIN partidas p ON u.id = p.id_usuario
            GROUP BY u.id, u.nombre_usuario
            ORDER BY puntaje_total DESC
        """;
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            int posicion = 1;
            while (rs.next()) {
                ranking.add(new RankingUsuario(
                    posicion++,
                    rs.getString("nombre_usuario"),
                    rs.getInt("puntaje_total"),
                    rs.getInt("victorias")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ranking: " + e.getMessage());
        }
        return ranking;
    }

    public List<RankingUsuario> buscarUsuarios(String filtro) {
        List<RankingUsuario> ranking = new ArrayList<>();
        String sql = """
            SELECT u.nombre_usuario, 
                   COALESCE(SUM(p.puntaje), 0) as puntaje_total,
                   COUNT(CASE WHEN p.ganada = 1 THEN 1 END) as victorias
            FROM usuarios u
            LEFT JOIN partidas p ON u.id = p.id_usuario
            WHERE u.nombre_usuario LIKE ?
            GROUP BY u.id, u.nombre_usuario
            ORDER BY puntaje_total DESC
        """;
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            int posicion = 1;
            while (rs.next()) {
                ranking.add(new RankingUsuario(
                    posicion++,
                    rs.getString("nombre_usuario"),
                    rs.getInt("puntaje_total"),
                    rs.getInt("victorias")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios: " + e.getMessage());
        }
        return ranking;
    }

}
