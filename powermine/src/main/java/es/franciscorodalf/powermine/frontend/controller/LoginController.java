package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import es.franciscorodalf.powermine.backend.service.AutenticacionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoginController {

    @FXML
    private TextField identificadorField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginErrorLabel;

    private final AutenticacionService authService = new AutenticacionService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String identificador = identificadorField.getText().trim();
        String contrasenia = passwordField.getText().trim();

        if (identificador.isEmpty() || contrasenia.isEmpty()) {
            AlertaService.mensajeEnLabel(loginErrorLabel, "Rellena todos los campos.", true);
            return;
        }

        Usuario usuario = authService.iniciarSesion(identificador, contrasenia);

        if (usuario != null) {
            AlertaService.mensajeEnLabel(loginErrorLabel, "", false);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu-principal.fxml"));
                Parent root = loader.load();

                MenuPrincipalController menuController = loader.getController();
                menuController.setUsuarioActual(usuario);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                AlertaService.mostrarError("Error", "No se pudo cargar el menú principal.");
            }

        } else {
            AlertaService.mensajeEnLabel(loginErrorLabel, "Credenciales incorrectas.", true);
        }
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) {
        cambiarPantalla(event, "/views/registro.fxml");
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        cambiarPantalla(event, "/views/recuperar-contrasenia.fxml");
    }

    private void cambiarPantalla(ActionEvent event, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertaService.mostrarError("Error", "No se pudo cambiar de pantalla.");
        }
    }
}
