package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

import java.sql.Date;
import java.sql.SQLException;

public class AjouterPublicite {

    @FXML
    private TextField demandeIdField;

    @FXML
    private TextField dateDebutField;

    @FXML
    private TextField dateFinField;

    @FXML
    private TextField supportField;

    @FXML
    private TextField montantField;

    private InterfacePublicites parentController; // For InterfacePublicites
    private InterfaceDemandes demandeParentController; // For InterfaceDemandes

    private final PubliciteService publiciteService = new PubliciteService();
    private boolean isFromDemande = false;

    @FXML
    private void ajouterPublicite() {
        int demandeId;
        Date dateDebut;
        Date dateFin;
        String support;
        double montant;

        // Validate Demande ID
        try {
            demandeId = Integer.parseInt(demandeIdField.getText());
        } catch (NumberFormatException e) {
            showAlert("Champ invalide", "Veuillez entrer un identifiant de demande numérique valide.");
            return;
        }

        // Validate Date début
        try {
            dateDebut = Date.valueOf(dateDebutField.getText());
        } catch (IllegalArgumentException e) {
            showAlert("Champ invalide", "Veuillez entrer une date de début valide au format yyyy-MM-dd.");
            return;
        }

        // Validate Date fin
        try {
            dateFin = Date.valueOf(dateFinField.getText());
        } catch (IllegalArgumentException e) {
            showAlert("Champ invalide", "Veuillez entrer une date de fin valide au format yyyy-MM-dd.");
            return;
        }

        // Check if dateDebut is before dateFin
        if (dateFin.before(dateDebut)) {
            showAlert("Dates invalides", "La date de fin doit être postérieure à la date de début.");
            return;
        }

        // Validate Support
        support = supportField.getText().trim();
        if (support.isEmpty()) {
            showAlert("Champ manquant", "Veuillez entrer le support de la publicité.");
            return;
        }

        // Validate Montant
        try {
            montant = Double.parseDouble(montantField.getText());
            if (montant < 0) {
                showAlert("Montant invalide", "Le montant ne peut pas être négatif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Champ invalide", "Veuillez entrer un montant numérique valide.");
            return;
        }

        // Création de l'objet Publicite
        Publicite publicite = new Publicite();
        publicite.setDemandeId(demandeId);
        publicite.setDateDebut(dateDebut);
        publicite.setDateFin(dateFin);
        publicite.setSupport(support);
        publicite.setMontant(montant);

        // Enregistrement via le service
        try {
            publiciteService.ajouterpub(publicite);
            showAlert("Succès", "Publicité ajoutée avec succès !", Alert.AlertType.INFORMATION);

            // Rafraîchir la liste dans l'interface parent
            if (isFromDemande && demandeParentController != null) {
                demandeParentController.refreshList();
            } else if (parentController != null) {
                parentController.refreshList();
            }

            // Fermer la fenêtre
            Stage stage = (Stage) demandeIdField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("Erreur SQL", "Erreur lors de l'ajout de la publicité : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Quand ouvert depuis InterfacePublicites
    public void setParentController(InterfacePublicites parentController) {
        this.parentController = parentController;
        this.demandeParentController = null;
        this.isFromDemande = false;
        demandeIdField.setEditable(true);
        demandeIdField.setStyle("");
    }

    // Quand ouvert depuis InterfaceDemandes
    public void setParentController(InterfaceDemandes demandeParentController) {
        this.demandeParentController = demandeParentController;
        this.parentController = null;
        this.isFromDemande = true;
    }

    public void setDemandeId(int demandeId) {
        demandeIdField.setText(String.valueOf(demandeId));
        demandeIdField.setEditable(false);
        demandeIdField.setStyle("-fx-background-color: #e0e0e0;");
        this.isFromDemande = true;
    }
}
