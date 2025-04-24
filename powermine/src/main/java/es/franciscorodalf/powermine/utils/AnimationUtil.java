package es.franciscorodalf.powermine.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Utilidad para animar elementos de la interfaz gráfica
 */
public class AnimationUtil {
    
    /**
     * Realiza una animación de fundido de entrada
     */
    public static void fadeIn(Node node, double duration) {
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Realiza una animación de fundido de salida
     */
    public static void fadeOut(Node node, double duration, Runnable onFinish) {
        FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> onFinish.run());
        fade.play();
    }

    /**
     * Realiza una animación de deslizamiento desde la derecha
     */
    public static void slideFromRight(Node node, double duration) {
        double endX = node.getLayoutX();
        double startX = endX + node.getBoundsInParent().getWidth();
        
        node.setLayoutX(startX);
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.layoutXProperty(), startX)),
            new KeyFrame(Duration.seconds(duration), new KeyValue(node.layoutXProperty(), endX))
        );
        timeline.play();
    }

    /**
     * Realiza una animación de deslizamiento hacia la izquierda
     */
    public static void slideToLeft(Node node, double duration, Runnable onFinish) {
        double startX = node.getLayoutX();
        double endX = startX - node.getBoundsInParent().getWidth();
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.layoutXProperty(), startX)),
            new KeyFrame(Duration.seconds(duration), new KeyValue(node.layoutXProperty(), endX))
        );
        timeline.setOnFinished(e -> onFinish.run());
        timeline.play();
    }

    /**
     * Realiza una animación de sacudida para indicar error
     */
    public static void shake(Node node) {
        double originalX = node.getLayoutX();
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.layoutXProperty(), originalX)),
            new KeyFrame(Duration.millis(100), new KeyValue(node.layoutXProperty(), originalX - 10)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.layoutXProperty(), originalX + 10)),
            new KeyFrame(Duration.millis(300), new KeyValue(node.layoutXProperty(), originalX - 10)),
            new KeyFrame(Duration.millis(400), new KeyValue(node.layoutXProperty(), originalX))
        );
        timeline.play();
    }

    /**
     * Realiza una transición suave entre dos pantallas
     */
    public static void transitionBetweenPanes(Pane currentPane, Pane newPane, boolean slideDirection) {
        // true = slide right, false = slide left
        double width = currentPane.getWidth();
        newPane.setLayoutX(slideDirection ? width : -width);
        
        // Añadir nuevo panel
        ((Pane) currentPane.getParent()).getChildren().add(newPane);
        
        // Animar ambos paneles
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(currentPane.layoutXProperty(), 0),
                new KeyValue(newPane.layoutXProperty(), slideDirection ? width : -width)
            ),
            new KeyFrame(Duration.seconds(0.5),
                new KeyValue(currentPane.layoutXProperty(), slideDirection ? -width : width),
                new KeyValue(newPane.layoutXProperty(), 0)
            )
        );
        
        timeline.setOnFinished(e -> {
            ((Pane) currentPane.getParent()).getChildren().remove(currentPane);
        });
        
        timeline.play();
    }

    /**
     * Realiza una animación de pulso para destacar un elemento
     */
    public static void pulse(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.5), node);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
    }
}
