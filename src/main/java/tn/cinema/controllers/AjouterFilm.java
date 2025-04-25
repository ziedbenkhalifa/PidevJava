package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import tn.cinema.entities.Films;
import tn.cinema.services.FilmsService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AjouterFilm {

    @FXML
    private DatePicker dateFilm;

    @FXML
    private ComboBox<String> genreCombo;

    @FXML
    private Button chooseImgButton;

    @FXML
    private Label imgLabel;

    private File selectedImageFile;

    @FXML
    private TextField nomFilm;

    @FXML
    private TextField realisateur;

    private FilmsService fs = new FilmsService();

    private Films filmToEdit;

    @FXML
    void initialize() {
        // Populate ComboBox with genre options
        genreCombo.setItems(FXCollections.observableArrayList(
                "Action", "Horror", "Comidie", "Science Fiction", "Drama", "Romance"
        ));
        // Set default prompt text style
        genreCombo.setPromptText("Sélectionner un genre");
    }

    @FXML
    void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(chooseImgButton.getScene().getWindow());

        if (file != null) {
            selectedImageFile = file;
            imgLabel.setText(file.getName());
        }
    }

    @FXML
    void ajout(ActionEvent event) {
        if (dateFilm.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Veuillez sélectionner une date pour le film.");
            alert.showAndWait();
            return;
        }

        if (nomFilm.getText().isEmpty() || realisateur.getText().isEmpty() ||
                genreCombo.getValue() == null || dateFilm.getValue() == null ||
                selectedImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs et sélectionner une image.");
            alert.showAndWait();
            return;
        }

        try {
            // Add the film to the database
            fs.ajouter(new Films(
                    nomFilm.getText(),
                    realisateur.getText(),
                    genreCombo.getValue(),
                    selectedImageFile.getAbsolutePath(),
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
                genreCombo.getValue() == null || dateFilm.getValue() == null ||
                selectedImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs et sélectionner une image.");
            alert.showAndWait();
            return;
        }

        try {
            filmToEdit.setNom_film(nomFilm.getText());
            filmToEdit.setRealisateur(realisateur.getText());
            filmToEdit.setGenre(genreCombo.getValue());
            filmToEdit.setImg(selectedImageFile.getAbsolutePath());
            filmToEdit.setDate_production(dateFilm.getValue());

            fs.modifier(filmToEdit); // Update the film in the database

            // Load the AfficherFilm view
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

    @FXML
    void cancel(ActionEvent event) {
        try {
            // Load the AfficherFilm view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFilm.fxml"));
            Parent root = loader.load();

            // Get the current scene and set the new root
            nomFilm.getScene().setRoot(root);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible de revenir à la liste des films : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void edit(Films film) {
        this.filmToEdit = film;

        // Pre-fill the fields with the selected film's values
        nomFilm.setText(film.getNom_film());
        realisateur.setText(film.getRealisateur());
        genreCombo.setValue(film.getGenre());
        imgLabel.setText(film.getImg() != null ? new File(film.getImg()).getName() : "Aucune image");
        selectedImageFile = film.getImg() != null ? new File(film.getImg()) : null;
        dateFilm.setValue(film.getDate_production());
    }
}