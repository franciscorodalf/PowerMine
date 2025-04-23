package es.franciscorodalf.powermine.frontend.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import es.franciscorodalf.powermine.backend.service.AlertaService;

public class RecuperarContraseniaController {

    @FXML private TextField campoIdentificador;
    @FXML private Label etiquetaMensaje;

    @FXML
    private void manejarRecuperacion(ActionEvent event) {
        String identificador = campoIdentificador.getText().trim();
        if (identificador.isEmpty()) {
            etiquetaMensaje.setText("Debe ingresar un correo o nombre de usuario.");
            etiquetaMensaje.setStyle("-fx-text-fill: red;");
            etiquetaMensaje.setVisible(true);
            return;
        }
        // Simula envío de instrucciones (aquí llamarías un servicio real)
        AlertaService.mostrarInfo("Recuperación", "Se han enviado instrucciones a: " + identificador);
        etiquetaMensaje.setText("Instrucciones enviadas correctamente.");
        etiquetaMensaje.setStyle("-fx-text-fill: green;");
        etiquetaMensaje.setVisible(true);
    }

    @FXML
    private void manejarIrALogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo cargar la pantalla de inicio de sesión.");
            e.printStackTrace();
        }
    }
}
