package tn.cinema.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
   /* @Override
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
    }*/
      /*  @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/AjouterDemandes.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("ajouter demande");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }*/

 /*   @Override
    public void start(Stage primaryStage) throws Exception {
        // Adjust the path to match your FXML file location
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceDemandes.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Showtime Dashboard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }*/


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Dashboard.fxml as the initial scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Showtime Dashboard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}