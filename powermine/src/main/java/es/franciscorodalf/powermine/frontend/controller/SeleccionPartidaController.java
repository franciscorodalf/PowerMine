package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.stage.Modality;

/**
 * Controlador para la selección de dificultad de partida.
 * Permite al usuario elegir entre diferentes niveles de dificultad antes de iniciar el juego.
 */
public class SeleccionPartidaController {

    @FXML private ToggleGroup grupoDificultad;
    @FXML private RadioButton opcionFacil;
    @FXML private RadioButton opcionMedio;
    @FXML private RadioButton opcionDificil;

    private Usuario usuarioActual;

    /**
     * Recibe el usuario desde el menú principal
     * @param usuario Usuario actual del juego
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        System.out.println("Usuario en SeleccionPartidaController: " + usuario);
    }

    /**
     * Maneja el inicio de una nueva partida con la dificultad seleccionada
     * @param event Evento del botón iniciar
     */
    @FXML
    private void manejarIniciarPartida(ActionEvent event) {
        if (usuarioActual == null) {
            System.err.println("❌ Error: usuarioActual es null en SeleccionPartidaController");
        }

        String nivel;
        if (opcionFacil.isSelected()) {
            nivel = "Fácil";
        } else if (opcionMedio.isSelected()) {
            nivel = "Medio";
        } else {
            nivel = "Difícil";
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/tablero.fxml")
            );
            Parent root = loader.load();

            TableroController tableroCtrl = loader.getController();
            tableroCtrl.iniciarPartida(usuarioActual, nivel);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            stage.setWidth(1000);  // Ancho más grande
            stage.setHeight(850);  // Alto más grande
            
            stage.setScene(new Scene(root));
            stage.centerOnScreen(); // Centrar la ventana en la pantalla
            stage.setTitle("PowerMine - " + nivel); // Añadir título con dificultad
            stage.show();

        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo iniciar la partida.");
            e.printStackTrace();
        }
    }

    @FXML
    private void manejarVolverAlMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/menu-principal.fxml")
            );
            Parent root = loader.load();

            MenuPrincipalController menuCtrl = loader.getController();
            menuCtrl.setUsuarioActual(usuarioActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo volver al menú principal.");
            e.printStackTrace();
        }
    }

    /**
     * Muestra la ventana de información sobre los poderes disponibles
     */
    @FXML
    private void mostrarInfoPoderes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/info-poderes.fxml"));
            Parent root = loader.load();
            
            Stage infoPoderesStage = new Stage();
            infoPoderesStage.initModality(Modality.APPLICATION_MODAL);
            infoPoderesStage.setTitle("Información de Poderes");
            infoPoderesStage.setScene(new Scene(root));
            infoPoderesStage.show();
        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo mostrar la información de poderes");
        }
    }
}
