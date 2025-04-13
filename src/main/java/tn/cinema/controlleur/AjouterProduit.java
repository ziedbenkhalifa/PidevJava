package tn.cinema.controlleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class AjouterProduit {

    @FXML
    private ComboBox<String> categorie;

    @FXML
    private TextField description;

    @FXML
    private TextField image;

    @FXML
    private TextField nom;

    @FXML
    private TextField prix;

    private ProduitService produitService = new ProduitService();

    @FXML
    void initialize() {
        // Prepopulate ComboBox with categories
        categorie.getItems().addAll("Vetement", "Maison", "Food"); // Example categories
    }

    @FXML
    void ajouter(ActionEvent event) {
        // Récupérer les données du formulaire
        String nomProduit = nom.getText();
        String descriptionProduit = description.getText();
        String categorieProduit = categorie.getValue(); // Get selected category
        String imageProduit = image.getText(); // Get image file path
        String prixText = prix.getText();

        if (nomProduit.isEmpty() || prixText.isEmpty() || categorieProduit == null) {
            afficherAlerte("Erreur", "Nom, prix et catégorie sont obligatoires.");
            return;
        }

        try {
            double prixProduit = Double.parseDouble(prixText);

            // Définir la date actuelle comme la date d'ajout
            LocalDateTime dateProduit = LocalDateTime.now();

            // Créer l'objet Produit
            Produit produit = new Produit(nomProduit, prixProduit, categorieProduit, descriptionProduit, imageProduit, dateProduit);

            // Ajouter le produit via le service
            produitService.ajouter(produit);

            // Afficher un message de succès
            afficherAlerte("Succès", "Produit ajouté avec succès !");

            // Vider les champs du formulaire après l'ajout
            viderChamps();

        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le prix doit être un nombre.");
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

    // Méthode pour vider les champs après l'ajout
    private void viderChamps() {
        nom.clear();
        description.clear();
        categorie.setValue(null); // Clear the selected category
        image.clear();
        prix.clear();
    }

    @FXML
    void choisirImage(ActionEvent event) {
        // Ouvrir une boîte de dialogue pour choisir un fichier image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // Ouvrir la boîte de dialogue et récupérer le fichier sélectionné
        File selectedFile = fileChooser.showOpenDialog(null);

        // Si un fichier est sélectionné, mettre à jour le champ image avec le chemin du fichier
        if (selectedFile != null) {
            image.setText(selectedFile.getAbsolutePath());
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
}
