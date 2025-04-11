package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tn.cinema.entities.Films;
import tn.cinema.services.FilmsService;


import java.sql.SQLException;

public class AjouterFilm {


    @FXML
    private DatePicker dateFilm;

    @FXML
    private TextField genre;

    @FXML
    private TextField img;

    @FXML
    private TextField nomFilm;

    @FXML
    private TextField realisateur;

    private FilmsService fs = new FilmsService();


    @FXML
    void ajout(ActionEvent event) {
        // Ensure the DatePicker value is not null
        if (dateFilm.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Please select a date for the film.");
            alert.showAndWait();
            return; // Exit the method early
        }

        // Check if other fields are empty
        if (nomFilm.getText().isEmpty() || realisateur.getText().isEmpty() || genre.getText().isEmpty() || img.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("All fields must be filled.");
            alert.showAndWait();
            return;
        }

        try {
            // Convert LocalDate to java.sql.Date before saving it to the database
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateFilm.getValue());

            // Add the film to the database
            fs.ajouter(new Films(
                    nomFilm.getText(),
                    realisateur.getText(),
                    genre.getText(),
                    img.getText(),
                    dateFilm.getValue() // Passing LocalDate directly
            ));
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }


}
