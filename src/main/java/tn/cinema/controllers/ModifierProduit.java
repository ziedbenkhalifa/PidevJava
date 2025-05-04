package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.cinema.entities.Produit;
import tn.cinema.services.ProduitService;
import javafx.collections.FXCollections;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class ModifierProduit {

    @FXML
    private TextField nom;
    @FXML
    private TextField prix;
    @FXML
    private ComboBox<String> categorie;
    @FXML
    private TextField description;
    @FXML
    private TextField image;
    @FXML
    private DatePicker date;

    private ProduitService produitService = new ProduitService();
    private Produit produitSelectionne;

    @FXML
    public void initialize() {
        // Initialisation de la ComboBox pour la catégorie
        categorie.setItems(FXCollections.observableArrayList("Vetement", "Maison", "Food"));

        // Charger les informations du produit à modifier si disponible
        if (produitSelectionne != null) {
            nom.setText(produitSelectionne.getNom());
            prix.setText(String.valueOf(produitSelectionne.getPrix()));
            categorie.setValue(produitSelectionne.getCategorie());
            description.setText(produitSelectionne.getDescription());
            image.setText(produitSelectionne.getImage());
            // date.setValue(produitSelectionne.getDateAjout().toLocalDate()); // Si disponible
        }
    }

    // Méthode pour récupérer le produit sélectionné
    public void setProduitSelectionne(Produit produit) {
        this.produitSelectionne = produit;
        if (produitSelectionne != null) {
            // Remplir les champs avec les données du produit sélectionné
            nom.setText(produitSelectionne.getNom());
            prix.setText(String.valueOf(produitSelectionne.getPrix()));
            categorie.setValue(produitSelectionne.getCategorie());
            description.setText(produitSelectionne.getDescription());
            image.setText(produitSelectionne.getImage());
            // date.setValue(produitSelectionne.getDateAjout().toLocalDate());
        }
    }

    // Méthode pour modifier le produit
    @FXML
    private void modifierProduit() {
        if (produitSelectionne == null) {
            showAlert(AlertType.ERROR, "Erreur", "Aucun produit sélectionné.");
            return;
        }

        // Vérification des champs vides
        if (nom.getText().isEmpty() || prix.getText().isEmpty() || categorie.getValue() == null || description.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérification du prix
        double prixValue;
        try {
            prixValue = Double.parseDouble(prix.getText());
            if (prixValue <= 0) {
                showAlert(AlertType.WARNING, "Prix invalide", "Le prix doit être un nombre positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur", "Le prix doit être un nombre valide.");
            return;
        }

        // Appliquer les modifications
        produitSelectionne.setNom(nom.getText());
        produitSelectionne.setPrix(prixValue);
        produitSelectionne.setCategorie(categorie.getValue());
        produitSelectionne.setDescription(description.getText());
        produitSelectionne.setImage(image.getText());
        // produitSelectionne.setDateAjout(...); Si tu veux mettre à jour la date

        // Enregistrement
        try {
            produitService.modifier(produitSelectionne);
            showAlert(AlertType.INFORMATION, "Succès", "Produit modifié avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la modification.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour choisir une image à partir du fichier
    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        // Ouvrir la fenêtre de sélection de fichier
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            image.setText(file.getAbsolutePath());  // Mettre à jour le champ TextField avec le chemin de l'image
        }
    }

    // Méthode pour afficher la liste des produits
    @FXML
    void afficher(ActionEvent event) {
        try {
            // Charger la scène FXML qui affiche la liste des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduit.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Remplacer le contenu de la scène actuelle par root

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des produits.");
        }
    }

    // Méthode pour afficher une alerte (version simplifiée)
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour effacer tous les champs
    @FXML
    private void clearFields() {
        // Clear the text fields
        nom.clear();
        prix.clear();
        description.clear();
        image.clear();

        // Reset the combo box to the first item or set it to null if needed
        categorie.setValue(null);

        // Optionally, clear the date picker
        date.setValue(null);
    }
}