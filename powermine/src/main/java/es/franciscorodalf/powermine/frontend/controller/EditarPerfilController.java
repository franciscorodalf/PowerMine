package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AutenticacionService;
import es.franciscorodalf.powermine.utils.ValidadorFormulario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class EditarPerfilController {

    @FXML
    private TextField campoNombreUsuario;
    @FXML
    private TextField campoCorreo;
    @FXML
    private PasswordField campoNuevaContrasenia;
    @FXML
    private PasswordField campoConfirmarContrasenia;
    @FXML
    private Label etiquetaError;
    @FXML
    private Label etiquetaExito;

    private Usuario usuario;
    private final AutenticacionService authService = new AutenticacionService();

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        campoNombreUsuario.setText(usuario.getNombreUsuario());
        campoCorreo.setText(usuario.getCorreo());
    }

    @FXML
    private void manejarGuardarCambios(ActionEvent event) {
        String nuevoNombre = campoNombreUsuario.getText().trim();
        String nuevaPass = campoNuevaContrasenia.getText();
        String confirmarPass = campoConfirmarContrasenia.getText();

        if (ValidadorFormulario.estaVacio(nuevoNombre)) {
            mostrarMensaje("El nombre de usuario no puede estar vacío.", true);
            return;
        }

        if (ValidadorFormulario.contieneEspacios(nuevoNombre)) {
            mostrarMensaje("El nombre de usuario no debe contener espacios.", true);
            return;
        }

        if (!ValidadorFormulario.estaVacio(nuevaPass)) {
            if (!ValidadorFormulario.contrasenaValida(nuevaPass)) {
                mostrarMensaje("Contraseña inválida. Debe tener al menos 4 caracteres y sin espacios.", true);
                return;
            }

            if (!ValidadorFormulario.contrasenasCoinciden(nuevaPass, confirmarPass)) {
                mostrarMensaje("Las contraseñas no coinciden.", true);
                return;
            }

            usuario.setContrasenia(nuevaPass);
        }

        usuario.setNombreUsuario(nuevoNombre);

        boolean actualizado = authService.actualizarUsuario(usuario);
        if (actualizado) {
            mostrarMensaje("Cambios guardados correctamente.", false);
        } else {
            AlertaService.mostrarError("Error", "No se pudo guardar los cambios.");
        }
    }

    @FXML
    private void manejarVolverAlMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu-principal.fxml"));
            Parent root = loader.load();

            MenuPrincipalController controller = loader.getController();
            controller.setUsuarioActual(usuario);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo volver al menú principal.");
        }
    }

    private void mostrarMensaje(String texto, boolean esError) {
        etiquetaError.setVisible(esError);
        etiquetaExito.setVisible(!esError);

        if (esError) {
            etiquetaError.setText(texto);
        } else {
            etiquetaExito.setText(texto);
        }
    }
}
