package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AjouterDemandeClientController implements Initializable {
    @FXML
    private TextField nombreJoursField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    private TextField lienSuppField;

    private DemandeService demandeService = new DemandeService();
    private DemandeClient parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the ChoiceBox with predefined options
        typeChoiceBox.setItems(FXCollections.observableArrayList("footerWeb", "backdrop", "integrefilm"));
        typeChoiceBox.setValue("footerWeb"); // Set default value
    }

    public void setParentController(DemandeClient parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void ajouterDemande() {
        // Validate input fields
        String nombreJoursText = nombreJoursField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = typeChoiceBox.getValue();
        String lienSupp = lienSuppField.getText().trim();

        // Check if any field is empty
        if (nombreJoursText.isEmpty()) {
            showErrorAlert("Le champ 'Nombre de Jours' est requis.");
            return;
        }
        if (description.isEmpty()) {
            showErrorAlert("Le champ 'Description' est requis.");
            return;
        }
        if (type == null) {
            showErrorAlert("Le champ 'Type' est requis. Veuillez sélectionner une option.");
            return;
        }
        if (lienSupp.isEmpty()) {
            showErrorAlert("Le champ 'Lien Supplémentaire' est requis.");
            return;
        }

        // Validate nombreJours as a positive integer
        int nombreJours;
        try {
            nombreJours = Integer.parseInt(nombreJoursText);
            if (nombreJours <= 0) {
                showErrorAlert("Le 'Nombre de Jours' doit être un entier positif supérieur à 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Le 'Nombre de Jours' doit être un entier valide.");
            return;
        }

        // Validate lienSupplementaire as a valid URL
        if (!isValidURL(lienSupp)) {
            showErrorAlert("Le 'Lien Supplémentaire' doit être une URL valide (ex: https://example.com).");
            return;
        }

        // If all validations pass, proceed to add the demand
        try {
            Demande demande = new Demande();
            demande.setNombreJours(nombreJours);
            demande.setDescription(description);
            demande.setType(type);
            demande.setLienSupplementaire(lienSupp);

            demandeService.ajoutDemande(demande);
            parentController.refreshList();

            Stage stage = (Stage) nombreJoursField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors de l'ajout de la demande : " + e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        Stage stage = (Stage) nombreJoursField.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private boolean isValidURL(String url) {
        // Simple regex to validate URL (allows http, https, ftp schemes)
        String urlRegex = "^(https?|ftp)://[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+(/[a-zA-Z0-9-./?%&=]*)?$";
        return Pattern.matches(urlRegex, url);
    }
}