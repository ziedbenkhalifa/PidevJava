package tn.cinema.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tn.cinema.entities.Cour;
import tn.cinema.services.CourService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AjouterCour {

    @FXML
    private ComboBox<String> typeCourComboBox;

    @FXML
    private TextField coutField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private TextField dateDebutTimeField; // New field for time input

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField dateFinTimeField; // New field for time input

    CourService courService = new CourService();

    @FXML
    private void ajoutercour() throws SQLException {
        try {
            // Récupération des données du formulaire
            String typeCour = typeCourComboBox.getValue();
            if (typeCour == null || typeCour.trim().isEmpty()) {
                throw new IllegalArgumentException("Type de Cour must be selected.");
            }

            double cout;
            try {
                cout = Double.parseDouble(coutField.getText());
                if (cout < 0) {
                    throw new IllegalArgumentException("Coût must be a positive number.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Coût must be a valid number.");
            }

            // Validate and parse DateDebut
            LocalDate dateDebut = dateDebutPicker.getValue();
            if (dateDebut == null) {
                throw new IllegalArgumentException("Date de Début must be selected.");
            }
            String dateDebutTimeStr = dateDebutTimeField.getText();
            if (dateDebutTimeStr == null || dateDebutTimeStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Time for Date de Début must be specified (e.g., 14:30).");
            }
            LocalTime dateDebutTime;
            try {
                dateDebutTime = LocalTime.parse(dateDebutTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Time for Date de Début must be in the format 'HH:mm' (e.g., 14:30).");
            }
            LocalDateTime dateDebutTimeFinal = LocalDateTime.of(dateDebut, dateDebutTime);

            // Validate and parse DateFin
            LocalDate dateFin = dateFinPicker.getValue();
            if (dateFin == null) {
                throw new IllegalArgumentException("Date de Fin must be selected.");
            }
            String dateFinTimeStr = dateFinTimeField.getText();
            if (dateFinTimeStr == null || dateFinTimeStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Time for Date de Fin must be specified (e.g., 16:30).");
            }
            LocalTime dateFinTime;
            try {
                dateFinTime = LocalTime.parse(dateFinTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Time for Date de Fin must be in the format 'HH:mm' (e.g., 16:30).");
            }
            LocalDateTime dateFinTimeFinal = LocalDateTime.of(dateFin, dateFinTime);

            // Validate that DateFin is after DateDebut
            if (dateFinTimeFinal.isBefore(dateDebutTimeFinal)) {
                throw new IllegalArgumentException("Date de Fin must be after Date de Début.");
            }

            // Create Cour object with the updated LocalDateTime values
            Cour cour = new Cour(typeCour, cout, dateDebutTimeFinal, dateFinTimeFinal);
            courService.ajouter(cour);
            System.out.println("Cours ajouté: " + cour);

            // Charger la vue AfficherCour.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCour.fxml"));
            Parent root = loader.load();

            // Passer les données à AfficherCour
            AfficherCour ac = loader.getController();
            ac.setRtype(typeCour);
            ac.setRcout(cout);
            ac.setRdatedebut(dateDebut);
            ac.setRdatefin(dateFin);

            // Charger la liste mise à jour
            List<Cour> coursList = courService.recuperer();
            ac.setRlistItems(coursList);

            // Remplacer la scène actuelle par AfficherCour
            Stage stage = (Stage) coutField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IllegalArgumentException e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de la vue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichageListCours.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coutField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGestionCour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCour.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}