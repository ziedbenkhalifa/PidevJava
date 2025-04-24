package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.sql.SQLException;

public class SupprimerDemandeClientController {
    @FXML
    private Text confirmationText;

    @FXML
    private Text detailsText;

    private DemandeService demandeService = new DemandeService();
    private DemandeClient parentController;
    private Demande demande;

    public void setParentController(DemandeClient parentController) {
        this.parentController = parentController;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
        confirmationText.setText("Êtes-vous sûr de vouloir supprimer la demande ID " + demande.getId() + " ?");
        detailsText.setText(
                "Nombre de Jours: " + demande.getNombreJours() + "\n" +
                        "Description: " + demande.getDescription() + "\n" +
                        "Type: " + demande.getType() + "\n" +
                        "Lien Supplémentaire: " + (demande.getLienSupplementaire() != null ? demande.getLienSupplementaire() : "N/A")
        );
    }

    @FXML
    private void confirmerSuppression() {
        try {
            demandeService.supprimer(demande.getId());
            parentController.refreshList();
            Stage stage = (Stage) confirmationText.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void annuler() {
        Stage stage = (Stage) confirmationText.getScene().getWindow();
        stage.close();
    }
}