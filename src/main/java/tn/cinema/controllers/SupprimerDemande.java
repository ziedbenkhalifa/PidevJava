package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.sql.SQLException;

public class SupprimerDemande {
    @FXML
    private Label confirmationLabel;

    private DemandeService demandeService = new DemandeService();
    private Demande demande;
    private InterfaceDemandes parentController;

    public void setDemande(Demande demande) {
        this.demande = demande;
        if (demande != null) {
            confirmationLabel.setText("Êtes-vous sûr de vouloir supprimer la demande ID " + demande.getId() + " ?");
        } else {
            confirmationLabel.setText("Erreur : Aucune demande sélectionnée.");
        }
    }

    public void setParentController(InterfaceDemandes parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void onConfirmerClick() {
        try {
            if (demande == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Aucune demande sélectionnée pour la suppression.");
                alert.showAndWait();
                return;
            }

            // Call DemandeService to delete the Demande
            demandeService.supprimer(demande.getId());

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Demande supprimée avec succès !");
            alert.showAndWait();

            // Refresh the ListView in InterfaceDemandes
            if (parentController != null) {
                parentController.refreshList(); // Changed from refreshTable()
            } else {
                System.out.println("Erreur : parentController est null.");
            }

            // Close the window
            Stage stage = (Stage) confirmationLabel.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression de la demande : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onAnnulerClick() {
        // Close the window without deleting
        Stage stage = (Stage) confirmationLabel.getScene().getWindow();
        stage.close();
    }
}