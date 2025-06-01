package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import es.franciscorodalf.powermine.backend.service.EstadisticasService;
import es.franciscorodalf.powermine.utils.AnimationUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

/**
 * Controlador del menú principal de la aplicación.
 * Muestra las estadísticas del usuario y permite navegar a las diferentes funcionalidades.
 */
public class MenuPrincipalController {

    @FXML
    private Label labelNombreUsuario;
    @FXML
    private Label labelCorreoUsuario;
    @FXML
    private Label labelPartidasJugadas;
    @FXML
    private Label labelPartidasGanadas;
    @FXML
    private Label labelPuntajeTotal;
    @FXML
    private VBox menuPane;
    @FXML
    private Button btnJugar;
    @FXML
    private Button btnEditarPerfil;
    @FXML
    private Button btnCerrarSesion;

    private final EstadisticasService estadisticasService = new EstadisticasService();
    private Usuario usuarioActual;

    @FXML
    private void initialize() {
        // Esperar al siguiente ciclo de JavaFX para asegurar que los nodos estén inicializados
        Platform.runLater(() -> {
            if (menuPane != null) {
                AnimationUtil.slideFromRight(menuPane, 0.5);
                
                // Añadir efecto hover a los botones solo si existen
                if (btnJugar != null) btnJugar.setOnMouseEntered(e -> AnimationUtil.pulse(btnJugar));
                if (btnEditarPerfil != null) btnEditarPerfil.setOnMouseEntered(e -> AnimationUtil.pulse(btnEditarPerfil));
                if (btnCerrarSesion != null) btnCerrarSesion.setOnMouseEntered(e -> AnimationUtil.pulse(btnCerrarSesion));
            }
        });
    }

    /**
     * Configura el menú para el usuario actual
     * Carga y muestra sus estadísticas
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarInformacionUsuario();
        // Animar labels al cargar información
        AnimationUtil.fadeIn(labelNombreUsuario, 0.5);
        AnimationUtil.fadeIn(labelCorreoUsuario, 0.7);
        AnimationUtil.fadeIn(labelPartidasJugadas, 0.9);
        AnimationUtil.fadeIn(labelPartidasGanadas, 1.1);
        AnimationUtil.fadeIn(labelPuntajeTotal, 1.3);
    }

    /**
     * Carga las estadísticas del usuario desde la base de datos
     * Actualiza la interfaz con la información
     */
    private void cargarInformacionUsuario() {
        if (usuarioActual == null)
            return;

        // Información básica del usuario
        labelNombreUsuario.setText("Usuario: " + usuarioActual.getNombreUsuario());
        labelCorreoUsuario.setText("Correo: " + usuarioActual.getCorreo());

        // Cargar estadísticas
        EstadisticasService estadisticasService = new EstadisticasService();
        int idUsuario = usuarioActual.getId();

        // Obtener y mostrar estadísticas detalladas
        int jugadas = estadisticasService.obtenerPartidasJugadas(idUsuario);
        int ganadas = estadisticasService.obtenerPartidasGanadas(idUsuario);
        int puntaje = estadisticasService.obtenerPuntajeTotal(idUsuario);
        double porcentajeVictorias = jugadas > 0 ? (ganadas * 100.0 / jugadas) : 0;

        labelPartidasJugadas.setText(String.format("Partidas jugadas: %d", jugadas));
        labelPartidasGanadas.setText(String.format("Partidas ganadas: %d (%.1f%%)", ganadas, porcentajeVictorias));
        labelPuntajeTotal.setText(String.format("Puntaje total: %d", puntaje));
    }

    @FXML
    private void manejarCerrarSesion(ActionEvent event) {
        AnimationUtil.fadeOut(menuPane, 0.5, () -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                AlertaService.mostrarErrorDespuesDeAnimacion("Error", "No se pudo volver al login.");
            }
        });
    }

    @FXML
    private void manejarEditarPerfil(ActionEvent event) {
        AnimationUtil.slideToLeft(menuPane, 0.5, () -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/editar-perfil.fxml"));
                Parent root = loader.load();

                EditarPerfilController controller = loader.getController();
                controller.setUsuario(usuarioActual);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                AlertaService.mostrarErrorDespuesDeAnimacion("Error", "No se pudo cargar la pantalla de edición de perfil.");
            }
        });
    }

    @FXML
    private void manejarJugar(ActionEvent event) {
        AnimationUtil.slideToLeft(menuPane, 0.5, () -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/views/seleccion-partida.fxml"));
                Parent root = loader.load();

                SeleccionPartidaController spc = loader.getController();
                spc.setUsuarioActual(usuarioActual);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                AlertaService.mostrarErrorDespuesDeAnimacion("Error", "No se pudo iniciar la selección de partida.");
                e.printStackTrace();
            }
        });
    }

}
