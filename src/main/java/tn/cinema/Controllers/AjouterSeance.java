package tn.cinema.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import tn.cinema.entities.Cour;
import tn.cinema.entities.Seance;
import tn.cinema.services.CourService;
import tn.cinema.services.SeanceService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterSeance implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Fetch the list of Cours from the database
            List<Cour> coursList = courService.recuperer();
            courIdComboBox.setItems(FXCollections.observableArrayList(coursList));

            // Customize the ComboBox display to show the nom and id of each Cour
            courIdComboBox.setConverter(new StringConverter<Cour>() {
                @Override
                public String toString(Cour cour) {
                    return cour != null ? cour.getTypeCour() + " (ID: " + cour.getId() + ")" : "";
                }

                @Override
                public Cour fromString(String string) {
                    return null; // Not needed for this use case
                }
            });
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement des cours : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void ajouterSeance() {
        try {
            // Validate Date Séance
            LocalDate dateSeance = dateSeancePicker.getValue();
            if (dateSeance == null) {
                throw new IllegalArgumentException("Date Séance must be selected.");
            }

            // Validate Durée
            String dureeStr = dureeField.getText();
            if (dureeStr == null || dureeStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Durée must be specified (e.g., 01:30).");
            }
            LocalTime duree;
            try {
                duree = LocalTime.parse(dureeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Durée must be in the format 'HH:mm' (e.g., 01:30).");
            }

            // Validate Objectifs
            String objectifs = objectifsField.getText();
            if (objectifs == null || objectifs.trim().isEmpty()) {
                throw new IllegalArgumentException("Objectifs must be specified.");
            }

            // Validate Cour selection
            Cour selectedCour = courIdComboBox.getValue();
            if (selectedCour == null) {
                throw new IllegalArgumentException("Cours must be selected.");
            }

            // Create Seance object
            Seance seance = new Seance(dateSeance, duree, objectifs, selectedCour);
            seanceService.ajouter(seance);

            // Navigate to AfficherSeance
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherSeance.fxml"));
            Parent root = loader.load();

            AfficherSeance controller = loader.getController();
            controller.setRdateSeance(dateSeance);
            controller.setRduree(duree);
            controller.setRobjectifs(objectifs);
            controller.setRcourId(selectedCour.getId());

            List<Seance> seancesList = seanceService.recuperer();
            controller.setRlistItems(seancesList);

            Stage stage = (Stage) dateSeancePicker.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void annulerAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichageListSeances.fxml"));
            Parent root = loader.load();
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
    private void handleGestionCour(ActionEvent event) {
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