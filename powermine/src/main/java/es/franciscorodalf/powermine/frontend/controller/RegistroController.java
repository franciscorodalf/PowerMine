package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import es.franciscorodalf.powermine.backend.service.AutenticacionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class RegistroController {

    @FXML
    private TextField campoUsuario;

    @FXML
    private TextField campoCorreo;

    @FXML
    private PasswordField campoContrasenia;

    @FXML
    private PasswordField campoConfirmarContrasenia;

    @FXML
    private Label etiquetaErrorRegistro;

    private final AutenticacionService authService = new AutenticacionService();

    @FXML
    private void manejarRegistro(ActionEvent event) {
        String usuario = campoUsuario.getText().trim();
        String correo = campoCorreo.getText().trim();
        String contrasenia = campoContrasenia.getText().trim();
        String confirmar = campoConfirmarContrasenia.getText().trim();

        if (usuario.isEmpty() || correo.isEmpty() || contrasenia.isEmpty() || confirmar.isEmpty()) {
            AlertaService.mensajeEnLabel(etiquetaErrorRegistro, "Todos los campos son obligatorios.", true);
            return;
        }

        if (!contrasenia.equals(confirmar)) {
            AlertaService.mensajeEnLabel(etiquetaErrorRegistro, "Las contraseñas no coinciden.", true);
            return;
        }

        Usuario nuevo = new Usuario(0, usuario, correo, contrasenia);

        boolean registrado = authService.registrarUsuario(nuevo);

        if (registrado) {
            AlertaService.mostrarInfo("Registro exitoso", "¡Tu cuenta ha sido creada!");
            redirigirALogin(event);
        } else {
            AlertaService.mensajeEnLabel(etiquetaErrorRegistro, "Ya existe un usuario o correo con esos datos.", true);
        }
    }

    @FXML
    private void manejarIrALogin(ActionEvent event) {
        redirigirALogin(event);
    }

    private void redirigirALogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertaService.mostrarError("Error", "No se pudo cambiar a la pantalla de login.");
        }
    }
}
