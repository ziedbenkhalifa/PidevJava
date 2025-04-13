package tn.cinema.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Front.fxml as the initial scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Front.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Client Dashboard");
        primaryStage.show();
        System.out.println("Application started, Front.fxml loaded.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}