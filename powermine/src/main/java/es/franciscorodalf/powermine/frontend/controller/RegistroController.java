package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import es.franciscorodalf.powermine.backend.service.AutenticacionService;
import es.franciscorodalf.powermine.utils.ValidadorFormulario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistroController {

    @FXML
    private TextField campoUsuario;
    @FXML
    private TextField campoCorreo;
    @FXML
    private PasswordField campoContrasena;
    @FXML
    private PasswordField campoConfirmarContrasena;
    @FXML
    private Label etiquetaErrorRegistro;

    private final AutenticacionService authService = new AutenticacionService();

    @FXML
    private void manejarRegistro(ActionEvent event) {
        String nombreUsuario = campoUsuario.getText().trim();
        String correo = campoCorreo.getText().trim();
        String contrasena = campoContrasena.getText();
        String confirmar = campoConfirmarContrasena.getText();

        if (ValidadorFormulario.estaVacio(nombreUsuario) || ValidadorFormulario.estaVacio(correo)
                || ValidadorFormulario.estaVacio(contrasena) || ValidadorFormulario.estaVacio(confirmar)) {
            mostrarMensaje("Completa todos los campos.", true);
            return;
        }

        if (ValidadorFormulario.contieneEspacios(nombreUsuario)) {
            mostrarMensaje("El nombre de usuario no debe contener espacios.", true);
            return;
        }

        if (!ValidadorFormulario.esCorreoValido(correo)) {
            mostrarMensaje("El correo electrónico no es válido.", true);
            return;
        }

        if (!ValidadorFormulario.contrasenasCoinciden(contrasena, confirmar)) {
            mostrarMensaje("Las contraseñas no coinciden.", true);
            return;
        }

        if (!ValidadorFormulario.contrasenaValida(contrasena)) {
            mostrarMensaje("Contraseña muy corta o contiene espacios.", true);
            return;
        }

        Usuario nuevo = new Usuario(0, nombreUsuario, correo, contrasena);
        boolean registrado = authService.registrarUsuario(nuevo);

        if (registrado) {
            mostrarMensaje("¡Registro exitoso! Redirigiendo...", false);
            redirigirAlLogin(event);
        } else {
            mostrarMensaje("Nombre de usuario o correo ya en uso.", true);
        }
    }

    @FXML
    private void manejarIrALogin(ActionEvent event) {
        redirigirAlLogin(event);
    }

    private void redirigirAlLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo abrir la pantalla de inicio.");
        }
    }

    private void mostrarMensaje(String texto, boolean esError) {
        etiquetaErrorRegistro.setText(texto);
        etiquetaErrorRegistro.setStyle(esError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        etiquetaErrorRegistro.setVisible(true);
    }
}
