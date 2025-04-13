package tn.cinema.controlleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import tn.cinema.entities.Produit;
import tn.cinema.services.ProduitService;
import javafx.scene.Node;
import java.io.IOException;

public class AfficherProduit {

    @FXML
    private ListView<HBox> listViewProduits;

    private ObservableList<Produit> produits = FXCollections.observableArrayList();

    private ProduitService produitService = new ProduitService();

    @FXML
    public void initialize() {
        produits.addAll(produitService.recuperer());
        afficherProduits();
    }

    private void afficherProduits() {
        listViewProduits.getItems().clear();

        for (Produit p : produits) {
            // Labels
            Label lblNom = new Label("Nom: " + p.getNom());
            Label lblDescription = new Label("Description: " + p.getDescription());
            Label lblCategorie = new Label("Catégorie: " + p.getCategorie());
            Label lblPrix = new Label("Prix: " + p.getPrix() + " DT");
            Label lblDate = new Label("Date: " + p.getDate());

            lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
            lblPrix.setStyle("-fx-text-fill: green; -fx-font-size: 14;");

            // Image
            ImageView imageView = new ImageView();
            try {
                Image image = new Image("file:" + p.getImage(), 120, 120, true, true);
                imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Erreur chargement image: " + e.getMessage());
            }

            // Boutons avec styles et animations
            Button btnModifier = new Button("Modifier");
            Button btnSupprimer = new Button("Supprimer");

            btnModifier.setOnAction(e -> modifierProduit(p, e));
            btnSupprimer.setOnAction(e -> supprimerProduit(p));

            // Styles CSS
            String btnStyle = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";
            String btnHover = "-fx-background-color: #0056b3;";

            String btnDeleteStyle = "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";
            String btnDeleteHover = "-fx-background-color: #a71d2a;";

            btnModifier.setStyle(btnStyle);
            btnSupprimer.setStyle(btnDeleteStyle);

            btnModifier.setOnMouseEntered(e -> btnModifier.setStyle(btnHover + " -fx-text-fill: white; -fx-font-weight: bold;"));
            btnModifier.setOnMouseExited(e -> btnModifier.setStyle(btnStyle));

            btnSupprimer.setOnMouseEntered(e -> btnSupprimer.setStyle(btnDeleteHover + " -fx-text-fill: white; -fx-font-weight: bold;"));
            btnSupprimer.setOnMouseExited(e -> btnSupprimer.setStyle(btnDeleteStyle));

            // Layout
            VBox infoBox = new VBox(5, lblNom, lblDescription, lblCategorie, lblPrix, lblDate);
            HBox btnBox = new HBox(10, btnModifier, btnSupprimer);
            VBox rightBox = new VBox(10, infoBox, btnBox);

            // Carte produit (horizontale)
            HBox produitBox = new HBox(20, imageView, rightBox);
            produitBox.setStyle(
                    "-fx-padding: 15;" +
                            "-fx-border-color: #cccccc;" +
                            "-fx-background-color: #ffffff;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
            );
            produitBox.setMinHeight(150);

            listViewProduits.getItems().add(produitBox);
        }
    }

    @FXML
    public void modifierProduit(Produit produitSelectionne, ActionEvent event) {
        if (produitSelectionne != null) {
            try {
                // Charger la vue de modification
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduit.fxml"));
                Parent root = loader.load();

                // Passer le produit sélectionné à la vue de modification
                ModifierProduit modifierController = loader.getController();
                modifierController.setProduitSelectionne(produitSelectionne);

                // Remplacer le contenu de la scène actuelle par la vue de modification
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);  // Remplacer le root de la scène actuelle avec la nouvelle vue de modification
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la fenêtre de modification.");
            }
        } else {
            showAlert("Aucun produit sélectionné", "Veuillez sélectionner un produit à modifier.");
        }
    }

    private void showAlert(String titre, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void supprimerProduit(Produit p) {
        produitService.supprimer(p.getId());
        produits.remove(p);
        afficherProduits();
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listViewProduits.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
