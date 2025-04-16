package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AfficherDetailsCour {

    @FXML
    private Label typeCourLabel;

    @FXML
    private Label coutLabel;

    @FXML
    private Label dateDebutLabel;

    @FXML
    private Label dateFinLabel;

    @FXML
    private Button courSubButton;

    @FXML
    private Button seanceSubButton;


    public void loadDetails(Cour cour) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        typeCourLabel.setText("Type de Cours: " + cour.getTypeCour());
        coutLabel.setText("Coût: " + cour.getCout() + " DT");
        dateDebutLabel.setText("Date de Début: " + cour.getDateDebut().format(formatter));
        dateFinLabel.setText("Date de Fin: " + cour.getDateFin().format(formatter));
    }

    @FXML
    private void retourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) typeCourLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la navigation : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void goCourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) courSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation vers la page Cours : " + e.getMessage());
        }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void toggleSubButtons() {
        boolean areSubButtonsVisible = courSubButton.isVisible();
        courSubButton.setVisible(!areSubButtonsVisible);
        seanceSubButton.setVisible(!areSubButtonsVisible);
    }
}