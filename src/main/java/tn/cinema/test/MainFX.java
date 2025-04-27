package tn.cinema.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Main;

import java.util.logging.Logger;

public class MainFX extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Ajouter Cour");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        LOGGER.info("Arrêt de l'application JavaFX.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}