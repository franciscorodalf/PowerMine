package es.franciscorodalf.powermine.backend.dao;

import es.franciscorodalf.powermine.backend.model.Poder;
import es.franciscorodalf.powermine.backend.model.abstractas.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoderDAO extends Conexion {

    public PoderDAO() {
        super();
    }

    public List<Poder> obtenerTodosLosPoderes() {
        List<Poder> poderes = new ArrayList<>();
        String sql = "SELECT * FROM poderes";

        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                poderes.add(new Poder(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener poderes: " + e.getMessage());
        }

        return poderes;
    }

    public boolean registrarPoderUsado(int idPartida, String nombrePoder, int turno) {
        String sql = "INSERT INTO poderes_usados (id_partida, nombre_poder, turno) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idPartida);
            stmt.setString(2, nombrePoder);
            stmt.setInt(3, turno);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar poder usado: " + e.getMessage());
            return false;
        }
    }

    public List<String> obtenerPoderesUsadosPorPartida(int idPartida) {
        List<String> poderes = new ArrayList<>();
        String sql = "SELECT nombre_poder FROM poderes_usados WHERE id_partida = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idPartida);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                poderes.add(rs.getString("nombre_poder"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener poderes usados: " + e.getMessage());
        }

        return poderes;
    }
}
