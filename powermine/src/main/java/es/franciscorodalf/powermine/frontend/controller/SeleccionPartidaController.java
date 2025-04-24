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

public class SeleccionPartidaController {

    @FXML private ToggleGroup grupoDificultad;
    @FXML private RadioButton opcionFacil;
    @FXML private RadioButton opcionMedio;
    @FXML private RadioButton opcionDificil;

    private Usuario usuarioActual;

    /** Recibido desde MenuPrincipalController */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        // Depurar: mostrar por consola el usuario recibido
        System.out.println("Usuario en SeleccionPartidaController: " + usuario);
    }

    /** Botón “Iniciar Partida” */
    @FXML
    private void manejarIniciarPartida(ActionEvent event) {
        // Depurar: confirmar que usuarioActual no es nulo
        if (usuarioActual == null) {
            System.err.println("❌ Error: usuarioActual es null en SeleccionPartidaController");
        }

        // Determinar nivel según selección
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

            // Pasamos usuario y nivel al TableroController
            TableroController tableroCtrl = loader.getController();
            tableroCtrl.iniciarPartida(usuarioActual, nivel);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo iniciar la partida.");
            e.printStackTrace();
        }
    }

    /** Botón “Volver” */
    @FXML
    private void manejarVolverAlMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/menu-principal.fxml")
            );
            Parent root = loader.load();

            // Devolvemos el usuario al menú principal
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
