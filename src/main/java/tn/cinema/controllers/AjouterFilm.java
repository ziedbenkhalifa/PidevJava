package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tn.cinema.entities.Films;
import tn.cinema.services.FilmsService;


import java.io.IOException;
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
        if (dateFilm.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Please select a date for the film.");
            alert.showAndWait();
            return;
        }

        if (nomFilm.getText().isEmpty() || realisateur.getText().isEmpty() || genre.getText().isEmpty() || img.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("All fields must be filled.");
            alert.showAndWait();
            return;
        }

        try {
            // Add the film to the database
            fs.ajouter(new Films(
                    nomFilm.getText(),
                    realisateur.getText(),
                    genre.getText(),
                    img.getText(),
                    dateFilm.getValue()
            ));

            // Load the AfficherFilm view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFilm.fxml"));
            Parent root = loader.load();

            // Get the current scene and set the new root
            nomFilm.getScene().setRoot(root);

        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }

    private Films filmToEdit;

    public void edit(Films film) {
        this.filmToEdit = film;

        // Pré-remplir les champs avec les valeurs du film sélectionné
        nomFilm.setText(film.getNom_film());
        realisateur.setText(film.getRealisateur());
        genre.setText(film.getGenre());
        img.setText(film.getImg());
        dateFilm.setValue(film.getDate_production());
    }

    @FXML
    void edit(ActionEvent event) {
        if (filmToEdit == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Aucun film sélectionné pour modification.");
            alert.showAndWait();
            return;
        }

        if (nomFilm.getText().isEmpty() || realisateur.getText().isEmpty() ||
                genre.getText().isEmpty() || img.getText().isEmpty() || dateFilm.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        try {
            filmToEdit.setNom_film(nomFilm.getText());
            filmToEdit.setRealisateur(realisateur.getText());
            filmToEdit.setGenre(genre.getText());
            filmToEdit.setImg(img.getText());
            filmToEdit.setDate_production(dateFilm.getValue());

            fs.modifier(filmToEdit); // Appelle la méthode de mise à jour

            // Recharge AfficherFilm.fxml après modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFilm.fxml"));
            Parent root = loader.load();
            nomFilm.getScene().setRoot(root);

        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la modification : " + e.getMessage());
            alert.showAndWait();
        }
    }



}
