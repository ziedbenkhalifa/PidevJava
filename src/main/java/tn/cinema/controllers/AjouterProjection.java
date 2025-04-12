package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tn.cinema.entities.Projection;
import tn.cinema.services.ProjectionService;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterProjection {

    @FXML
    private TextField capaciter;

    @FXML
    private DatePicker dateProjection;

    @FXML
    private TextField prix;

    private ProjectionService ps = new ProjectionService();
    private Projection projectionToEdit;

    // Method for adding a projection
    @FXML
    void ajouterProjection(ActionEvent event) {
        // Check if the form is valid
        if (dateProjection.getValue() == null || capaciter.getText().isEmpty() || prix.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("All fields must be filled.");
            alert.showAndWait();
            return;
        }

        try {
            // Add the projection to the database
            ps.ajouter(new Projection(
                    Integer.parseInt(capaciter.getText()),  // Parsing as int
                    dateProjection.getValue(),
                    Float.parseFloat(prix.getText())  // Parsing as float
            ));

            // After successful insertion, navigate to another view (e.g., projection list)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProjection.fxml"));
            Parent root = loader.load();
            capaciter.getScene().setRoot(root);

        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Error adding projection: " + e.getMessage());
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setContentText("Invalid number format. Please check the fields.");
            alert.showAndWait();
        }
    }

    // Method for editing an existing projection
    public void edit(Projection projection) {
        this.projectionToEdit = projection;

        // Pre-fill fields with the selected projection data
        dateProjection.setValue(projection.getDate_projection());
        capaciter.setText(String.valueOf(projection.getCapaciter()));
        prix.setText(String.valueOf(projection.getPrix()));
    }

    @FXML
    void modifierProjection(ActionEvent event) {
        if (projectionToEdit == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("No projection selected for modification.");
            alert.showAndWait();
            return;
        }

        // Validate form fields
        if (dateProjection.getValue() == null || capaciter.getText().isEmpty() || prix.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Fields");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        try {
            // Check if capaciter and prix are valid numbers
            int capacite = 0;
            float prixValue = 0.0f;

            try {
                capacite = Integer.parseInt(capaciter.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setContentText("Invalid capaciter value. Please enter a valid number.");
                alert.showAndWait();
                return;
            }

            try {
                prixValue = Float.parseFloat(prix.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setContentText("Invalid prix value. Please enter a valid number.");
                alert.showAndWait();
                return;
            }

            // Update the projection details
            projectionToEdit.setDate_projection(dateProjection.getValue());
            projectionToEdit.setCapaciter(capacite);
            projectionToEdit.setPrix(prixValue);

            // Update the projection in the database
            ps.modifier(projectionToEdit);

            // After successful update, navigate to another view (e.g., projection list)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProjection.fxml"));
            Parent root = loader.load();
            capaciter.getScene().setRoot(root);

        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Error updating projection: " + e.getMessage());
            alert.showAndWait();
        }
    }

}
