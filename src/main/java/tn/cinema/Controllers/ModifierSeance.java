package tn.cinema.Controllers;

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
import java.time.LocalDate;
import java.time.LocalTime;
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
        // Populate the ComboBox with Cour objects
        try {
            List<Cour> cours = courService.recuperer();
            courIdComboBox.getItems().addAll(cours);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement des cours : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setSeanceToModify(Seance seance) {
        this.seanceToModify = seance;
        dateSeancePicker.setValue(seance.getDateSeance());
        dureeField.setText(seance.getDuree().toString());
        objectifsField.setText(seance.getObjectifs());
        courIdComboBox.getSelectionModel().select(seance.getCour());
    }

    @FXML
    private void modifierSeance() {
        try {
            seanceToModify.setDateSeance(dateSeancePicker.getValue());
            seanceToModify.setDuree(LocalTime.parse(dureeField.getText()));
            seanceToModify.setObjectifs(objectifsField.getText());
            seanceToModify.setCour(courIdComboBox.getSelectionModel().getSelectedItem());
            seanceService.modifier(seanceToModify);

            // Navigate back to AfficherSeance
            Parent root = FXMLLoader.load(getClass().getResource("/AffichageListSeances.fxml"));
            Stage stage = (Stage) dateSeancePicker.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la modification : " + e.getMessage());
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors du retour : " + e.getMessage());
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de la navigation : " + e.getMessage());
            alert.showAndWait();
        }
    }
}