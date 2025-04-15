package tn.cinema.controlleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Produit;
import tn.cinema.services.ProduitService;
import javafx.scene.Node;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

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
        }
    }

    // Méthode pour récupérer le produit sélectionné
    public void setProduitSelectionne(Produit produit) {
        this.produitSelectionne = produit;
    }

    // Méthode pour modifier le produit
    @FXML
    private void modifierProduit() {
        try {
            if (produitSelectionne != null) {
                produitSelectionne.setNom(nom.getText());
                produitSelectionne.setPrix(Double.parseDouble(prix.getText()));
                produitSelectionne.setCategorie(categorie.getValue());
                produitSelectionne.setDescription(description.getText());
                produitSelectionne.setImage(image.getText());

                // Mettre à jour le produit dans le service
                produitService.modifier(produitSelectionne);

                showAlert(AlertType.INFORMATION, "Succès", "Le produit a été modifié avec succès.");
            } else {
                showAlert(AlertType.WARNING, "Erreur", "Produit non trouvé.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs correctement.");
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
    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
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
