package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.dao.PartidaDAO;
import es.franciscorodalf.powermine.backend.model.Partida;
import es.franciscorodalf.powermine.backend.model.Poder;
import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.awt.Point;
import javafx.scene.layout.GridPane;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del juego Buscaminas.
 * Gestiona toda la lógica del juego:
 * - Generación del tablero
 * - Manejo de clicks y revelación de casillas
 * - Sistema de poderes especiales
 * - Puntuación y finalización de partida
 */
public class TableroController {
    // Variables FXML para elementos de la interfaz
    @FXML
    private GridPane gridTablero;
    @FXML
    private Label labelPuntaje;
    @FXML
    private Label labelCasillasRestantes;
    @FXML
    private Label labelDificultad;
    @FXML
    private Label labelMinas;
    @FXML
    private Label labelPoderSeleccionado;
    @FXML
    private Label labelNombrePoder;
    @FXML
    private Label labelDescripcionPoder;

    /**
     * Matriz de botones que representa el tablero visual
     */
    private Button[][] casillas;

    /**
     * Matriz que almacena el estado del tablero:
     * -2 = mina con poder
     * -1 = mina normal
     * 0 = casilla vacía
     * >0 = número de minas adyacentes
     */
    private int[][] tablero;

    /**
     * Matriz que registra qué casillas han sido reveladas
     */
    private boolean[][] reveladas;

    private int filas, columnas, numMinas;
    private int casillasSeguras;
    private int puntaje = 0;
    private String poderActual = null;
    private Usuario usuario;
    private List<Poder> poderesDisponibles = new ArrayList<>();
    private Poder poderSeleccionado = null;
    private boolean poderObtenido = false; // Añadir esta variable al inicio de la clase

    @FXML
    private void initialize() {
        labelNombrePoder.setText("Ningún poder seleccionado");
        labelDescripcionPoder.setText("Selecciona un poder para ver su descripción");
    }

    public void iniciarPartida(Usuario usuario, String dificultad) {
        this.usuario = usuario;
        configurarTablero(dificultad);
        inicializarTablero();
        actualizarEtiquetas();
    }

    /**
     * Configura el tablero según la dificultad seleccionada
     * @param dificultad Nivel de dificultad (Fácil, Medio, Difícil)
     */
    private void configurarTablero(String dificultad) {
        switch (dificultad) {
            case "Fácil":
                filas = columnas = 8;
                numMinas = 10;
                break;
            case "Medio":
                filas = columnas = 12;
                numMinas = 20;
                break;
            case "Difícil":
                filas = columnas = 14;
                numMinas = 30;
                break;
        }
        casillasSeguras = (filas * columnas) - numMinas;
        casillas = new Button[filas][columnas];
        tablero = new int[filas][columnas];
        reveladas = new boolean[filas][columnas];
        labelDificultad.setText("Dificultad: " + dificultad);
    }

