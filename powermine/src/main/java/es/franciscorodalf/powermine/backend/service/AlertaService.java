package es.franciscorodalf.powermine.backend.service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class AlertaService {

    /**
     * Muestra una alerta de error con el título y mensaje proporcionados.
     *
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje de la alerta.
     */
    public static void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de información con el título y mensaje proporcionados.
     *
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje de la alerta.
     */
    public static void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de advertencia con el título y mensaje proporcionados.
     *
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje de la alerta.
     */
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje en un Label con el color correspondiente según si es un
     * error o no.
     *
     * @param label   El Label donde se mostrará el mensaje.
     * @param mensaje El mensaje a mostrar.
     * @param esError true si es un error, false si es un mensaje de éxito.
     */
    public static void mensajeEnLabel(Label label, String mensaje, boolean esError) {
        label.setText(mensaje);

        if (esError) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.GREEN);
        }

        label.setVisible(true);
    }

    public static void mostrarErrorDespuesDeAnimacion(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.show(); // Usar show() en lugar de showAndWait()
        });
    }

    public static void mostrarInfoDespuesDeAnimacion(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.show(); // Usar show() en lugar de showAndWait()
        });
    }

}
