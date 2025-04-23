package es.franciscorodalf.powermine.backend.dao;

import es.franciscorodalf.powermine.backend.model.Partida;
import es.franciscorodalf.powermine.backend.model.abstractas.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class PartidaDAO extends Conexion {

    public PartidaDAO() {
        super();
    }

    public boolean registrarPartida(Partida partida) {
        String sql = "INSERT INTO partidas (id_usuario, dificultad, puntaje, ganada, fecha) VALUES (?, ?, ?, ?, datetime('now'))";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, partida.getIdUsuario());
            stmt.setString(2, partida.getDificultad());
            stmt.setInt(3, partida.getPuntaje());
            stmt.setBoolean(4, partida.isGanada());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar partida: " + e.getMessage());
            return false;
        }
    }

    public List<Partida> obtenerPartidasPorUsuario(int idUsuario) {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM partidas WHERE id_usuario = ? ORDER BY fecha DESC";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Partida p = new Partida();
                p.setId(rs.getInt("id"));
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setDificultad(rs.getString("dificultad"));
                p.setPuntaje(rs.getInt("puntaje"));
                p.setGanada(rs.getBoolean("ganada"));
                p.setFecha(LocalDateTime.parse(rs.getString("fecha").replace(" ", "T")));
                partidas.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener partidas: " + e.getMessage());
        }

        return partidas;
    }

    public int contarPartidasGanadas(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM partidas WHERE id_usuario = ? AND ganada = 1";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar partidas ganadas: " + e.getMessage());
        }

        return 0;
    }

    public int obtenerPuntajeTotal(int idUsuario) {
        String sql = "SELECT SUM(puntaje) FROM partidas WHERE id_usuario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener puntaje total: " + e.getMessage());
        }

        return 0;
    }
}
