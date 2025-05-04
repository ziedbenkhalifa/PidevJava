package tn.cinema.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import tn.cinema.entities.Salle;
import tn.cinema.services.SalleService;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjouterSalle implements Initializable {

    @FXML
    private TextField tfNomSalle;

    @FXML
    private ComboBox<String> cbDisponibilite;

    @FXML
    private ComboBox<String> tfEmplacement;

    @FXML
    private ComboBox<String> cbStatut;

    @FXML
    private ComboBox<String> tfType;

    @FXML
    private TextField tfPlaces;

    @FXML
    private Button btnAjouterSalle;

    @FXML
    private Button btnResetSalle;

    @FXML
    private DatePicker dateMaintenance; // 🔥 Ajout du DatePicker

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Tu peux initialiser ici si besoin
    }

    @FXML
    public void ajouteSalle() {
        // Récupérer les valeurs des champs
        String nom = tfNomSalle.getText();
        String type = tfType.getValue();
        String disponibilite = cbDisponibilite.getValue();
        String emplacement = tfEmplacement.getValue();
        String statut = cbStatut.getValue();
        String placesStr = tfPlaces.getText();
        LocalDate dateMaintenanceValue = dateMaintenance.getValue(); // 🔥 récupérer la date

        // Validation du nom de la salle
        if (!isNomSalleValide(nom)) {
            afficherErreur("❌ Le nom de la salle doit contenir uniquement des lettres et des chiffres.");
            return;
        }

        // Validation du nombre de places
        int places;
        try {
            places = Integer.parseInt(placesStr);
            if (places <= 0) {
                afficherErreur("❌ Le nombre de places doit être un entier positif.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherErreur("❌ Le nombre de places doit être un entier valide.");
            return;
        }

        // Vérifier que toutes les ComboBox sont sélectionnées
        if (type == null || disponibilite == null || emplacement == null || statut == null) {
            afficherErreur("❌ Veuillez sélectionner une valeur pour tous les champs de type, disponibilité, emplacement et statut.");
            return;
        }

        // Vérifier que la date de maintenance est sélectionnée
        if (dateMaintenanceValue == null) {
            afficherErreur("❌ Veuillez choisir une date de maintenance.");
            return;
        }

        // Confirmation avant ajout
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirmer l'ajout");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir ajouter cette salle ?");
        if (confirmationAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        // Création de l'objet Salle
        Salle nouvelleSalle = new Salle();
        nouvelleSalle.setNom_salle(nom);
        nouvelleSalle.setType_salle(type);
        nouvelleSalle.setDisponibilite(disponibilite);
        nouvelleSalle.setEmplacement(emplacement);
        nouvelleSalle.setStatut(statut);
        nouvelleSalle.setNombre_de_place(places);

        // ⚠️ La date de maintenance est juste stockée en mémoire (pas en base)
        System.out.println("Date de maintenance prévue : " + dateMaintenanceValue); // Affichage dans la console pour debug

        // Ajout à la base de données
        try {
            SalleService salleService = new SalleService();
            salleService.ajouter(nouvelleSalle);

            // ✅ Alerte de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("✔ Salle ajoutée avec succès !");
            successAlert.showAndWait();

            // ✅ Redirection vers ListeSalle.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeSalle.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tfNomSalle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'ajout de la salle : " + e.getMessage());
        } catch (IOException e) {
            afficherErreur("Erreur lors du chargement de la page ListeSalle : " + e.getMessage());
        }
    }

    @FXML
    public void resetForm() {
        tfNomSalle.clear();
        tfType.getSelectionModel().clearSelection();
        tfPlaces.clear();
        cbDisponibilite.getSelectionModel().clearSelection();
        cbStatut.getSelectionModel().clearSelection();
        tfEmplacement.getSelectionModel().clearSelection();
        dateMaintenance.setValue(null); // 🔥 Réinitialiser aussi la date
    }

    private boolean isNomSalleValide(String nom) {
        return nom.matches("[a-zA-Z0-9]+");
    }

    private void afficherErreur(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur");
        errorAlert.setHeaderText("Erreur de saisie");
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    // Setters
    public void setTfNomSalle(TextField tfNomSalle) {
        this.tfNomSalle = tfNomSalle;
    }

    public void setCbDisponibilite(ComboBox<String> cbDisponibilite) {
        this.cbDisponibilite = cbDisponibilite;
    }

    public void setTfEmplacement(ComboBox<String> tfEmplacement) {
        this.tfEmplacement = tfEmplacement;
    }

    public void setCbStatut(ComboBox<String> cbStatut) {
        this.cbStatut = cbStatut;
    }

    public void setTfType(ComboBox<String> tfType) {
        this.tfType = tfType;
    }

    public void setTfPlaces(TextField tfPlaces) {
        this.tfPlaces = tfPlaces;
    }

    public void setBtnAjouterSalle(Button btnAjouterSalle) {
        this.btnAjouterSalle = btnAjouterSalle;
    }

    public void setBtnResetSalle(Button btnResetSalle) {
        this.btnResetSalle = btnResetSalle;
    }

    public void setDateMaintenance(DatePicker dateMaintenance) {
        this.dateMaintenance = dateMaintenance;
    }
}
