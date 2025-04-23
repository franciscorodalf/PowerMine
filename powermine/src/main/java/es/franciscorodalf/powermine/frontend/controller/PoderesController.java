package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.model.Poder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import java.util.List;
import javafx.scene.control.Tooltip;

public class PoderesController {
    @FXML
    private HBox contenedorPoderes;
    
    private TableroController tableroController;

    public void setTableroController(TableroController controller) {
        this.tableroController = controller;
    }

    @FXML
    public void initialize() {
        // Guardar referencia a este controlador en el contenedor
        contenedorPoderes.setUserData(this);
    }

    public void actualizarPoderes(List<Poder> poderes) {
        contenedorPoderes.getChildren().clear();
        for (Poder poder : poderes) {
            Button btn = new Button(poder.getEmoji());
            btn.setStyle("-fx-font-size: 20px;");
            btn.setMinSize(50, 50);
            btn.setMaxSize(50, 50);
            
            // Tooltip con información del poder
            Tooltip.install(btn, new Tooltip(poder.getNombre()));
            
            btn.setOnAction(e -> seleccionarPoder(poder));
            contenedorPoderes.getChildren().add(btn);
        }
    }

    private void seleccionarPoder(Poder poder) {
        if (tableroController != null) {
            tableroController.seleccionarPoder(poder);
        }
    }
}
