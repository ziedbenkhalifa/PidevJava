package tn.cinema.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AfficherDetailsCour {

    private static final Logger LOGGER = Logger.getLogger(AfficherDetailsCour.class.getName());

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

    private Cour currentCour;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) typeCourLabel.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                LOGGER.info("Fermeture de l'application détectée.");
            });
        });
    }

    public void loadDetails(Cour cour) {
        this.currentCour = cour;
        typeCourLabel.setText("Type de Cours: " + (cour.getTypeCour() != null ? cour.getTypeCour() : "Non défini"));
        coutLabel.setText("Coût: " + cour.getCout() + " DT");
        dateDebutLabel.setText("Date de Début: " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "Non défini"));
        dateFinLabel.setText("Date de Fin: " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "Non défini"));
    }

    @FXML
    private void retourAction() {
        try {
            LOGGER.info("Tentative de navigation vers AffichageListCoursFront.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Le fichier FXML /AffichageListCoursFront.fxml n'a pas été trouvé.");
                showAlert("Erreur", "Le fichier FXML /AffichageListCoursFront.fxml n'a pas été trouvé.");
                return;
            }
            Parent root = loader.load();
            AffichageListCoursFront controller = loader.getController();
            controller.refreshParticipations();
            controller.displayCours(controller.allCours);

            Stage stage = (Stage) typeCourLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la navigation vers AffichageListCoursFront.fxml", e);
            showAlert("Erreur", "Erreur lors de la navigation vers AffichageListCoursFront.fxml : " + e.getMessage());
        }
    }

    @FXML
    private void goCourAction() {
        try {
            LOGGER.info("Tentative de navigation vers AffichageListCoursFront.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Le fichier FXML /AffichageListCoursFront.fxml n'a pas été trouvé.");
                showAlert("Erreur", "Le fichier FXML /AffichageListCoursFront.fxml n'a pas été trouvé.");
                return;
            }
            Parent root = loader.load();
            AffichageListCoursFront controller = loader.getController();
            controller.refreshParticipations();
            controller.displayCours(controller.allCours);

            Stage stage = (Stage) courSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la navigation vers AffichageListCoursFront.fxml", e);
            showAlert("Erreur", "Erreur lors de la navigation vers AffichageListCoursFront.fxml : " + e.getMessage());
        }
    }

    @FXML
    private void goSeanceAction() {
        try {
            LOGGER.info("Tentative de navigation vers AffichageListSeancesFront.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListSeancesFront.fxml"));
            if (loader.getLocation() == null) {
                LOGGER.severe("Le fichier FXML /AffichageListSeancesFront.fxml n'a pas été trouvé.");
                showAlert("Erreur", "Le fichier FXML /AffichageListSeancesFront.fxml n'a pas été trouvé.");
                return;
            }
            Parent root = loader.load();
            AffichageListSeancesFront controller = loader.getController();
            controller.initializeWithCour(currentCour); // Passer le cours actuel
            Stage stage = (Stage) courSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la navigation vers AffichageListSeancesFront.fxml", e);
            showAlert("Erreur", "Erreur lors de la navigation vers AffichageListSeancesFront.fxml : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}