    /**
     * Inicializa el tablero colocando minas y calculando números adyacentes
     */
    private void inicializarTablero() {
        gridTablero.getChildren().clear();
        Random rand = new Random();

        // Colocar minas y poderes
        int minasColocadas = 0;
        while (minasColocadas < numMinas) {
            int x = rand.nextInt(filas);
            int y = rand.nextInt(columnas);
            if (tablero[x][y] == 0) {
                tablero[x][y] = rand.nextDouble() < 0.5 ? -2 : -1; // 50% de probabilidad de poder
                minasColocadas++;
            }
        }

        // Crear botones y calcular números
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] >= 0) {
                    tablero[i][j] = contarMinasAdyacentes(i, j);
                }
                Button casilla = crearCasilla(i, j);
                casillas[i][j] = casilla;
                gridTablero.add(casilla, j, i);
            }
        }
    }

    private Button crearCasilla(int fila, int columna) {
        Button casilla = new Button();
        casilla.getStyleClass().add("casilla");
        casilla.setMinSize(35, 35); // Reducido de 40 a 35
        casilla.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        casilla.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (poderActual != null) {
                    aplicarPoder(fila, columna);
                } else {
                    revelarCasilla(fila, columna);
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                marcarCasilla(fila, columna);
            }
        });
        return casilla;
    }

    private int contarMinasAdyacentes(int fila, int columna) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newFila = fila + i;
                int newCol = columna + j;
                if (newFila >= 0 && newFila < filas && newCol >= 0 && newCol < columnas) {
                    if (tablero[newFila][newCol] < 0)
                        count++;
                }
            }
        }
        return count;
    }

    /**
     * Revela una casilla y maneja la lógica correspondiente
     * (mina, poder, número o casilla vacía)
     */
    private void revelarCasilla(int fila, int columna) {
        if (reveladas[fila][columna])
            return;

        Button casilla = casillas[fila][columna];
        int valor = tablero[fila][columna];

        if (valor == -1) {
            finalizarPartida(false);
            return;
        }

        if (valor == -2) {
            Poder nuevoPoder = Poder.getTodosLosPoderes()[new Random().nextInt(Poder.getTodosLosPoderes().length)];
            poderesDisponibles.add(nuevoPoder);
            casilla.setText(nuevoPoder.getEmoji());
            casilla.setStyle("-fx-background-color: lightgray;");
            labelPoderSeleccionado.setText("¡Nuevo poder obtenido: " + nuevoPoder.getEmoji() + "!");
            actualizarBotonesPoderes();
            // Convertir la casilla en una normal después de obtener el poder
            tablero[fila][columna] = contarMinasAdyacentes(fila, columna);
        } else {
            revelarNumero(fila, columna);
            if (valor == 0) {
                revelarAdyacentesRecursivo(fila, columna);
            }
        }

        reveladas[fila][columna] = true;
        casillasSeguras--;
        puntaje += 10;
        actualizarEtiquetas();

        if (casillasSeguras == 0) {
            finalizarPartida(true);
        }
    }

    private void revelarAdyacentesRecursivo(int fila, int columna) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newFila = fila + i;
                int newCol = columna + j;

                if (newFila >= 0 && newFila < filas &&
                        newCol >= 0 && newCol < columnas &&
                        !reveladas[newFila][newCol] &&
                        tablero[newFila][newCol] >= 0) {

                    revelarNumero(newFila, newCol);
                    reveladas[newFila][newCol] = true;
                    casillasSeguras--;
                    puntaje += 10;

                    // Si la casilla adyacente también está vacía, continuar recursivamente
                    if (tablero[newFila][newCol] == 0) {
                        revelarAdyacentesRecursivo(newFila, newCol);
                    }
                }
            }
        }
    }

    private void marcarCasilla(int fila, int columna) {
        Button casilla = casillas[fila][columna];
        if (!reveladas[fila][columna]) {
            if (casilla.getText().isEmpty()) {
                casilla.setText("🚩");
                casilla.setTextFill(Color.RED);
            } else {
                casilla.setText("");
            }
        }
    }

    private void actualizarBotonesPoderes() {
        // Obtener el controlador de poderes
        PoderesController poderesCtrl = (PoderesController) gridTablero.getScene()
                .lookup("#contenedorPoderes").getUserData();
        if (poderesCtrl != null) {
            poderesCtrl.setTableroController(this);
            poderesCtrl.actualizarPoderes(poderesDisponibles);
        }
    }

    @FXML
    private void manejarUsarPoder() {
        if (poderActual == null) {
            labelPoderSeleccionado.setText("⚠️ Selecciona un poder primero");
            labelPoderSeleccionado.setTextFill(Color.RED);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                labelPoderSeleccionado.setText("Poder: Ninguno");
                labelPoderSeleccionado.setTextFill(Color.BLACK);
            }));
            timeline.play();
            return;
        }

        // Si es desminador, visión o escudo, aplicarlo inmediatamente
        if (poderActual.equals("Desminador")) {
            eliminarMina();
            resetearEstadosPoder();
        } else if (poderActual.equals("Visión")) {
            revelarCasillaSegura();
            resetearEstadosPoder();
        } else if (poderActual.equals("Escudo")) {
            protegerCasillaAleatoria();
            resetearEstadosPoder();
        } else {
            AlertaService.mostrarInfo("Poder Activado", "Selecciona una casilla para usar " + poderActual);
        }
    }

    private void resetearEstadosPoder() {
        poderesDisponibles.remove(poderSeleccionado);
        poderSeleccionado = null;
        poderActual = null;
        actualizarBotonesPoderes();
        labelPoderSeleccionado.setText("Poder: Ninguno");
        labelNombrePoder.setText("Ningún poder seleccionado");
        labelDescripcionPoder.setText("Selecciona un poder para ver su descripción");
    }

    private void revelarCasillaSegura() {
        List<Point> casillasSeguras = new ArrayList<>();
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] >= 0 && !reveladas[i][j]) {
                    casillasSeguras.add(new Point(i, j));
                }
            }
        }

        if (!casillasSeguras.isEmpty()) {
            Point casillaElegida = casillasSeguras.get(new Random().nextInt(casillasSeguras.size()));
            int fila = (int) casillaElegida.getX();
            int col = (int) casillaElegida.getY();

            Button casilla = casillas[fila][col];
            casilla.setStyle("-fx-background-color: lightblue;");
            casilla.setText("👁️");

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                revelarCasilla(fila, col);
            }));
            timeline.play();
        }
    }

    /**
     * Aplica el poder seleccionado en la casilla indicada
     */
    private void aplicarPoder(int fila, int columna) {
        if (poderSeleccionado == null)
            return;

        switch (poderSeleccionado.getNombre()) {
            case "Radar":
                revelarArea(fila, columna, 2);
                break;
            case "Tsunami":
                aplicarTsunami(fila);
                break;
            case "Terremoto":
                limpiarColumna(columna);
                break;
        }
        resetearEstadosPoder();
    }

    /**
     * Efecto del poder Tsunami: limpia toda una fila con animación
     */
    private void aplicarTsunami(int fila) {
        int[] minasEliminadas = { 0 }; // Using array to allow modification in lambda

        // Primero mostrar el efecto de ola
        for (int j = 0; j < columnas; j++) {
            final int col = j;
            Button casilla = casillas[fila][j];

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(100 * j), e -> {
                        casilla.setStyle("-fx-background-color: #00BFFF;");
                        casilla.setText("🌊");
                    }));
            timeline.play();
        }

        // Después procesar las casillas
        Timeline procesoTimeline = new Timeline(new KeyFrame(Duration.millis(columnas * 100 + 500), e -> {
            for (int j = 0; j < columnas; j++) {
                if (!reveladas[fila][j]) {
                    if (tablero[fila][j] < 0) {
                        minasEliminadas[0]++;
                        tablero[fila][j] = contarMinasAdyacentes(fila, j);
                        numMinas--;
                        actualizarNumerosAdyacentes(fila, j);
                    }
                    revelarCasilla(fila, j);
                }
            }
            // Bonus de puntos por minas eliminadas
            puntaje += minasEliminadas[0] * 20;
            actualizarEtiquetas();
        }));
        procesoTimeline.play();
    }

    /**
     * Efecto del poder Terremoto: limpia toda una columna
     */
    private void limpiarColumna(int columna) {
        for (int i = 0; i < filas; i++) {
            if (!reveladas[i][columna]) {
                if (tablero[i][columna] >= 0) {
                    revelarCasilla(i, columna);
                } else {
                    // Convertir mina en casilla segura
                    tablero[i][columna] = contarMinasAdyacentes(i, columna);
                    numMinas--;
                    actualizarNumerosAdyacentes(i, columna);
                    Button casilla = casillas[i][columna];
                    casilla.setStyle("-fx-background-color: gold;");
                    casilla.setText("💫");
                    final int fila = i;
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                        if (!reveladas[fila][columna]) {
                            revelarCasilla(fila, columna);
                        }
                    }));
                    timeline.play();
                }
            }
        }
        actualizarEtiquetas();
    }

    /**
     * Efecto del poder Escudo: protege dos minas aleatorias
     */
    private void protegerCasillaAleatoria() {
        List<Point> minas = buscarMinas();
        if (minas.size() >= 2) {
            // Proteger dos minas aleatorias
            for (int i = 0; i < 2; i++) {
                Point minaElegida = minas.remove(new Random().nextInt(minas.size()));
                int fila = (int) minaElegida.getX();
                int col = (int) minaElegida.getY();

                tablero[fila][col] = contarMinasAdyacentes(fila, col);
                numMinas--;
                casillas[fila][col].setStyle("-fx-background-color: gold;");
                casillas[fila][col].setText("🛡️");

                // Actualizar números adyacentes
                actualizarNumerosAdyacentes(fila, col);
            }
            puntaje += 50; // Bonus por usar escudo
            actualizarEtiquetas();
        }
    }

    private List<Point> buscarMinas() {
        List<Point> minas = new ArrayList<>();
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] < 0 && !reveladas[i][j]) {
                    minas.add(new Point(i, j));
                }
            }
        }
        return minas;
    }

    /**
     * Efecto del poder Radar: revela temporalmente las minas en un área
     */
    private void revelarArea(int fila, int columna, int radio) {
        for (int i = -radio; i <= radio; i++) {
            for (int j = -radio; j <= radio; j++) {
                int newFila = fila + i;
                int newCol = columna + j;
                if (newFila >= 0 && newFila < filas && newCol >= 0 && newCol < columnas) {
                    Button casilla = casillas[newFila][newCol];
                    if (tablero[newFila][newCol] < 0) {
                        casilla.setText("⚠️");
                        casilla.setStyle("-fx-background-color: #FFE5E5;");
                        // Efecto temporal
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                            casilla.setText("");
                            casilla.setStyle("");
                        }));
                        timeline.play();
                    }
                }
            }
        }
    }

    // Renombrar y modificar el método eliminarMina para que no necesite parámetros
    /**
     * Efecto del poder Desminador: elimina una mina aleatoria
     */
    private void eliminarMina() {
        List<Point> minas = buscarMinas();
        if (!minas.isEmpty()) {
            Point minaElegida = minas.get(new Random().nextInt(minas.size()));
            int minaFila = (int) minaElegida.getX();
            int minaCol = (int) minaElegida.getY();

            Button casillaMinada = casillas[minaFila][minaCol];
            if (casillaMinada.getText().equals("🚩")) {
                casillaMinada.setText("");
            }

            tablero[minaFila][minaCol] = contarMinasAdyacentes(minaFila, minaCol);
            casillaMinada.setStyle("-fx-background-color: gold;");
            numMinas--;

            // Actualizar números adyacentes
            actualizarNumerosAdyacentes(minaFila, minaCol);

            casillaMinada.setText("💥");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                if (!reveladas[minaFila][minaCol]) {
                    casillaMinada.setText("");
                }
            }));
            timeline.play();

            actualizarEtiquetas();
        }
    }

    /**
     * Actualiza los números de las casillas adyacentes
     * cuando se elimina una mina
     */
    private void actualizarNumerosAdyacentes(int fila, int columna) {
        // Actualizar los números de las casillas adyacentes
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newFila = fila + i;
                int newCol = columna + j;
                if (newFila >= 0 && newFila < filas &&
                        newCol >= 0 && newCol < columnas &&
                        tablero[newFila][newCol] >= 0) {

                    tablero[newFila][newCol] = contarMinasAdyacentes(newFila, newCol);
                    if (reveladas[newFila][newCol]) {
                        revelarNumero(newFila, newCol);
                    }
                }
            }
        }
    }

    private void actualizarEtiquetas() {
        labelPuntaje.setText("Puntaje: " + puntaje);
        labelCasillasRestantes.setText("Casillas restantes: " + casillasSeguras);
        labelMinas.setText("Minas: " + numMinas);
    }

    /**
     * Finaliza la partida y guarda el resultado
     */
    private void finalizarPartida(boolean victoria) {
        revelarTableroCompleto();

        // Guardar la partida en la base de datos
        Partida partida = new Partida();
        partida.setIdUsuario(usuario.getId());
        partida.setDificultad(labelDificultad.getText().replace("Dificultad: ", ""));
        partida.setPuntaje(puntaje);
        partida.setGanada(victoria);
        partida.setFecha(LocalDateTime.now());

        PartidaDAO partidaDAO = new PartidaDAO();
        boolean guardado = partidaDAO.registrarPartida(partida);

        String mensaje = victoria ? "¡Felicitaciones! Has ganado con " + puntaje + " puntos"
                : "Game Over. Puntaje final: " + puntaje;

        // Mostrar el mensaje y esperar antes de volver al menú
        AlertaService.mostrarInfo("Fin del juego", mensaje);

        // Esperar 2 segundos antes de volver al menú
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> manejarVolverAlMenu()));
        timeline.play();
    }

    private void revelarTableroCompleto() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] < 0) {
                    casillas[i][j].setText("💣");
                    casillas[i][j].setStyle("-fx-background-color: red;");
                } else if (!reveladas[i][j]) {
                    revelarNumero(i, j);
                }
            }
        }
    }

    /**
     * Revela el número en una casilla y aplica el estilo correspondiente
     */
    private void revelarNumero(int fila, int columna) {
        Button casilla = casillas[fila][columna];
        int valor = tablero[fila][columna];
        casilla.getStyleClass().add("casilla-revelada");

        if (valor > 0) {
            casilla.setText(String.valueOf(valor));
            casilla.getStyleClass().add("numero-" + valor);
        }
        casilla.setStyle("-fx-background-color: lightgray;");

        if (valor > 0) {
            casilla.setText(String.valueOf(valor));
            // Asignar color según el número
            switch (valor) {
                case 1:
                    casilla.setTextFill(Color.BLUE);
                    break;
                case 2:
                    casilla.setTextFill(Color.GREEN);
                    break;
                case 3:
                    casilla.setTextFill(Color.RED);
                    break;
                case 4:
                    casilla.setTextFill(Color.DARKBLUE);
                    break;
                case 5:
                    casilla.setTextFill(Color.DARKRED);
                    break;
                case 6:
                    casilla.setTextFill(Color.TEAL);
                    break;
                case 7:
                    casilla.setTextFill(Color.BLACK);
                    break;
                case 8:
                    casilla.setTextFill(Color.GRAY);
                    break;
            }
        }
    }

    @FXML
    private void manejarVolverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu-principal.fxml"));
            Parent root = loader.load();

            // Pasar el usuario actual al menú principal
            MenuPrincipalController menuCtrl = loader.getController();
            menuCtrl.setUsuarioActual(usuario);

            // Cambiar a la vista del menú
            Stage stage = (Stage) gridTablero.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo volver al menú principal");
            e.printStackTrace();
        }
    }

    /**
     * Maneja la selección y activación de poderes
     */
    public void seleccionarPoder(Poder poder) {
        if (puntaje < poder.getCostePuntos()) {
            labelPoderSeleccionado.setText("⚠️ Necesitas " + poder.getCostePuntos() + " puntos");
            labelPoderSeleccionado.setTextFill(Color.RED);
            return;
        }

        this.poderSeleccionado = poder;
        this.poderActual = poder.getNombre();
        puntaje -= poder.getCostePuntos();

        labelPoderSeleccionado.setText("Poder seleccionado: " + poder.getEmoji());
        labelNombrePoder.setText(poder.getEmoji() + " " + poder.getNombre());
        labelDescripcionPoder.setText(poder.getInfoCompleta());
        actualizarEtiquetas();
    }
}
