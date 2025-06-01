package es.franciscorodalf.powermine.backend.dao;

import es.franciscorodalf.powermine.backend.model.RankingUsuario;
import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.model.abstractas.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar las operaciones de usuarios en la base de datos.
 * Maneja el registro, autenticación y actualización de usuarios.
 */
public class UsuarioDAO extends Conexion {

    public UsuarioDAO() {
        super();
    }

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param usuario Usuario a registrar
     * @return true si el registro fue exitoso
     */
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

    /**
     * Busca un usuario por su correo o nombre de usuario
     * 
     * @param identificador Correo o nombre de usuario
     * @return Usuario encontrado o null
     */
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

    /**
     * Actualiza los datos de un usuario existente
     * 
     * @param usuario Usuario con los nuevos datos
     * @return true si la actualización fue exitosa
     */
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

    /**
     * Autentica a un usuario verificando sus credenciales
     * 
     * @param identificador Correo o nombre de usuario
     * @param contrasenia Contraseña del usuario
     * @return Usuario autenticado o null si falló la autenticación
     */
    public Usuario login(String identificador, String contrasenia) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? OR nombre_usuario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, identificador);
            stmt.setString(2, identificador);

            // Debug: Mostrar consulta SQL
            System.out.println("Ejecutando consulta: " + sql.replace("?", "'" + identificador + "'"));

            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre_usuario");
                String correo = rs.getString("correo");
                String contraseniaEnBD = rs.getString("contrasenia");
                
                System.out.println("Usuario encontrado: " + nombre);
                
                // Comparar la contraseña sin procesar con la almacenada
                if (contraseniaEnBD != null && contraseniaEnBD.equals(contrasenia)) {
                    System.out.println("Contraseña correcta para usuario: " + nombre);
                    return new Usuario(id, nombre, correo, contraseniaEnBD);
                } else {
                    System.out.println("Contraseña incorrecta para usuario: " + nombre);
                }
            } else {
                System.out.println("No se encontró usuario con identificador: " + identificador);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene el ranking completo de usuarios
     * 
     * @return Lista de usuarios ordenada por puntaje
     */
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
                        rs.getInt("victorias")));
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
                        rs.getInt("victorias")));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios: " + e.getMessage());
        }
        return ranking;
    }

}
