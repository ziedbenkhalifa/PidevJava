package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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

    // Mouse Hover for TextField (scale effect)
    @FXML
    private void onMouseEntered(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setScaleX(1.03);
        source.setScaleY(1.03);
    }

    @FXML
    private void onMouseExited(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setScaleX(1.0);
        source.setScaleY(1.0);
    }

    // Mouse Hover for Button (DropShadow effect + scale)
    @FXML
    private void onButtonHover(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setScaleX(1.05);
        source.setScaleY(1.05);
        source.setEffect(new DropShadow(10, Color.web("#bbbbbb")));
    }

    @FXML
    private void onButtonExit(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setScaleX(1.0);
        source.setScaleY(1.0);
        source.setEffect(null);
    }

    @FXML
    void initialize() {
        // Pré-remplir la ComboBox avec des catégories
        categorie.getItems().addAll("Vetement", "Maison", "Food");

        // Ajout des listeners pour le filtrage en live
        nom.textProperty().addListener((observable, oldValue, newValue) -> {
            String filtered = produitService.filterBadWords(newValue);
            if (!newValue.equals(filtered)) {
                nom.setText(filtered);  // Mettre à jour le texte avec les étoiles
                nom.positionCaret(filtered.length());  // Replacer le curseur à la fin du texte
            }
        });

        description.textProperty().addListener((observable, oldValue, newValue) -> {
            String filtered = produitService.filterBadWords(newValue);
            if (!newValue.equals(filtered)) {
                description.setText(filtered);  // Mettre à jour le texte avec les étoiles
                description.positionCaret(filtered.length());  // Replacer le curseur à la fin du texte
            }
        });
    }

    @FXML
    void ajouter(ActionEvent event) {
        String nomProduit = nom.getText().trim();
        String descriptionProduit = description.getText().trim();
        String categorieProduit = categorie.getValue();
        String imageProduit = image.getText().trim();
        String prixText = prix.getText().trim();

        // Filtrage des mots interdits via le service
        nomProduit = produitService.filterBadWords(nomProduit);
        descriptionProduit = produitService.filterBadWords(descriptionProduit);

        // Vérification des champs obligatoires
        if (nomProduit.isEmpty() || prixText.isEmpty() || categorieProduit == null || imageProduit.isEmpty()) {
            afficherAlerte("Erreur", "Tous les champs sont obligatoires.");
            return;
        }

        // Validation du nom
        if (nomProduit.length() < 3) {
            afficherAlerte("Erreur", "Le nom du produit doit contenir au moins 3 caractères.");
            return;
        }

        // Validation de la description
        if (descriptionProduit.isEmpty()) {
            afficherAlerte("Erreur", "La description est obligatoire.");
            return;
        } else if (descriptionProduit.length() < 10) {
            afficherAlerte("Erreur", "La description doit contenir au moins 10 caractères.");
            return;
        }

        // Validation du prix
        double prixProduit;
        try {
            prixProduit = Double.parseDouble(prixText);
            if (prixProduit <= 0) {
                afficherAlerte("Erreur", "Le prix doit être un nombre positif.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le prix doit être un nombre valide.");
            return;
        }

        // Validation de l'image
        File imageFile = new File(imageProduit);
        if (!imageFile.exists() || imageFile.isDirectory()) {
            afficherAlerte("Erreur", "L’image sélectionnée est invalide ou n’existe pas.");
            return;
        }

        // Création du produit
        LocalDateTime dateProduit = LocalDateTime.now();
        Produit produit = new Produit(nomProduit, prixProduit, categorieProduit, descriptionProduit, imageProduit, dateProduit);

        // Ajout via service
        produitService.ajouter(produit);

        // Message succès
        afficherAlerte("Succès", "Produit ajouté avec succès !");
        viderChamps();
    }

    // Choisir une image via FileChooser
    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            image.setText(selectedFile.getAbsolutePath());
        }
    }

    // Aller à l'affichage des produits
    @FXML
    void afficher(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des produits.");
        }
    }

    // Afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Vider les champs du formulaire
    private void viderChamps() {
        nom.clear();
        description.clear();
        categorie.setValue(null);
        image.clear();
        prix.clear();
    }
}