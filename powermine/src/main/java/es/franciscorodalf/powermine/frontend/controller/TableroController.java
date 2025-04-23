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

public class TableroController {
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

    private Button[][] casillas;
    private int[][] tablero; // -1=bomba, -2=bombaConPoder, 0=vacío, n=número
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
                filas = columnas = 14; // Reducido de 16 a 14
                numMinas = 30; // Ajustado de 35 a 30 para mantener la proporción
                break;
        }
        casillasSeguras = (filas * columnas) - numMinas;
        casillas = new Button[filas][columnas];
        tablero = new int[filas][columnas];
        reveladas = new boolean[filas][columnas];
        labelDificultad.setText("Dificultad: " + dificultad);
    }

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

    private void obtenerPoderAleatorio() {
        Poder nuevoPoder = Poder.getTodosLosPoderes()[new Random().nextInt(Poder.getTodosLosPoderes().length)];
        poderesDisponibles.add(nuevoPoder);
        labelPoderSeleccionado.setText("¡Nuevo poder obtenido: " + nuevoPoder.getEmoji() + "!");
        actualizarBotonesPoderes();
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

    private void aplicarPoder(int fila, int columna) {
        if (poderSeleccionado == null) return;

        switch (poderSeleccionado.getNombre()) {
            case "Radar":
                revelarArea(fila, columna, 2);
                break;
            case "Fila":
                limpiarFila(fila);
                break;
            case "Columna":
                limpiarColumna(columna);
                break;
        }

        resetearEstadosPoder();
    }

    private void limpiarColumna(int columna) {
        for (int i = 0; i < filas; i++) {
            if (!reveladas[i][columna]) {
                if (tablero[i][columna] >= 0) {
                    revelarCasilla(i, columna);
                } else {
                    // Convertir mina en casilla segura
                    tablero[i][columna] = contarMinasAdyacentes(i, columna);
                    numMinas--;
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

    private void limpiarFila(int fila) {
        for (int j = 0; j < columnas; j++) {
            if (!reveladas[fila][j]) {
                if (tablero[fila][j] >= 0) {
                    revelarCasilla(fila, j);
                } else {
                    // Convertir mina en casilla segura
                    tablero[fila][j] = contarMinasAdyacentes(fila, j);
                    numMinas--;
                    Button casilla = casillas[fila][j];
                    casilla.setStyle("-fx-background-color: gold;");
                    casilla.setText("💫");
                    final int columna = j;
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

    private void protegerCasillaAleatoria() {
        // Buscar una mina aleatoria
        List<Point> minas = new ArrayList<>();
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] < 0 && !reveladas[i][j]) {
                    minas.add(new Point(i, j));
                }
            }
        }

        if (!minas.isEmpty()) {
            Point minaElegida = minas.get(new Random().nextInt(minas.size()));
            int minaFila = (int) minaElegida.getX();
            int minaCol = (int) minaElegida.getY();

            Button casilla = casillas[minaFila][minaCol];
            tablero[minaFila][minaCol] = contarMinasAdyacentes(minaFila, minaCol);
            numMinas--;
            
            casilla.setStyle("-fx-background-color: lightblue;");
            casilla.setText("🛡️");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                if (!reveladas[minaFila][minaCol]) {
                    casilla.setText("");
                }
            }));
            timeline.play();
            actualizarEtiquetas();
        }
    }

    private void protegerCasilla(int fila, int columna) {
        if (tablero[fila][columna] < 0) { // Si es una mina
            tablero[fila][columna] = contarMinasAdyacentes(fila, columna);
            numMinas--;
            Button casilla = casillas[fila][columna];
            casilla.setStyle("-fx-background-color: lightblue;");
            casilla.setText("🛡️");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                if (!reveladas[fila][columna]) {
                    casilla.setText("");
                }
            }));
            timeline.play();
            actualizarEtiquetas();
        }
    }

    private void revelarArea(int fila, int columna, int radio) {
        for (int i = -radio; i <= radio; i++) {
            for (int j = -radio; j <= radio; j++) {
                int newFila = fila + i;
                int newCol = columna + j;

                // Verificar que la posición está dentro del tablero
                if (newFila >= 0 && newFila < filas &&
                        newCol >= 0 && newCol < columnas &&
                        !reveladas[newFila][newCol]) {

                    Button casilla = casillas[newFila][newCol];
                    if (tablero[newFila][newCol] >= 0) {
                        // Revelar casilla segura
                        revelarNumero(newFila, newCol);
                        reveladas[newFila][newCol] = true;
                        casillasSeguras--;
                        puntaje += 10;
                    } else {
                        // Marcar minas con un icono especial
                        casilla.setText("⚠️");
                        casilla.setStyle("-fx-background-color: #FFE5E5;");
                    }
                }
            }
        }
        actualizarEtiquetas();
    }

    // Renombrar y modificar el método eliminarMina para que no necesite parámetros
    private void eliminarMina() {
        // Buscar una mina aleatoria
        List<Point> minas = new ArrayList<>();
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] < 0 && !reveladas[i][j]) {
                    minas.add(new Point(i, j));
                }
            }
        }

        if (!minas.isEmpty()) {
            // Seleccionar mina aleatoria
            Point minaElegida = minas.get(new Random().nextInt(minas.size()));
            int minaFila = (int) minaElegida.getX();
            int minaCol = (int) minaElegida.getY();

            // Eliminar banderín si existe
            Button casillaMinada = casillas[minaFila][minaCol];
            if (casillaMinada.getText().equals("🚩")) {
                casillaMinada.setText("");
            }

            // Convertir mina en casilla normal
            tablero[minaFila][minaCol] = contarMinasAdyacentes(minaFila, minaCol);
            casillaMinada.setStyle("-fx-background-color: gold;");
            numMinas--;

            // Mostrar efecto visual temporal
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

    private void actualizarEtiquetas() {
        labelPuntaje.setText("Puntaje: " + puntaje);
        labelCasillasRestantes.setText("Casillas restantes: " + casillasSeguras);
        labelMinas.setText("Minas: " + numMinas);
    }

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

        AlertaService.mostrarInfo("Fin del juego", mensaje);

        // Volver al menú principal automáticamente
        manejarVolverAlMenu();
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

    private void revelarNumero(int fila, int columna) {
        Button casilla = casillas[fila][columna];
        int valor = tablero[fila][columna];
        casilla.getStyleClass().add("casilla-revelada");
        
        if (valor > 0) {
            casilla.setText(String.valueOf(valor));
            casilla.getStyleClass().add("numero-" + valor);
        }
        casilla.setStyle("-fx-background-color: lightgray;");
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

    public void seleccionarPoder(Poder poder) {
        this.poderSeleccionado = poder;
        this.poderActual = poder.getNombre();

        // Actualizar toda la información del poder
        labelPoderSeleccionado.setText("Poder seleccionado: " + poder.getEmoji());
        labelNombrePoder.setText(poder.getEmoji() + " " + poder.getNombre());
        labelDescripcionPoder.setText(poder.getDescripcion());
    }
}
