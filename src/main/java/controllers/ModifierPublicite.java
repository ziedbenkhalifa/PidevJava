package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierPublicite implements Initializable {
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

    private Publicite publicite;
    private InterfacePublicites parentController;
    private PubliciteService publiciteService = new PubliciteService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pre-fill the form with the existing publicite data
        if (publicite != null) {
            demandeIdField.setText(String.valueOf(publicite.getDemandeId()));
            dateDebutField.setText(publicite.getDateDebut().toString());
            dateFinField.setText(publicite.getDateFin().toString());
            supportField.setText(publicite.getSupport());
            montantField.setText(String.valueOf(publicite.getMontant()));
        }
    }

    @FXML
    private void modifierPublicite() {
        try {
            // Validate and parse inputs
            int demandeId = Integer.parseInt(demandeIdField.getText());
            Date dateDebut = Date.valueOf(dateDebutField.getText());
            Date dateFin = Date.valueOf(dateFinField.getText());
            String support = supportField.getText();
            double montant = Double.parseDouble(montantField.getText());

            // Update the publicite object
            Publicite updatedPublicite = new Publicite();
            updatedPublicite.setDemandeId(demandeId);
            updatedPublicite.setDateDebut(dateDebut);
            updatedPublicite.setDateFin(dateFin);
            updatedPublicite.setSupport(support);
            updatedPublicite.setMontant(montant);

            // Update the publicite in the database
            publiciteService.modifierpub(publicite.getId(), updatedPublicite);

            // Refresh the parent ListView
            parentController.refreshList();

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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification de la publicité : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setPublicite(Publicite publicite) {
        this.publicite = publicite;
    }

    public void setParentController(InterfacePublicites parentController) {
        this.parentController = parentController;
    }
}