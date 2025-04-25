package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.utils.AnimationUtil;
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
import javafx.scene.layout.AnchorPane;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Controlador para la pantalla de inicio de sesión.
 * Maneja la autenticación de usuarios y la navegación a registro y recuperación de contraseña.
 */
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
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView logoImage;

    private final AutenticacionService authService = new AutenticacionService();

    /**
     * Inicializa la pantalla con animaciones
     * Configura la animación del logo y efectos visuales
     */
    @FXML
    private void initialize() {
        // Asegurarse de que mainPane no sea null antes de aplicar la animación
        if (mainPane != null) {
            AnimationUtil.fadeIn(mainPane, 1.0);
        }
        
        // Animación de respiración para el logo
        ScaleTransition breathingAnimation = new ScaleTransition(Duration.seconds(2), logoImage);
        breathingAnimation.setFromX(1.0);
        breathingAnimation.setFromY(1.0);
        breathingAnimation.setToX(1.05);
        breathingAnimation.setToY(1.05);
        breathingAnimation.setAutoReverse(true);
        breathingAnimation.setCycleCount(ScaleTransition.INDEFINITE);
        breathingAnimation.play();
    }

    /**
     * Maneja el intento de inicio de sesión
     * Valida las credenciales y redirige al menú principal
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        // Oculta cualquier mensaje previo
        lblMensaje.setVisible(false);

        String identificador = txtEmail.getText().trim();
        String contrasenia = txtPassword.getText();

        // Validaciones básicas
        if (identificador.isEmpty() || contrasenia.isEmpty()) {
            if (identificador.isEmpty()) AnimationUtil.shake(txtEmail);
            if (contrasenia.isEmpty()) AnimationUtil.shake(txtPassword);
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

        // Modificar la transición al menú principal
        AnimationUtil.fadeOut(mainPane, 0.5, () -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu-principal.fxml"));
                Parent root = loader.load();
                MenuPrincipalController menuCtrl = loader.getController();
                menuCtrl.setUsuarioActual(user);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                AlertaService.mostrarErrorDespuesDeAnimacion("Error", "No se pudo abrir el menú principal.");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleIrARegistro(ActionEvent event) {
        AnimationUtil.slideToLeft(mainPane, 0.5, () -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/views/registro.fxml"));
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                AlertaService.mostrarErrorDespuesDeAnimacion("Error", "No se pudo cargar la pantalla de registro.");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleIrARecuperar(ActionEvent event) {
        AnimationUtil.slideToLeft(mainPane, 0.5, () -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/views/restablecer-contrasenia.fxml"));
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                lblMensaje.setText("No se pudo cargar la recuperación de contraseña.");
                lblMensaje.setVisible(true);
                e.printStackTrace();
            }
        });
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
