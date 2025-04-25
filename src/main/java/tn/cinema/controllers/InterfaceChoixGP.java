package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class InterfaceChoixGP {
    @FXML
    private Button gererDemandesButton;

    @FXML
    private Button gererPublictesButton;

    @FXML
    private Button backButton;

    @FXML
    private Button demandesStatsButton; // Added for Demandes Stats

    @FXML
    private Button publicitesStatsButton; // Added for Publicités Stats

    @FXML
    private void goToInterfaceDemandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceDemandes.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gererDemandesButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading InterfaceDemandes.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void goToInterfacePublictes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfacePublictes.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gererPublictesButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Publicités");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading InterfacePublictes.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void goToDemandesStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandesStats.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) demandesStatsButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Statistiques des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading DemandesStats.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void goToPublicitesStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PublicitesStats.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) publicitesStatsButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Statistiques des Publicités");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading PublicitesStats.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void goBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Login.fxml: " + e.getMessage());
        }
    }
}