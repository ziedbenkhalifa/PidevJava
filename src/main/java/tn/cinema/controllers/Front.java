package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class Front {
    @FXML
    private Button publicitesButton;
    @FXML
    private Button demandeSubButton;
    @FXML
    private Button publiciteSubButton;

    @FXML
    public void toggleSubButtons(ActionEvent event) {
        boolean isVisible = demandeSubButton.isVisible();
        demandeSubButton.setVisible(!isVisible);
        publiciteSubButton.setVisible(!isVisible);
    }

    @FXML
    public void goToDemandeClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandeClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToPubliciteClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PubliciteClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        // Placeholder for logout logic
        // Example: Navigate to login page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void filmsButtonClicked(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FrontFilm.fxml")); // Adjust path if needed
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();   // Get current window
            Scene scene = new Scene(root);
            stage.setScene(scene); // Set the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debug
        }


    }


    @FXML
    void affichage(ActionEvent event) {
        try {
            // Charger la scène FXML qui affiche la liste des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProduits.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Remplacer le contenu de la scène actuelle par root

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des produits.");
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}