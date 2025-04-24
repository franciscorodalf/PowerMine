package es.franciscorodalf.powermine.frontend.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class InfoPoderesController {
    
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
