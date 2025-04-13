package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Dashboard {
    @FXML
    private Button gestionPubButton;

    @FXML
    private void goToInterfaceChoixGP() {
        try {
            // Load InterfaceChoixGP.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceChoixGP.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gestionPubButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion Publicités");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceChoixGP.fxml: " + e.getMessage());
        }
    }

    @FXML
    private Button gererDemandesButton;
    public void goToInterfaceDemandes(ActionEvent actionEvent) {
        try {
            // Load InterfaceDemandes.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceDemandes.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gererDemandesButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceDemandes.fxml: " + e.getMessage());
        }
    }
    @FXML
    private Button gererPublictesButton;
    public void goToInterfacePublictes(ActionEvent actionEvent) {
        try {
            // Load InterfaceDemandes.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfacePublicites.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gererDemandesButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceDemandes.fxml: " + e.getMessage());
        }
    }


}



