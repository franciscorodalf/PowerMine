package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AutenticacionService;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblMensaje;
    @FXML
    private Hyperlink linkRegistro;
    @FXML
    private Hyperlink linkRecuperar;

    private final AutenticacionService authService = new AutenticacionService();

    @FXML
    private void handleLogin(ActionEvent event) {
        // Oculta cualquier mensaje previo
        lblMensaje.setVisible(false);

        String identificador = txtEmail.getText().trim();
        String contrasenia = txtPassword.getText();

        // Validaciones básicas
        if (identificador.isEmpty() || contrasenia.isEmpty()) {
            lblMensaje.setText("Por favor completa todos los campos.");
            lblMensaje.setVisible(true);
            return;
        }

        // Intento de login
        Usuario user = authService.iniciarSesion(identificador, contrasenia);
        if (user == null) {
            lblMensaje.setText("Correo o contraseña incorrectos.");
            lblMensaje.setVisible(true);
            return;
        }

        // Carga menú principal
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/menu-principal.fxml"));
            Parent root = loader.load();

            // Pasar usuario al controlador del menú
            MenuPrincipalController menuCtrl = loader.getController();
            menuCtrl.setUsuarioActual(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            // Mensaje crítico en alerta
            AlertaService.mostrarError("Error", "No se pudo abrir el menú principal.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIrARegistro(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/registro.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            lblMensaje.setText("No se pudo cargar la pantalla de registro.");
            lblMensaje.setVisible(true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIrARecuperar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/restablecer-contrasenia.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            lblMensaje.setText("No se pudo cargar la recuperación de contraseña.");
            lblMensaje.setVisible(true);
            e.printStackTrace();
        }
    }

    @FXML
    private void manejarRanking() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/ranking.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
