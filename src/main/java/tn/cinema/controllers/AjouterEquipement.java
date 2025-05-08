package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import tn.cinema.entities.Equipement;
import tn.cinema.services.EquipementService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterEquipement implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfType;

    @FXML
    private TextField tfSalle;

    @FXML
    private ComboBox<String> cbEtat;

    private final EquipementService ps = new EquipementService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.getItems().addAll("Disponible", "En panne", "En maintenance");
    }

    public void AjouteEquipement(ActionEvent actionEvent) {
        // Validation des champs
        if (!isNomEquipementValide(tfNom.getText())) {
            afficherErreur("Le nom de l'équipement doit contenir uniquement des lettres et des chiffres.");
            return;
        }

        if (!isTypeValide(tfType.getText())) {
            afficherErreur("Le type doit contenir uniquement des lettres.");
            return;
        }

        if (!isSalleValide(tfSalle.getText())) {
            afficherErreur("Le numéro de la salle doit être un entier.");
            return;
        }

        try {
            // Création et ajout
            Equipement e = new Equipement(
                    tfNom.getText(),
                    tfType.getText(),
                    Integer.parseInt(tfSalle.getText()),
                    cbEtat.getValue()
            );
            ps.ajouter(e);

            // ✅ Message de succès AVANT redirection
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("✔ Équipement ajouté avec succès !");
            successAlert.showAndWait(); // On attend que l'utilisateur clique sur OK

            // ✅ Redirection vers ListeEquipement.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeEquipement.fxml"));
            Parent root = loader.load();
            tfNom.getScene().setRoot(root); // Redirige la scène actuelle vers ListeEquipement

        } catch (SQLException | NumberFormatException ex) {
            // ❌ Message d’erreur
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Échec de l'ajout");
            errorAlert.setContentText("Vérifiez les champs saisis et réessayez.");
            errorAlert.showAndWait();

            ex.printStackTrace(); // utile pour le débogage
        } catch (IOException e) {
            e.printStackTrace(); // pour les erreurs de chargement FXML
        }
    }


    // Validation pour tfNom : lettres et chiffres uniquement
    private boolean isNomEquipementValide(String text) {
        return text.matches("[a-zA-Z0-9]+");
    }

    // Validation pour tfType : uniquement des lettres
    private boolean isTypeValide(String text) {
        text = text.trim();
        return text.matches("[a-zA-Z]+");
    }

    // Validation pour tfSalle : uniquement des chiffres
    private boolean isSalleValide(String text) {
        return text.matches("[0-9]+");
    }

    // Méthode pour afficher un message d'erreur
    private void afficherErreur(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur");
        errorAlert.setHeaderText("Erreur de saisie");
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public void resetForm(ActionEvent actionEvent) {
        tfNom.clear();
        tfType.clear();
        tfSalle.clear();
        cbEtat.getSelectionModel().clearSelection();
    }

    public void setTfNom(TextField tfNom) {
        this.tfNom = tfNom;
    }

    public void setTfType(TextField tfType) {
        this.tfType = tfType;
    }

    public void setTfSalle(TextField tfSalle) {
        this.tfSalle = tfSalle;
    }

    public void setCbEtat(ComboBox<String> cbEtat) {
        this.cbEtat = cbEtat;
    }



}