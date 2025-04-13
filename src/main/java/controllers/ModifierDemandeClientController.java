package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.sql.SQLException;

public class ModifierDemandeClientController {
    @FXML
    private TextField nombreJoursField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField lienSuppField;

    private DemandeService demandeService = new DemandeService();
    private DemandeClient parentController;
    private Demande demande;

    public void setParentController(DemandeClient parentController) {
        this.parentController = parentController;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
        nombreJoursField.setText(String.valueOf(demande.getNombreJours()));
        descriptionField.setText(demande.getDescription());
        typeField.setText(demande.getType());
        lienSuppField.setText(demande.getLienSupplementaire() != null ? demande.getLienSupplementaire() : "");
    }

    @FXML
    private void modifierDemande() {
        try {
            int nombreJours = Integer.parseInt(nombreJoursField.getText());
            String description = descriptionField.getText();
            String type = typeField.getText();
            String lienSupp = lienSuppField.getText();

            Demande updatedDemande = new Demande();
            updatedDemande.setNombreJours(nombreJours);
            updatedDemande.setDescription(description);
            updatedDemande.setType(type);
            updatedDemande.setLienSupplementaire(lienSupp.isEmpty() ? null : lienSupp);

            demandeService.modifier(demande.getId(), updatedDemande);
            parentController.refreshList();

            Stage stage = (Stage) nombreJoursField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le nombre de jours doit être un entier valide.");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification de la demande : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void annuler() {
        Stage stage = (Stage) nombreJoursField.getScene().getWindow();
        stage.close();
    }
}