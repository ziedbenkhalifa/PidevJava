package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionCour {

    @FXML
    private Button btnGererSeances;

    @FXML
    private Button viewStatsCoursButton;

    @FXML
    private Button viewStatsSeancesButton;

    @FXML
    private void handleGererSeances(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AffichageListSeances.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la navigation vers AffichageListSeances : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleGestionCours(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/affichageListCours.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la navigation vers affichageListCours : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void retourAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            Stage stage = (Stage) btnGererSeances.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors du retour au tableau de bord : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void viewStatsCoursAction(ActionEvent event) {
        try {
            // Naviguer vers la fenêtre des statistiques
            Parent root = FXMLLoader.load(getClass().getResource("/StatsCoursChart.fxml"));
            Stage stage = (Stage) viewStatsCoursButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'affichage des statistiques : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void viewStatsSeancesAction(ActionEvent event) {
        try {
            // Naviguer vers la fenêtre des statistiques
            Parent root = FXMLLoader.load(getClass().getResource("/StatsSeanceChart.fxml"));
            Stage stage = (Stage) viewStatsSeancesButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'affichage des statistiques : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }}