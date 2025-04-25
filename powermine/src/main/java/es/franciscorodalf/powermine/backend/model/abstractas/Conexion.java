package es.franciscorodalf.powermine.backend.model.abstractas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase abstracta que gestiona la conexión a la base de datos SQLite.
 * Proporciona métodos comunes para todos los DAOs.
 */
public abstract class Conexion {
    /**
     * Instancia única de la conexión a la base de datos
     */
    private static Connection connection;
    
    /**
     * Ruta al archivo de la base de datos
     */
    private static final String DATABASE_URL = "jdbc:sqlite:powermine.db";

    /**
     * Constructor que inicializa la conexión
     */
    protected Conexion() {
        crearConexion();
    }

    /**
     * Obtiene la conexión existente o crea una nueva
     */
    protected Connection getConnection() {
        return connection;
    }

    /**
     * Inicializa la conexión a la base de datos
     */
    private void crearConexion() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(DATABASE_URL);
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión a la base de datos.
     *
     * @throws SQLException Si ocurre un error al cerrar la conexión.
     */
    public static void cerrar() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }
}
