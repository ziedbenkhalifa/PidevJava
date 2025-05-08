package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class interfacePrincipale {




    @FXML
    void handlePagePrincipale(ActionEvent event) {
        System.out.println("clic bouton");
        chargerPage("/tn/cinema/Views/Page1.fxml", "Page Principale", event);
    }


    // Méthode utilitaire pour charger une page et changer la scène
    private void chargerPage(String cheminFxml, String titrePage, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Page1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titrePage);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur de chargement", "Impossible de charger la page : " + titrePage);
        }
    }

    // Méthode pour afficher une alerte en cas d'erreur
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}