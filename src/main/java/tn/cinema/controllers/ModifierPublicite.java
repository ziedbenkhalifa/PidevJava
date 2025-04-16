package tn.cinema.controllers;

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
import java.time.LocalDate;
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
        // Rien ici car les données seront initialisées après avec setPublicite()
    }

    /**
     * Méthode appelée lorsqu'on clique sur le bouton "Modifier"
     */
    @FXML
    private void modifierPublicite() {
        try {
            // Validate that all fields are filled
            String demandeIdText = demandeIdField.getText().trim();
            String dateDebutText = dateDebutField.getText().trim();
            String dateFinText = dateFinField.getText().trim();
            String support = supportField.getText().trim();
            String montantText = montantField.getText().trim();

            if (demandeIdText.isEmpty() || dateDebutText.isEmpty() || dateFinText.isEmpty() || support.isEmpty() || montantText.isEmpty()) {
                showAlert("Tous les champs doivent être remplis.");
                return;
            }

            // Validate support format (must start with https://)
            if (!support.matches("^https://\\S+")) {
                showAlert("Le champ Support doit commencer par 'https://' suivi d'une adresse valide (ex. https://exemple.com).");
                return;
            }

            // Validate date formats and relationships
            LocalDate dateDebut;
            LocalDate dateFin;
            try {
                dateDebut = LocalDate.parse(dateDebutText);
                dateFin = LocalDate.parse(dateFinText);
            } catch (java.time.format.DateTimeParseException e) {
                showAlert("Veuillez entrer des dates valides au format yyyy-MM-dd.");
                return;
            }

            // Check if dateDebut is >= current date
            LocalDate currentDate = LocalDate.now();
            if (dateDebut.isBefore(currentDate)) {
                showAlert("La date de début doit être supérieure ou égale à la date d'aujourd'hui.");
                return;
            }

            // Check if dateDebut < dateFin
            if (!dateDebut.isBefore(dateFin)) {
                showAlert("La date de début doit être antérieure à la date de fin.");
                return;
            }

            // Lecture et validation des champs
            int demandeId = Integer.parseInt(demandeIdText);
            Date sqlDateDebut = Date.valueOf(dateDebut);
            Date sqlDateFin = Date.valueOf(dateFin);
            double montant = Double.parseDouble(montantText);

            // Mise à jour de l'objet
            Publicite updatedPublicite = new Publicite();
            updatedPublicite.setDemandeId(demandeId);
            updatedPublicite.setDateDebut(sqlDateDebut);
            updatedPublicite.setDateFin(sqlDateFin);
            updatedPublicite.setSupport(support);
            updatedPublicite.setMontant(montant);

            // Persistance
            publiciteService.modifierpub(publicite.getId(), updatedPublicite);

            // Rafraîchissement de la liste (dans la vue précédente)
            if (parentController != null) {
                parentController.refreshList();
            }

            // Fermeture de la fenêtre
            Stage stage = (Stage) demandeIdField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer des valeurs numériques valides pour Demande ID et Montant.");
        } catch (SQLException e) {
            showAlert("Erreur lors de la modification de la publicité : " + e.getMessage());
        }
    }

    /**
     * Méthode à appeler après le chargement du contrôleur, pour injecter l'objet à modifier
     */
    public void setPublicite(Publicite publicite) {
        this.publicite = publicite;
        initialiserChamps();
    }

    /**
     * Injecte le contrôleur parent (pour pouvoir rafraîchir la liste)
     */
    public void setParentController(InterfacePublicites parentController) {
        this.parentController = parentController;
    }

    /**
     * Pré-remplit les champs avec les données existantes
     */
    private void initialiserChamps() {
        if (publicite != null) {
            demandeIdField.setText(String.valueOf(publicite.getDemandeId()));
            dateDebutField.setText(publicite.getDateDebut().toString());
            dateFinField.setText(publicite.getDateFin().toString());
            supportField.setText(publicite.getSupport());
            montantField.setText(String.valueOf(publicite.getMontant()));
        }
    }

    /**
     * Affiche une alerte avec un message donné
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}