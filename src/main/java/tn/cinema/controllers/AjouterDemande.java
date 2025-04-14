package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;
import tn.cinema.services.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class AjouterDemande implements Initializable {
    @FXML
    private TextField userIdField;

    @FXML
    private TextField nombreJoursField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField lienSupplementaireField;

    @FXML
    private ComboBox<String> statutComboBox;

    private DemandeService demandeService = new DemandeService();
    private User userService = new User();

    private InterfaceDemandes parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Restrict userIdField to digits only
        userIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                userIdField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Restrict nombreJoursField to digits only
        nombreJoursField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nombreJoursField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Initialize ComboBox options
       // typeComboBox.getItems().addAll("Congé", "Absence", "Autre");
        statutComboBox.getItems().addAll("Approuvée", "En_attente", "Rejetée");

        // Set default values for ComboBox
        //typeComboBox.setValue("Congé"); // Default type
        statutComboBox.setValue("En_attente"); // Default status
    }

    public void setParentController(InterfaceDemandes parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void onSubmitClick() {
        try {
            // Validate required fields
            if (userIdField.getText().isEmpty() || nombreJoursField.getText().isEmpty() ||
                    descriptionField.getText().isEmpty() || typeComboBox.getValue() == null ||
                    statutComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
                alert.showAndWait();
                return;
            }

            // Validate userId exists
            int userId;
            try {
                userId = Integer.parseInt(userIdField.getText());
                if (userId <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "L'ID de l'utilisateur doit être un entier positif.");
                    alert.showAndWait();
                    return;
                }
                if (!userService.userExists(userId)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "L'ID de l'utilisateur " + userId + " n'existe pas dans la base de données.");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "L'ID de l'utilisateur doit être un entier valide.");
                alert.showAndWait();
                return;
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la vérification de l'ID utilisateur : " + e.getMessage());
                alert.showAndWait();
                return;
            }

            // Validate nombreJours is a positive integer
            int nombreJours;
            try {
                nombreJours = Integer.parseInt(nombreJoursField.getText());
                if (nombreJours <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Le nombre de jours doit être un entier positif.");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Le nombre de jours doit être un entier valide.");
                alert.showAndWait();
                return;
            }

            // Transform statut value for database
            String statut = transformStatutForDatabase(statutComboBox.getValue());
            if (statut == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Statut invalide sélectionné.");
                alert.showAndWait();
                return;
            }

            // Create a new Demande object
            Demande demande = new Demande();
            demande.setUserId(userId);
            demande.setNombreJours(nombreJours);
            demande.setDescription(descriptionField.getText());
            demande.setType(typeComboBox.getValue());
            demande.setLienSupplementaire(lienSupplementaireField.getText().isEmpty() ? null : lienSupplementaireField.getText());
            demande.setStatut(statut);
            demande.setAdminId(null); // Admin ID is nullable, set later if needed
            demande.setDateSoumission(new Date()); // Set current date

            // Save the Demande using DemandeService
            demandeService.ajouterDemande(demande);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Demande ajoutée avec succès !");
            alert.showAndWait();

            // Refresh the ListView in InterfaceDemandes
            if (parentController != null) {
                parentController.refreshList(); // Changed from refreshTable()
            } else {
                System.out.println("Erreur : parentController est null.");
            }

            // Close the window
            Stage stage = (Stage) userIdField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout de la demande : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private String transformStatutForDatabase(String statut) {
        switch (statut) {
            case "Approuvée":
                return "approuvee";
            case "En_attente":
                return "en_attente";
            case "Rejetée":
                return "rejete";
            default:
                return null;
        }
    }
}