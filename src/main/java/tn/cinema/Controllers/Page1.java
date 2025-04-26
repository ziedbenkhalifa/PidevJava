package tn.cinema.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Page1 {

    @FXML
    private void gererSalle(ActionEvent event) {
        chargerPage("/Views/ListeSalle.fxml", "Gestion des Salles");
    }

    @FXML
    private void gererEquipement(ActionEvent event) {
        chargerPage("/Views/ListeEquipement.fxml", "Gestion des Équipements");
    }

    @FXML
    private void ChatBotEvent(ActionEvent event) {
        chargerPage("/Views/ChatBotController.fxml", "Chatbot");
    }

    @FXML
    private void statistiques(ActionEvent event) {
        chargerPage("/Views/Statistique.fxml", "Statistiques");
    }

    private void chargerPage(String cheminFxml, String titrePage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));
            Parent root = loader.load(); // Utilisez Parent pour une flexibilité maximale
            Stage stage = new Stage();
            stage.setTitle(titrePage);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur de chargement", "Impossible de charger la page : " + titrePage);
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
