package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

public class AjouterCour extends Dashboard{

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

            String typeCour = typeCourComboBox.getValue();
            if (typeCour == null || typeCour.trim().isEmpty()) {
                throw new IllegalArgumentException("Type de Cour doit être sélectionné.");
            }

            double cout;
            try {
                cout = Double.parseDouble(coutField.getText());
                if (cout < 0) {
                    throw new IllegalArgumentException("Le Coût doit être un nombre positif.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Le Coût doit être un nombre valide.");
            }


            LocalDate dateDebut = dateDebutPicker.getValue();
            if (dateDebut == null) {
                throw new IllegalArgumentException("La Date de Début doit être sélectionnée.");
            }
            String dateDebutTimeStr = dateDebutTimeField.getText();
            if (dateDebutTimeStr == null || dateDebutTimeStr.trim().isEmpty()) {
                throw new IllegalArgumentException("L'heure pour la Date de Début doit être spécifiée (ex. : 14:30).");
            }
            LocalTime dateDebutTime;
            try {
                dateDebutTime = LocalTime.parse(dateDebutTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("L'heure pour la Date de Début doit être au format 'HH:mm' (ex. : 14:30).");
            }
            LocalDateTime dateDebutTimeFinal = LocalDateTime.of(dateDebut, dateDebutTime);


            if (dateDebutTimeFinal.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La Date de Début doit être aujourd'hui ou dans le futur.");
            }

            LocalDate dateFin = dateFinPicker.getValue();
            if (dateFin == null) {
                throw new IllegalArgumentException("La Date de Fin doit être sélectionnée.");
            }
            String dateFinTimeStr = dateFinTimeField.getText();
            if (dateFinTimeStr == null || dateFinTimeStr.trim().isEmpty()) {
                throw new IllegalArgumentException("L'heure pour la Date de Fin doit être spécifiée (ex. : 16:30).");
            }
            LocalTime dateFinTime;
            try {
                dateFinTime = LocalTime.parse(dateFinTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("L'heure pour la Date de Fin doit être au format 'HH:mm' (ex. : 16:30).");
            }
            LocalDateTime dateFinTimeFinal = LocalDateTime.of(dateFin, dateFinTime);


            if (dateFinTimeFinal.isBefore(dateDebutTimeFinal)) {
                throw new IllegalArgumentException("La Date de Fin doit être postérieure à la Date de Début.");
            }

            Cour cour = new Cour(typeCour, cout, dateDebutTimeFinal, dateFinTimeFinal);
            courService.ajouter(cour);
            System.out.println("Cours ajouté: " + cour);


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCour.fxml"));
            Parent root = loader.load();

            AfficherCour ac = loader.getController();
            ac.setRtype(typeCour);
            ac.setRcout(cout);
            ac.setRdatedebut(dateDebut);
            ac.setRdatefin(dateFin);


            List<Cour> coursList = courService.recuperer();
            ac.setRlistItems(coursList);

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