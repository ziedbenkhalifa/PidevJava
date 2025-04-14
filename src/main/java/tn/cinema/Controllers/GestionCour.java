package tn.cinema.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionCour {
    @FXML
    private void handleGererSeances(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AffichageListSeances.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors de la navigation vers AffichageListSeances : " + e.getMessage());
        }
    }

    @FXML
    private void handleGererCours(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/affichageListCours.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors de la navigation vers GestionCour : " + e.getMessage());
        }
    }


}
