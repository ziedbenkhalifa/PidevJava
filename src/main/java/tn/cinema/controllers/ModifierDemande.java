package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierDemande implements Initializable {
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
    private Demande demande;
    private InterfaceDemandes parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Restrict nombreJoursField to digits only
        nombreJoursField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nombreJoursField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Initialize ComboBox options
        typeComboBox.getItems().addAll("Congé", "Absence", "Autre");
        statutComboBox.getItems().addAll("Approuvée", "En_attente", "Rejetée");
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
        // Pre-fill the form with the Demande's data
        nombreJoursField.setText(String.valueOf(demande.getNombreJours()));
        descriptionField.setText(demande.getDescription());
        typeComboBox.setValue(demande.getType());
        lienSupplementaireField.setText(demande.getLienSupplementaire());
        statutComboBox.setValue(transformStatutForUI(demande.getStatut()));
    }

    public void setParentController(InterfaceDemandes parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void onSubmitClick() {
        try {
            // Validate required fields
            if (nombreJoursField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
                    typeComboBox.getValue() == null || statutComboBox.getValue() == null ||
                    lienSupplementaireField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
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

            // Validate the lienSupplementaire is a valid URL
            String lien = lienSupplementaireField.getText();
            if (!isValidURL(lien)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Le lien fourni n'est pas une URL valide (ex: https://exemple.com).");
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

            // Update the Demande object
            demande.setNombreJours(nombreJours);
            demande.setDescription(descriptionField.getText());
            demande.setType(typeComboBox.getValue());
            demande.setLienSupplementaire(lien);
            demande.setStatut(statut);

            // Call DemandeService to update the Demande
            demandeService.modifierAdmin(demande.getId(), demande);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Demande modifiée avec succès !");
            alert.showAndWait();

            // Refresh the ListView in InterfaceDemandes
            parentController.refreshList();

            // Close the window
            Stage stage = (Stage) nombreJoursField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification de la demande : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean isValidURL(String url) {
        try {
            URL u = new URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (MalformedURLException e) {
            return false;
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

    private String transformStatutForUI(String statut) {
        switch (statut) {
            case "approuvee":
                return "Approuvée";
            case "en_attente":
                return "En_attente";
            case "rejete":
                return "Rejetée";
            default:
                return "En_attente"; // Default value if statut is invalid
        }
    }
}
