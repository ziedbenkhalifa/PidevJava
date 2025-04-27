package tn.cinema.controllers;

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

public class AjouterCour extends Dashboard {

    @FXML
    private ComboBox<String> typeCourComboBox;

    @FXML
    private TextField coutField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private TextField dateDebutTimeField; // Champ pour l'heure de début

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField dateFinTimeField; // Champ pour l'heure de fin

    private CourService courService = new CourService();

    @FXML
    private void ajoutercour() throws SQLException {
        try {
            // Validation du type de cours
            String typeCour = typeCourComboBox.getValue();
            if (typeCour == null || typeCour.trim().isEmpty()) {
                throw new IllegalArgumentException("Type de Cour doit être sélectionné.");
            }

            // Validation du coût (doit être entre 50 et 999, et explicitement pas 0)
            double cout;
            try {
                cout = Double.parseDouble(coutField.getText());
                if (cout == 0) {
                    throw new IllegalArgumentException("Le Coût ne peut pas être égal à 0.");
                }
                if (cout < 50 || cout > 999) {
                    throw new IllegalArgumentException("Le Coût doit être compris entre 50 et 999.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Le Coût doit être un nombre valide.");
            }

            // Validation de la date de début
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

            // Vérification si la date de début est aujourd'hui ou dans le futur
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = LocalDate.now();
            if (dateDebut.isBefore(today)) {
                throw new IllegalArgumentException("La Date de Début doit être aujourd'hui ou dans le futur.");
            } else if (dateDebut.isEqual(today) && dateDebutTimeFinal.isBefore(now)) {
                throw new IllegalArgumentException("L'heure de début pour aujourd'hui doit être postérieure à l'heure actuelle (" + now.format(DateTimeFormatter.ofPattern("HH:mm")) + ").");
            }

            // Validation de la date de fin
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

            // Vérification si la date de fin est aujourd'hui ou dans le futur
            if (dateFin.isBefore(today)) {
                throw new IllegalArgumentException("La Date de Fin doit être aujourd'hui ou dans le futur.");
            } else if (dateFin.isEqual(today) && dateFinTimeFinal.isBefore(now)) {
                throw new IllegalArgumentException("L'heure de fin pour aujourd'hui doit être postérieure à l'heure actuelle (" + now.format(DateTimeFormatter.ofPattern("HH:mm")) + ").");
            }

            // Vérification que la date de fin est postérieure à la date de début
            if (dateFinTimeFinal.isBefore(dateDebutTimeFinal) || dateFinTimeFinal.isEqual(dateDebutTimeFinal)) {
                throw new IllegalArgumentException("La Date de Fin doit être strictement postérieure à la Date de Début.");
            }

            // Création et ajout du cours
            Cour cour = new Cour(typeCour, cout, dateDebutTimeFinal, dateFinTimeFinal);
            courService.ajouter(cour);
            System.out.println("Cours ajouté: " + cour);

            // Chargement de la page suivante
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