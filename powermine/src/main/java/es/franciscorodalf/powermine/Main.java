package es.franciscorodalf.powermine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import es.franciscorodalf.powermine.backend.dao.UsuarioDAO;
import es.franciscorodalf.powermine.backend.model.Usuario;
import es.franciscorodalf.powermine.backend.service.AutenticacionService;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Crear usuario de prueba para facilitar el login
        crearUsuarioDePrueba();
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 547, 529);
        stage.setTitle("Pantalla Principal");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Crea un usuario de prueba si no existe ninguno en la base de datos
     */
    private void crearUsuarioDePrueba() {
        try {
            AutenticacionService authService = new AutenticacionService();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // Comprobar si ya existe algún usuario
            if (usuarioDAO.obtenerRanking().isEmpty()) {
                // Crear usuario admin de prueba
                Usuario usuario = new Usuario(0, "admin", "admin@powermine.com", "admin");
                boolean registrado = authService.registrarUsuario(usuario);
                
                if (registrado) {
                    System.out.println("✅ Usuario de prueba creado: admin / admin");
                } else {
                    System.out.println("❌ No se pudo crear el usuario de prueba");
                }
            } else {
                System.out.println("✅ Base de datos ya contiene usuarios");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al crear usuario de prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
