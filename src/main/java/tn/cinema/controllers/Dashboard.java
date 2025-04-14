package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tn.cinema.utils.SessionManager;

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

    @FXML
    private Button backButton;
    public void goBackToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
        }
    }






    /// aliiiiiiiiii


    @FXML
    private Button gestionUserButton;

    @FXML
    private Button monCompteButton;

    @FXML
    private void handleGestionUserAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gestionUserButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger AfficherUser.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleMonCompteAction() {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MonCompte.fxml"));
            Parent root = loader.load();
            MonCompte controller = loader.getController();
            controller.setLoggedInUser(SessionManager.getInstance().getLoggedInUser());
            Stage stage = (Stage) monCompteButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Compte");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger MonCompte.fxml: " + e.getMessage());
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    void gestionFilm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionFilms.fxml")); // Adjust path if needed
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();   // Get current window
            Scene scene = new Scene(root);
            stage.setScene(scene); // Set the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debug
        }
    }

    public void goToInterfaceChoixGP(ActionEvent actionEvent) {
    }
}



