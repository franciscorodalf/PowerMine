package es.franciscorodalf.powermine.frontend.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/**
 * Controlador para la ventana de información de poderes.
 * Muestra detalles sobre los diferentes poderes disponibles en el juego.
 */
public class InfoPoderesController {
    
    /**
     * Cierra la ventana de información de poderes
     * @param event Evento del botón cerrar
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
