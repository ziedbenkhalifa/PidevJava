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

    private PubliciteService publiciteService = new PubliciteService();

    private boolean isFromDemande = false; // Flag to track if opened from InterfaceDemandes

    @FXML
    private void ajouterPublicite() {
        try {
            // Validate and parse inputs
            int demandeId = Integer.parseInt(demandeIdField.getText());
            Date dateDebut = Date.valueOf(dateDebutField.getText());
            Date dateFin = Date.valueOf(dateFinField.getText());
            String support = supportField.getText();
            double montant = Double.parseDouble(montantField.getText());

            // Create a new Publicite object
            Publicite publicite = new Publicite();
            publicite.setDemandeId(demandeId);
            publicite.setDateDebut(dateDebut);
            publicite.setDateFin(dateFin);
            publicite.setSupport(support);
            publicite.setMontant(montant);

            // Add the publicite using the service
            publiciteService.ajouterpub(publicite);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Publicité ajoutée avec succès !");
            alert.showAndWait();

            // Refresh the parent ListView
            if (isFromDemande && demandeParentController != null) {
                demandeParentController.refreshList();
            } else if (parentController != null) {
                parentController.refreshList();
            }

            // Close the window
            Stage stage = (Stage) demandeIdField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez entrer des valeurs numériques valides pour Demande ID et Montant.");
            alert.showAndWait();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez entrer des dates valides au format yyyy-MM-dd.");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout de la publicité : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Method to set the parent controller when opened from InterfacePublicites
    public void setParentController(InterfacePublicites parentController) {
        this.parentController = parentController;
        this.demandeParentController = null;
        this.isFromDemande = false;
        demandeIdField.setEditable(true); // Allow editing when opened from InterfacePublicites
        demandeIdField.setStyle(""); // Reset style
    }

    // Overloaded method to set the parent controller when opened from InterfaceDemandes
    public void setParentController(InterfaceDemandes demandeParentController) {
        this.demandeParentController = demandeParentController;
        this.parentController = null;
        this.isFromDemande = true;
    }

    // Method to set the Demande ID and make the field non-editable
    public void setDemandeId(int demandeId) {
        demandeIdField.setText(String.valueOf(demandeId));
        demandeIdField.setEditable(false); // Make the field non-editable
        demandeIdField.setStyle("-fx-background-color: #e0e0e0;"); // Visually indicate it's non-editable
        this.isFromDemande = true;
    }
}