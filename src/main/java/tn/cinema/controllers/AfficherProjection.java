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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Films;
import tn.cinema.entities.Projection;
import tn.cinema.services.FilmsService;
import tn.cinema.services.ProjectionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherProjection {

    @FXML
    private ListView<Projection> listProjection;

    private ProjectionService ps = new ProjectionService();

    // Load projections when the view is initialized
    @FXML
    void initialize() {
        try {
            List<Projection> projections = ps.recuperer();  // Get all projections
            ObservableList<Projection> observableList = FXCollections.observableList(projections);
            listProjection.setItems(observableList);

            // Set custom cell factory
            listProjection.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Projection item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        // HBox container
                        HBox hbox = new HBox(10);
                        hbox.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

                        // VBox for text content
                        VBox vbox = new VBox(5);
                        vbox.setStyle("-fx-padding: 5;");

                        // Displaying date, capaciter, and prix
                        Text date = new Text("Date: " + item.getDate_projection());
                        date.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                        Text capacite = new Text("Capacité: " + item.getCapaciter());
                        capacite.setStyle("-fx-font-size: 13px;");

                        Text prix = new Text("Prix: " + item.getPrix() + " DT");
                        prix.setStyle("-fx-font-size: 13px;");

                        vbox.getChildren().addAll(date, capacite, prix);
                        hbox.getChildren().add(vbox);

                        setGraphic(hbox);  // Set the graphic for each item in the list
                    } else {
                        setGraphic(null);  // Set null when the item is empty
                    }
                }
            });

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur de récupération des projections : " + e.getMessage());
            alert.showAndWait();
        }
    }


    // Navigate to the 'AjouterProjection' form
    @FXML
    void ajout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProjection.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new root to the current stage
            currentStage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible d'ouvrir la page d'ajout de projection.");
            alert.showAndWait();
        }
    }

    // Delete a selected projection
    @FXML
    void delete(ActionEvent event) {
        Projection selectedProjection = listProjection.getSelectionModel().getSelectedItem();

        if (selectedProjection == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune projection sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une projection à supprimer.");
            alert.showAndWait();
            return;
        }

        try {
            ps.supprimer(selectedProjection.getId()); // Delete projection by ID

            // Refresh the list
            List<Projection> projections = ps.recuperer();
            ObservableList<Projection> observableList = FXCollections.observableList(projections);
            listProjection.setItems(observableList);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Projection supprimée avec succès !");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Navigate to the form to edit a selected projection
    @FXML
    void edit(ActionEvent event) {
        Projection selectedProjection = listProjection.getSelectionModel().getSelectedItem();

        if (selectedProjection == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune projection sélectionnée");
            alert.setContentText("Veuillez sélectionner une projection à modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProjection.fxml"));
            Parent root = loader.load();

            // Pass projection to the form controller
            AjouterProjection controller = loader.getController();
            controller.edit(selectedProjection);

            listProjection.getScene().setRoot(root); // Navigate to form
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigate to another management screen (optional)

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