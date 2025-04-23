package es.franciscorodalf.powermine.frontend.controller;

import es.franciscorodalf.powermine.backend.dao.UsuarioDAO;
import es.franciscorodalf.powermine.backend.model.RankingUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.control.TextField;

public class RankingController {
    @FXML private TableView<RankingUsuario> tablaRanking;
    @FXML private TableColumn<RankingUsuario, Integer> posicionColumn;
    @FXML private TableColumn<RankingUsuario, String> jugadorColumn;
    @FXML private TableColumn<RankingUsuario, Integer> puntajeColumn;
    @FXML private TableColumn<RankingUsuario, Integer> victoriasColumn;
    @FXML private TextField txtBuscar;

    @FXML
    public void initialize() {
        posicionColumn.setCellValueFactory(cellData -> cellData.getValue().posicionProperty().asObject());
        jugadorColumn.setCellValueFactory(cellData -> cellData.getValue().nombreUsuarioProperty());
        puntajeColumn.setCellValueFactory(cellData -> cellData.getValue().puntajeTotalProperty().asObject());
        victoriasColumn.setCellValueFactory(cellData -> cellData.getValue().victoriasProperty().asObject());

        // Añadir listener para búsqueda
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarRanking(newValue);
        });

        cargarRanking("");
    }

    private void cargarRanking(String filtro) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        tablaRanking.setItems(FXCollections.observableArrayList(
            usuarioDAO.buscarUsuarios(filtro)
        ));
    }

    @FXML
    private void manejarVolver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) tablaRanking.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
