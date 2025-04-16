package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;
import tn.cinema.entities.User;
import tn.cinema.services.CourService;
import tn.cinema.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AffichageListCoursFront extends FrontzController implements Initializable {

    @FXML
    private GridPane coursGrid;
    @FXML
    private Button courSubButton;

    @FXML
    private Button seanceSubButton;

    private CourService courService = new CourService();
    private List<Cour> allCours = new ArrayList<>();
    // ID du client par défaut
    private final int clientId = 1;
    // Liste des cour_id auxquels le client a participé
    private List<Integer> participatedCoursIds;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Charger tous les cours
            allCours = courService.recuperer();

            // Charger les participations pour le client par défaut
            participatedCoursIds = courService.recupererParticipations(clientId);

            // Afficher tous les cours directement
            displayCours(allCours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des cours : " + e.getMessage());
        }
    }

    private void displayCours(List<Cour> coursList) {
        coursGrid.getChildren().clear();

        int row = 0;
        int col = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Cour cour : coursList) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #1c2526; -fx-background-radius: 10; -fx-padding: 10;");
            card.setPrefWidth(250);
            card.setPrefHeight(150);

            Label typeLabel = new Label(cour.getTypeCour());
            typeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

            Label coutLabel = new Label(cour.getCout() + " DT");
            coutLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

            Label dateDebutLabel = new Label("Date Debut: " + cour.getDateDebut().format(formatter));
            dateDebutLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

            Label dateFinLabel = new Label("Date Fin: " + cour.getDateFin().format(formatter));
            dateFinLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

            Button showButton = new Button("SHOW");
            showButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5;");
            showButton.setOnAction(event -> handleShowAction(cour));

            Button actionButton;
            if (participatedCoursIds.contains(cour.getId())) {
                actionButton = new Button("QUITTER");
                actionButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                actionButton.setOnAction(event -> handleQuitterAction(cour));
            } else {
                actionButton = new Button("PARTICIPER");
                actionButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5;");
                actionButton.setOnAction(event -> handleParticiperAction(cour));
            }

            HBox buttonsBox = new HBox(10);
            buttonsBox.getChildren().addAll(showButton, actionButton);
            card.getChildren().addAll(typeLabel, coutLabel, dateDebutLabel, dateFinLabel, buttonsBox);

            // Associer le cours à la carte pour pouvoir le récupérer lors du clic
            card.setUserData(cour);

            coursGrid.add(card, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    private void handleShowAction(Cour cour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailsCour.fxml"));
            Parent root = loader.load();

            AfficherDetailsCour controller = loader.getController();
            controller.loadDetails(cour);

            Stage stage = (Stage) coursGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers les détails du cours : " + e.getMessage());
        }
    }

    private void handleParticiperAction(Cour cour) {
        try {
            // Récupérer l'utilisateur connecté depuis la session
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            // Appeler le service avec uniquement l'ID du cours (user ID est géré en interne)
            courService.ajouterParticipation(cour.getId());

            // Mettre à jour la liste des participations
            participatedCoursIds.add(cour.getId());

            showAlert("Succès", "Vous avez participé au cours : " + cour.getTypeCour());

            // Rafraîchir l'affichage
            displayCours(allCours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la participation au cours : " + e.getMessage());
        }
    }

    private void handleQuitterAction(Cour cour) {
        try {
            // Retirer le client de la liste des participants via le service
            courService.supprimerParticipation(clientId, cour.getId());

            // Mettre à jour la liste des participations
            participatedCoursIds.remove(Integer.valueOf(cour.getId()));

            showAlert("Succès", "Vous avez quitté le cours : " + cour.getTypeCour());

            // Rafraîchir l'affichage
            displayCours(allCours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'annulation de la participation : " + e.getMessage());
        }
    }

    private void showAlertt(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    @FXML
    private void toggleSubButtons() {
        boolean areSubButtonsVisible = courSubButton.isVisible();
        courSubButton.setVisible(!areSubButtonsVisible);
        seanceSubButton.setVisible(!areSubButtonsVisible);
    }

    @FXML
    private void retourAccueilAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontZ.fxml")); // chemin vers ton fichier FXML d'accueil
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}