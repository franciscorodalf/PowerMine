package es.franciscorodalf.powermine.backend.model.abstractas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * Indica si la base de datos ya ha sido inicializada
     */
    private static boolean dbInitialized = false;

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
        if (connection == null) {
            crearConexion();
        }
        return connection;
    }

    /**
     * Inicializa la conexión a la base de datos
     */
    private void crearConexion() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(DATABASE_URL);

                // Inicializar la base de datos si no se ha hecho ya
                if (!dbInitialized) {
                    inicializarBaseDatos();
                    dbInitialized = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    /**
     * Comprueba si una tabla existe en la base de datos
     *
     * @param tableName Nombre de la tabla a comprobar
     * @return true si la tabla existe
     */
    private boolean tablaExiste(String tableName) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error al comprobar si existe la tabla: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicializa las tablas en la base de datos si no existen
     */
    private void inicializarBaseDatos() {
        try {
            // Cargar el script SQL desde resources
            var inputStream = getClass().getResourceAsStream("/db/tabla-powemine.sql");
            if (inputStream == null) {
                System.err.println("❌ No se encontró el script SQL para inicializar la base de datos");
                return;
            }

            String sqlScript = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Extraer sentencias CREATE TABLE
            List<String> createStatements = extraerCreateTableStatements(sqlScript);

            boolean tablasCreadasActualizadas = false;

            // Ejecutar cada CREATE TABLE solo si la tabla no existe
            for (String statement : createStatements) {
                // Extraer el nombre de la tabla del statement
                Pattern pattern = Pattern.compile("CREATE\\s+TABLE\\s+(\\w+)");
                Matcher matcher = pattern.matcher(statement);

                if (matcher.find()) {
                    String tableName = matcher.group(1);
                    if (!tablaExiste(tableName)) {
                        try (Statement stmt = connection.createStatement()) {
                            stmt.execute(statement);
                            System.out.println("✅ Tabla " + tableName + " creada correctamente");
                            tablasCreadasActualizadas = true;
                        }
                    } else {
                        System.out.println("ℹ️ La tabla " + tableName + " ya existe, se omite su creación");
                    }
                }
            }

            // Ejecutar sentencias restantes que no son CREATE TABLE (vistas, etc.)
            List<String> otherStatements = extraerOtrosStatements(sqlScript);
            if (!otherStatements.isEmpty()) {
                try (Statement stmt = connection.createStatement()) {
                    for (String statement : otherStatements) {
                        try {
                            stmt.execute(statement);
                            System.out.println("✅ Sentencia SQL ejecutada: " + statement.substring(0, Math.min(statement.length(), 50)) + "...");
                            tablasCreadasActualizadas = true;
                        } catch (SQLException e) {
                            // Ignorar error si la vista ya existe
                            if (e.getMessage().contains("already exists")) {
                                System.out.println("ℹ️ El objeto ya existe, se omite su creación");
                            } else {
                                throw e;
                            }
                        }
                    }
                }
            }

            if (tablasCreadasActualizadas) {
                System.out.println("✅ Base de datos inicializada correctamente");
            } else {
                System.out.println("ℹ️ Base de datos ya inicializada, no se requieren cambios");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extrae las sentencias CREATE TABLE del script SQL
     */
    private List<String> extraerCreateTableStatements(String sqlScript) {
        List<String> statements = new ArrayList<>();
        Pattern pattern = Pattern.compile("CREATE\\s+TABLE\\s+\\w+\\s*\\([^;]+\\);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlScript);

        while (matcher.find()) {
            statements.add(matcher.group());
        }

        return statements;
    }

    /**
     * Extrae otras sentencias SQL que no son CREATE TABLE
     */
    private List<String> extraerOtrosStatements(String sqlScript) {
        List<String> statements = new ArrayList<>();

        // Eliminar comentarios
        sqlScript = sqlScript.replaceAll("--.*?\\n", "\n");

        // Dividir por punto y coma y filtrar CREATE TABLE statements
        for (String statement : sqlScript.split(";")) {
            statement = statement.trim();
            if (!statement.isEmpty() &&
                    !statement.toUpperCase().startsWith("CREATE TABLE")) {
                statements.add(statement + ";");
            }
        }

        return statements;
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
