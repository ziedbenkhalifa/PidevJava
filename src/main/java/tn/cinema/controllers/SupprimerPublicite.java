package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

public class SupprimerPublicite {
    @FXML
    private Label confirmationLabel;

    @FXML
    private void supprimerPublicite() {
        try {
            // Delete the publicite using the service
            publiciteService.supprimerpub(publicite.getId());

            // Refresh the parent ListView
            parentController.refreshList();

            // Close the window
            Stage stage = (Stage) confirmationLabel.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression de la publicité : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private Publicite publicite;
    private InterfacePublicites parentController;
    private PubliciteService publiciteService = new PubliciteService();

    public void setPublicite(Publicite publicite) {
        this.publicite = publicite;
        confirmationLabel.setText("Êtes-vous sûr de vouloir supprimer la publicité avec l'ID " + publicite.getId() + " ?");
    }

    public void setParentController(InterfacePublicites parentController) {
        this.parentController = parentController;
    }
}