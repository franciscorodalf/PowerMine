package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import es.franciscorodalf.powermine.backend.service.AutenticacionService;
import es.franciscorodalf.powermine.utils.ValidadorFormulario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * Controlador para el restablecimiento de contraseña.
 * Permite al usuario establecer una nueva contraseña después de la recuperación.
 */
public class RestablecerContraseniaController {

    @FXML
    private PasswordField campoNuevaContrasenia;
    @FXML
    private PasswordField campoConfirmarContrasenia;
    @FXML
    private Label etiquetaMensaje;

    private Usuario usuario;
    private final AutenticacionService authService = new AutenticacionService();

    /**
     * Establece el usuario cuya contraseña se va a restablecer
     * @param usuario Usuario a modificar
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Maneja la validación y guardado de la nueva contraseña
     * @param event Evento del botón guardar
     */
    @FXML
    private void manejarGuardar(ActionEvent event) {
        String nueva = campoNuevaContrasenia.getText();
        String confirmar = campoConfirmarContrasenia.getText();

        if (ValidadorFormulario.estaVacio(nueva) || ValidadorFormulario.estaVacio(confirmar)) {
            mostrarMensaje("Completa todos los campos.", true);
            return;
        }

        if (!ValidadorFormulario.contrasenasCoinciden(nueva, confirmar)) {
            mostrarMensaje("Las contraseñas no coinciden.", true);
            return;
        }

        if (!ValidadorFormulario.contrasenaValida(nueva)) {
            mostrarMensaje("Contraseña inválida: mínimo 4 caracteres, sin espacios.", true);
            return;
        }

        usuario.setContrasenia(nueva);
        boolean actualizado = authService.actualizarUsuario(usuario);

        if (actualizado) {
            mostrarMensaje("¡Contraseña actualizada! Redirigiendo al inicio...", false);
            redirigirAlLogin(event);
        } else {
            AlertaService.mostrarError("Error", "No se pudo actualizar la contraseña.");
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
            AlertaService.mostrarError("Error", "No se pudo abrir la pantalla de inicio de sesión.");
        }
    }

    private void mostrarMensaje(String texto, boolean esError) {
        etiquetaMensaje.setText(texto);
        etiquetaMensaje.setStyle(esError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        etiquetaMensaje.setVisible(true);
    }
}
