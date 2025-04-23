package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AlertaService;
import es.franciscorodalf.powermine.backend.service.EstadisticasService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class MenuPrincipalController {

    @FXML
    private Label labelNombreUsuario;
    @FXML
    private Label labelCorreoUsuario;
    @FXML
    private Label labelPartidasJugadas;
    @FXML
    private Label labelPartidasGanadas;
    @FXML
    private Label labelPuntajeTotal;

    private final EstadisticasService estadisticasService = new EstadisticasService();
    private Usuario usuarioActual;

    /** Este es el método que debes usar para pasar el usuario tras el login */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarInformacionUsuario();
    }

    private void cargarInformacionUsuario() {
        if (usuarioActual == null)
            return;

        // Información básica del usuario
        labelNombreUsuario.setText("Usuario: " + usuarioActual.getNombreUsuario());
        labelCorreoUsuario.setText("Correo: " + usuarioActual.getCorreo());

        // Cargar estadísticas
        EstadisticasService estadisticasService = new EstadisticasService();
        int idUsuario = usuarioActual.getId();

        // Obtener y mostrar estadísticas detalladas
        int jugadas = estadisticasService.obtenerPartidasJugadas(idUsuario);
        int ganadas = estadisticasService.obtenerPartidasGanadas(idUsuario);
        int puntaje = estadisticasService.obtenerPuntajeTotal(idUsuario);
        double porcentajeVictorias = jugadas > 0 ? (ganadas * 100.0 / jugadas) : 0;

        labelPartidasJugadas.setText(String.format("Partidas jugadas: %d", jugadas));
        labelPartidasGanadas.setText(String.format("Partidas ganadas: %d (%.1f%%)", ganadas, porcentajeVictorias));
        labelPuntajeTotal.setText(String.format("Puntaje total: %d", puntaje));
    }

    @FXML
    private void manejarCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo volver al login.");
        }
    }

    @FXML
    private void manejarEditarPerfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/editar-perfil.fxml"));
            Parent root = loader.load();

            EditarPerfilController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo cargar la pantalla de edición de perfil.");
        }
    }

    @FXML
    private void manejarJugar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/seleccion-partida.fxml"));
            Parent root = loader.load();

            // 1️⃣ Obtienes el controller de la pantalla de selección...
            SeleccionPartidaController spc = loader.getController();
            // 2️⃣ Le pasas el usuario logueado
            spc.setUsuarioActual(usuarioActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            AlertaService.mostrarError("Error", "No se pudo iniciar la selección de partida.");
            e.printStackTrace();
        }
    }

}
