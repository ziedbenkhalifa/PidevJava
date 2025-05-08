package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;
import tn.cinema.entities.Seance;
import tn.cinema.services.CourService;
import tn.cinema.services.SeanceService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierSeance implements Initializable {

    @FXML
    private DatePicker dateSeancePicker;

    @FXML
    private TextField dureeField;

    @FXML
    private TextField objectifsField;

    @FXML
    private ComboBox<Cour> courIdComboBox;

    private final SeanceService seanceService = new SeanceService();
    private final CourService courService = new CourService();
    private Seance seanceToModify;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Cour> cours = courService.recuperer();
            if (cours.isEmpty()) {
                showAlert("Avertissement", "Aucun cours disponible. Veuillez ajouter un cours avant de modifier une séance.");
            }
            courIdComboBox.getItems().addAll(cours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des cours : " + e.getMessage());
        }
    }

    public void setSeanceToModify(Seance seance) {
        this.seanceToModify = seance;
        if (seance == null) {
            showAlert("Erreur", "Aucune séance à modifier.");
            return;
        }

        // Vérification de l'ID
        System.out.println("ID de la séance à modifier : " + seance.getId());
        dateSeancePicker.setValue(seance.getDateSeance());
        dureeField.setText(seance.getDuree().toString());
        objectifsField.setText(seance.getObjectifs());
        courIdComboBox.getSelectionModel().select(seance.getCour());
    }

    @FXML
    private void modifierSeance() {
        try {
            // Validation des champs
            if (dateSeancePicker.getValue() == null) {
                showAlert("Erreur", "Veuillez sélectionner une date pour la séance.");
                return;
            }

            LocalTime duree;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                duree = LocalTime.parse(dureeField.getText(), formatter);
            } catch (DateTimeParseException e) {
                showAlert("Erreur", "Veuillez entrer une durée valide au format HH:mm (ex. 01:30).");
                return;
            }

            if (objectifsField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez entrer les objectifs de la séance.");
                return;
            }

            if (courIdComboBox.getSelectionModel().getSelectedItem() == null) {
                showAlert("Erreur", "Veuillez sélectionner un cours.");
                return;
            }

            // Mise à jour de l'objet Seance
            seanceToModify.setDateSeance(dateSeancePicker.getValue());
            seanceToModify.setDuree(duree);
            seanceToModify.setObjectifs(objectifsField.getText());
            seanceToModify.setCour(courIdComboBox.getSelectionModel().getSelectedItem());

            // Appel au service pour modifier dans la base de données
            seanceService.modifier(seanceToModify);

            // Redirection vers la liste des séances
            Parent root = FXMLLoader.load(getClass().getResource("/AffichageListSeances.fxml"));
            Stage stage = (Stage) dateSeancePicker.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException | IOException e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AffichageListSeances.fxml"));
            Stage stage = (Stage) dateSeancePicker.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur s'est produite lors du retour : " + e.getMessage());
        }
    }

    @FXML
    private void handleGererCour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCour.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) dateSeancePicker.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur s'est produite lors de la navigation : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}