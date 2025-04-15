package tn.cinema.controllers;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Films;
import tn.cinema.services.FilmsService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherFilm {


    @FXML
    private ListView<Films> listFilm;

    @FXML
    void ajout(ActionEvent event) {
        try {
            // Load the AjouterFilm.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFilm.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event (the current window)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new root to the current stage (i.e., replace the current content with the Add Film view)
            currentStage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible d'ouvrir la page d'ajout de film.");
            alert.showAndWait();
        }
    }




    @FXML
    void delete(ActionEvent event) {
        Films selectedFilm = listFilm.getSelectionModel().getSelectedItem();

        if (selectedFilm == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun film sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un film à supprimer.");
            alert.showAndWait();
            return;
        }

        try {
            fs.supprimer(selectedFilm.getId()); // delete by ID

            // Refresh the list
            List<Films> films = fs.recuperer();
            ObservableList<Films> observableList = FXCollections.observableList(films);
            listFilm.setItems(observableList);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Film supprimé avec succès !");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    void edit(ActionEvent event) {
        Films selectedFilm = listFilm.getSelectionModel().getSelectedItem();

        if (selectedFilm == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun film sélectionné");
            alert.setContentText("Veuillez sélectionner un film à modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFilm.fxml"));
            Parent root = loader.load();

            // Pass film to controller
            AjouterFilm controller = loader.getController();
            controller.edit(selectedFilm);

            listFilm.getScene().setRoot(root); // Navigate to form
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private FilmsService fs = new FilmsService();

    @FXML
    void initialize() {
        try {
            List<Films> films = fs.recuperer();
            ObservableList<Films> observableList = FXCollections.observableList(films);
            listFilm.setItems(observableList);

            listFilm.setCellFactory(param -> new javafx.scene.control.ListCell<Films>() {
                @Override
                protected void updateItem(Films item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        // HBox container
                        HBox hbox = new HBox(10);
                        hbox.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

                        // ImageView
                        ImageView imageView = new ImageView();
                        try {
                            Image image = new Image("file:" + item.getImg());
                            imageView.setImage(image);
                        } catch (Exception e) {
                            imageView.setImage(new Image("file:src/images/placeholder.jpg"));
                        }
                        imageView.setFitHeight(100);
                        imageView.setFitWidth(80);

                        // Text content
                        VBox vbox = new VBox(5);
                        vbox.setStyle("-fx-padding: 5;");

                        Text title = new Text(item.getNom_film());
                        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                        Text director = new Text("Réalisateur: " + item.getRealisateur());
                        director.setStyle("-fx-font-size: 13px;");

                        Text genre = new Text("Genre: " + item.getGenre());
                        genre.setStyle("-fx-font-size: 13px;");

                        vbox.getChildren().addAll(title, director, genre);

                        hbox.getChildren().addAll(imageView, vbox);
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    void gestionFilm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionFilms.fxml")); // Adjust path if needed
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();   // Get current window
            Scene scene = new Scene(root);
            stage.setScene(scene); // Set the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debug
        }
    }
}
