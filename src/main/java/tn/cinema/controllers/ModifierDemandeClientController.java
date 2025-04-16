package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierDemandeClientController implements Initializable {

    @FXML
    private TextField nombreJoursField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> typeField;

    @FXML
    private TextField lienSuppField;

    private DemandeService demandeService = new DemandeService();
    private DemandeClient parentController;
    private Demande demande;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBox with allowed values
        typeField.getItems().addAll("integrefilm", "footerWeb", "backdrop");
        typeField.setPromptText("Sélectionner un type");
    }

    public void setParentController(DemandeClient parentController) {
        this.parentController = parentController;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
        nombreJoursField.setText(String.valueOf(demande.getNombreJours()));
        descriptionField.setText(demande.getDescription());
        // Set ComboBox selection based on demande type
        typeField.setValue(demande.getType());
        lienSuppField.setText(demande.getLienSupplementaire() != null ? demande.getLienSupplementaire() : "");
    }

    @FXML
    private void modifierDemande() {
        try {
            // Validate that all fields are filled
            String nombreJoursText = nombreJoursField.getText().trim();
            String description = descriptionField.getText().trim();
            String type = typeField.getValue();
            String lienSupp = lienSuppField.getText().trim();

            if (nombreJoursText.isEmpty() || description.isEmpty() || type == null || lienSupp.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Tous les champs doivent être remplis.");
                alert.showAndWait();
                return;
            }

            // Check that lienSupp starts with https://
            if (!lienSupp.startsWith("https://")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Le lien doit commencer par https://");
                alert.showAndWait();
                return;
            }

            // Parse and proceed with modification
            int nombreJours = Integer.parseInt(nombreJoursText);
            Demande updatedDemande = new Demande();
            updatedDemande.setId(demande.getId()); // Preserve the original ID
            updatedDemande.setNombreJours(nombreJours);
            updatedDemande.setDescription(description);
            updatedDemande.setType(type);
            updatedDemande.setLienSupplementaire(lienSupp);

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