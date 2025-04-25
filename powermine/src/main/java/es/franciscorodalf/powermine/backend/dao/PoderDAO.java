package es.franciscorodalf.powermine.backend.dao;

import es.franciscorodalf.powermine.backend.model.Poder;
import es.franciscorodalf.powermine.backend.model.abstractas.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar el registro y consulta de poderes usados en las
 * partidas.
 * Permite trackear qué poderes se usaron en cada partida.
 */
public class PoderDAO extends Conexion {

    public PoderDAO() {
        super();
    }

    /**
     * Registra el uso de un poder en una partida
     * 
     * @param idPartida ID de la partida
     * @param poder     Poder utilizado
     * @param turno     Número de turno en que se usó
     * @return true si se registró correctamente
     */
    public boolean registrarPoderUsado(int idPartida, Poder poder, int turno) {
        String sql = "INSERT INTO poderes_usados (id_partida, id_poder, turno) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idPartida);
            stmt.setInt(2, poder.getId());
            stmt.setInt(3, turno);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar poder: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los poderes usados en una partida específica
     * 
     * @param idPartida ID de la partida
     * @return Lista de poderes ordenados por turno
     */
    public List<Poder> obtenerPoderesUsadosEnPartida(int idPartida) {
        List<Poder> poderes = new ArrayList<>();
        String sql = "SELECT id_poder FROM poderes_usados WHERE id_partida = ? ORDER BY turno";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idPartida);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Poder poder = Poder.getPorId(rs.getInt("id_poder"));
                if (poder != null) {
                    poderes.add(poder);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener poderes: " + e.getMessage());
        }

        return poderes;
    }
}