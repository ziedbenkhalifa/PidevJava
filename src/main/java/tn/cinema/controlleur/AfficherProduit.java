package tn.cinema.controlleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonType;
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
import javafx.geometry.Pos;
import javafx.geometry.Insets;
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
            // === Image ===
            ImageView imageView = new ImageView();
            try {
                Image image = new Image("file:" + p.getImage(), 180, 180, true, true);
                imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Erreur chargement image: " + e.getMessage());
            }
            imageView.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.1, 2, 2);" +
                            "-fx-background-radius: 15; -fx-transition: all 0.3s ease-in-out;"
            );

            // Image hover animation
            imageView.setOnMouseEntered(e -> {
                imageView.setScaleX(1.1);
                imageView.setScaleY(1.1);
            });
            imageView.setOnMouseExited(e -> {
                imageView.setScaleX(1);
                imageView.setScaleY(1);
            });

            // === Labels ===
            Label lblNom = new Label("🛍️ " + p.getNom());
            Label lblDescription = new Label("📋 " + p.getDescription());
            Label lblCategorie = new Label("📂 " + p.getCategorie());
            Label lblPrix = new Label("💰 " + p.getPrix() + " DT");
            Label lblDate = new Label("📅 " + p.getDate());

            // Styles
            lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2c3e50;");
            lblDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            lblCategorie.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            lblPrix.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");
            lblDate.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

            // === Buttons ===
            Button btnModifier = new Button("✏️ Modifier");
            Button btnSupprimer = new Button("🗑️ Supprimer");

            btnModifier.setOnAction(e -> modifierProduit(p, e));
            btnSupprimer.setOnAction(e -> supprimerProduit(p));

            String baseBtn = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand;";
            btnModifier.setStyle(baseBtn + "-fx-background-color: #3498db; -fx-text-fill: white;");
            btnSupprimer.setStyle(baseBtn + "-fx-background-color: #e74c3c; -fx-text-fill: white;");

            // Button hover animation
            btnModifier.setOnMouseEntered(e -> {
                btnModifier.setStyle(baseBtn + "-fx-background-color: #2980b9; -fx-text-fill: white;");
                btnModifier.setScaleX(1.1);
                btnModifier.setScaleY(1.1);
            });
            btnModifier.setOnMouseExited(e -> {
                btnModifier.setStyle(baseBtn + "-fx-background-color: #3498db; -fx-text-fill: white;");
                btnModifier.setScaleX(1);
                btnModifier.setScaleY(1);
            });

            btnSupprimer.setOnMouseEntered(e -> {
                btnSupprimer.setStyle(baseBtn + "-fx-background-color: #c0392b; -fx-text-fill: white;");
                btnSupprimer.setScaleX(1.1);
                btnSupprimer.setScaleY(1.1);
            });
            btnSupprimer.setOnMouseExited(e -> {
                btnSupprimer.setStyle(baseBtn + "-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnSupprimer.setScaleX(1);
                btnSupprimer.setScaleY(1);
            });

            // Button press animation
            btnModifier.setOnMousePressed(e -> {
                btnModifier.setStyle(baseBtn + "-fx-background-color: #1f618d; -fx-text-fill: white;");
            });
            btnModifier.setOnMouseReleased(e -> {
                btnModifier.setStyle(baseBtn + "-fx-background-color: #2980b9; -fx-text-fill: white;");
            });

            btnSupprimer.setOnMousePressed(e -> {
                btnSupprimer.setStyle(baseBtn + "-fx-background-color: #922b21; -fx-text-fill: white;");
            });
            btnSupprimer.setOnMouseReleased(e -> {
                btnSupprimer.setStyle(baseBtn + "-fx-background-color: #c0392b; -fx-text-fill: white;");
            });

            // === Info section in a Grid-like layout
            GridPane infoGrid = new GridPane();
            infoGrid.setHgap(20);
            infoGrid.setVgap(10);
            infoGrid.add(lblNom, 0, 0);
            infoGrid.add(lblDescription, 1, 0);
            infoGrid.add(lblCategorie, 0, 1);
            infoGrid.add(lblPrix, 1, 1);
            infoGrid.add(lblDate, 0, 2);

            // === Buttons in line
            HBox btnBox = new HBox(15, btnModifier, btnSupprimer);
            btnBox.setAlignment(Pos.CENTER_LEFT);
            btnBox.setPadding(new Insets(10, 0, 0, 0));

            VBox contentBox = new VBox(10, infoGrid, btnBox);
            contentBox.setAlignment(Pos.CENTER_LEFT);

            // === Main product box
            HBox produitBox = new HBox(30, imageView, contentBox);
            produitBox.setPadding(new Insets(25));
            produitBox.setStyle(
                    "-fx-background-color: linear-gradient(to right, #ffffff, #f7f9fa);" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #ecf0f1;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.07), 15, 0, 0, 10);"
            );
            produitBox.setMinHeight(200);
            produitBox.setAlignment(Pos.CENTER_LEFT);

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
        // Création de la boîte de confirmation personnalisée
        VBox vbox = new VBox(15);
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 15; -fx-padding: 20;");
        vbox.setAlignment(Pos.CENTER);

        Label confirmationMessage = new Label("Êtes-vous sûr de vouloir supprimer ce produit ?");
        confirmationMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        // Boutons de confirmation
        Button btnYes = new Button("Oui");
        Button btnNo = new Button("Non");
        btnYes.setStyle("-fx-font-size: 14px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8;");
        btnNo.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8;");

        btnYes.setOnMouseEntered(e -> btnYes.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;"));
        btnYes.setOnMouseExited(e -> btnYes.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"));
        btnNo.setOnMouseEntered(e -> btnNo.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;"));
        btnNo.setOnMouseExited(e -> btnNo.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"));

        // Action pour le bouton "Oui"
        btnYes.setOnAction(event -> {
            produitService.supprimer(p.getId());
            produits.remove(p);
            afficherProduits();
            showConfirmationSuccess();
        });

        // Action pour le bouton "Non"
        btnNo.setOnAction(event -> {
            // Fermer la fenêtre sans rien faire
            System.out.println("Suppression annulée.");
        });

        // Ajouter tous les éléments à la VBox
        vbox.getChildren().addAll(confirmationMessage, btnYes, btnNo);

        // Créer une nouvelle scène pour afficher la confirmation
        Stage confirmationStage = new Stage();
        Scene confirmationScene = new Scene(vbox);
        confirmationStage.setScene(confirmationScene);
        confirmationStage.setTitle("Confirmation de suppression");
        confirmationStage.show();
    }

    private void showConfirmationSuccess() {
        // Fenêtre de succès après suppression
        VBox successBox = new VBox(15);
        successBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 15; -fx-padding: 20;");
        successBox.setAlignment(Pos.CENTER);

        Label successMessage = new Label("Le produit a été supprimé avec succès !");
        successMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");

        Button btnClose = new Button("Fermer");
        btnClose.setStyle("-fx-font-size: 14px; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8;");
        btnClose.setOnMouseEntered(e -> btnClose.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;"));
        btnClose.setOnMouseExited(e -> btnClose.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"));

        btnClose.setOnAction(event -> {
            // Fermer la fenêtre de succès
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        });

        successBox.getChildren().addAll(successMessage, btnClose);

        // Créer une nouvelle scène pour afficher le message de succès
        Stage successStage = new Stage();
        Scene successScene = new Scene(successBox);
        successStage.setScene(successScene);
        successStage.setTitle("Succès");
        successStage.show();
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
    @FXML
    void back(ActionEvent event) {
        try {
            // Charger la scène FXML qui affiche la liste des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Remplacer le contenu de la scène actuelle par root

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page de dashboard.");
        }
    }

    private void afficherAlerte(String erreur, String s) {
    }
}
