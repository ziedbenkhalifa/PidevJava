package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class ModifierCour {

    @FXML
    private ComboBox<String> typeCourComboBox;
    @FXML
    private TextField coutField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private TextField dateDebutTimeField;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField dateFinTimeField;

    private Cour cour;
    private final CourService courService = new CourService();


    public void setCour(Cour cour) {
        this.cour = cour;

        typeCourComboBox.setValue(cour.getTypeCour());
        coutField.setText(String.valueOf(cour.getCout()));
        dateDebutPicker.setValue(cour.getDateDebut().toLocalDate());
        dateDebutTimeField.setText(cour.getDateDebut().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        dateFinPicker.setValue(cour.getDateFin().toLocalDate());
        dateFinTimeField.setText(cour.getDateFin().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    @FXML
    private void modifierCour() {
        try {

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


            if (dateFinTimeFinal.isBefore(dateDebutTimeFinal)) {
                throw new IllegalArgumentException("Date de Fin must be after Date de Début.");
            }


            cour.setTypeCour(typeCour);
            cour.setCout(cout);
            cour.setDateDebut(dateDebutTimeFinal);
            cour.setDateFin(dateFinTimeFinal);


            courService.modifier(cour);
            System.out.println("Cours modifié: " + cour);


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichageListCours.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coutField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de la modification : " + e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors du chargement de la vue : " + e.getMessage());
            alert.showAndWait();
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
            Stage stage = (Stage) typeCourComboBox.getScene().getWindow();
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