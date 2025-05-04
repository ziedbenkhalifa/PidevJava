package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.cinema.entities.Produit;
import tn.cinema.services.ProduitService;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ListProduits extends FrontzController{

    @FXML
    private ListView<HBox> listViewProduits;
    private ObservableList<Produit> favoris = FXCollections.observableArrayList();
    private ObservableList<Produit> produits = FXCollections.observableArrayList();
    private ProduitService produitService = new ProduitService();
    private CommandeService commandeService = new CommandeService();  // Instancier CommandeService
    @FXML
    private Button afficherChatbotButton;


    @FXML
    public void initialize() {
        produits.addAll(produitService.recuperer());
        afficherProduitsClient();

    }

    private void applyZoomOnHover(ImageView imageView) {
        // Ajouter l'effet de zoom lors du survol
        imageView.setOnMouseEntered(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
            scaleTransition.setToX(1.2);  // Agrandir l'image de 20%
            scaleTransition.setToY(1.2);  // Agrandir l'image de 20%
            scaleTransition.play();
        });

        // Revenir à la taille normale lorsque la souris quitte l'image
        imageView.setOnMouseExited(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
            scaleTransition.setToX(1);  // Retour à la taille normale
            scaleTransition.setToY(1);  // Retour à la taille normale
            scaleTransition.play();
        });
    }

    private void afficherProduitsClient() {
        listViewProduits.getItems().clear();

        for (Produit p : produits) {
            // === Image ===
            ImageView imageView = new ImageView();
            try {
                String imagePath = "file:" + p.getImage();
                Image image = new Image(imagePath, 200, 200, true, true);
                if (image.isError()) throw new Exception("Image invalide.");
                imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Erreur image: " + e.getMessage());
                Image defaultImage = new Image("file:default-image.png", 200, 200, true, true);
                imageView.setImage(defaultImage);
            }

            applyZoomOnHover(imageView);
            imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.1, 2, 2);");

            // === Infos ===
            Label lblNom = new Label(p.getNom());
            Label lblPrix = new Label(p.getPrix() + " DT");
            Label lblDescription = new Label(p.getDescription());

            lblNom.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            lblPrix.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");
            lblDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-wrap-text: true;");

            VBox infoBox = new VBox(5, lblNom, lblPrix, lblDescription);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            // === Button Commander ===
            Button btnCommander = new Button("Commander");
            btnCommander.setStyle(
                    "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                            "-fx-font-size: 18px; -fx-background-radius: 20; -fx-padding: 15 30; -fx-border-radius: 20; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 5);"
            );

            btnCommander.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btnCommander);
                st.setToX(1.1);
                st.setToY(1.1);
                st.play();
            });
            btnCommander.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btnCommander);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            btnCommander.setOnAction(e -> {
                commanderProduit(p);
                ScaleTransition st = new ScaleTransition(Duration.millis(100), btnCommander);
                st.setToX(0.9);
                st.setToY(0.9);
                st.setOnFinished(evt -> {
                    ScaleTransition st2 = new ScaleTransition(Duration.millis(100), btnCommander);
                    st2.setToX(1);
                    st2.setToY(1);
                    st2.play();
                });
                st.play();
            });

            Button btnFavoris = new Button("♡");
            btnFavoris.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-size: 24px;" +
                            "-fx-text-fill: #e74c3c;" +
                            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.6), 10, 0, 0, 2);"
            );

// Zoom au survol
            btnFavoris.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btnFavoris);
                st.setToX(1.3);
                st.setToY(1.3);
                st.play();
            });
// Retour à la taille normale
            btnFavoris.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btnFavoris);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

// Clic : toggle favori + animation « pulse »
            btnFavoris.setOnAction(e -> {
                boolean added = ajouterAuxFavoris(p);
                // changer l’icône cœur plein/vide
                btnFavoris.setText(added ? "❤️" : "♡");

                // Pulse au clic
                ScaleTransition st = new ScaleTransition(Duration.millis(150), btnFavoris);
                st.setFromX(1.3);
                st.setFromY(1.3);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });

            // === Layout HBox ===
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox produitBox = new HBox(20, imageView, infoBox, spacer, btnCommander, btnFavoris);
            produitBox.setAlignment(Pos.CENTER_LEFT);
            produitBox.setPadding(new Insets(15));
            produitBox.setStyle(
                    "-fx-background-color: linear-gradient(to right, #0b0f29, #0b0f29);" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: #0b0f29;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
            );

            listViewProduits.getItems().add(produitBox);
        }
    }

    public boolean ajouterAuxFavoris(Produit produit) {
        if (!favoris.contains(produit)) {
            favoris.add(produit);
            // Mettre à jour la ListView
            return true;  // Produit ajouté
        } else {
            return false;  // Produit déjà dans les favoris
        }
    }


    private void commanderProduit(Produit p) {
        int commandeId = getIdCommandeEnCours();

        if (commandeId == -1) {
            // Création d'une nouvelle commande
            Commande nouvelleCommande = new Commande();
            nouvelleCommande.setDateCommande(LocalDate.now().atStartOfDay());
            nouvelleCommande.setMontantPaye(p.getPrix()); // 💰 montant basé sur le produit
            nouvelleCommande.setEtat("en cours");

            // Ajouter la commande via le service (qui gère le userId via SessionManager)
            commandeService.ajouter(nouvelleCommande);

            // Récupérer l'ID de la nouvelle commande
            commandeId = commandeService.recupererDerniereCommandeId();
            if (commandeId == -1) {
                System.err.println("❌ Impossible de récupérer l'ID de la commande.");
                return;
            }
        }

        // Ajouter le produit à la commande
        try {
            commandeService.ajouterProduitACommande(commandeId, p.getId());

            Commande commandeExistante = commandeService.recupererParId(commandeId);
            if (commandeExistante != null) {
                System.out.println("🛒 Produit ajouté à la commande : " + p.getNom());
                showConfirmationMessage("Commande réussie", "Le produit '" + p.getNom() + "' a été ajouté à votre commande.");
            } else {
                System.err.println("❌ Impossible de retrouver la commande après l'ajout.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du produit à la commande : " + e.getMessage());
            showErrorMessage("Erreur", "Impossible d'ajouter le produit à la commande.");
        }
    }


    private void showConfirmationMessage(String title, String message) {
        // Création de l'alerte
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Personnalisation des boutons
        Button btnOk = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        btnOk.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20;");
        btnOk.setDefaultButton(true);

        // Personnalisation de la fenêtre de l'alerte
        alert.getDialogPane().setStyle("-fx-font-size: 16px; -fx-font-family: Arial; -fx-background-color: #ecf0f1;");

        // Ajout d'une icône (en option)
        Image icon = new Image("file:confirmation-icon.png");  // Remplacez par le chemin de votre icône
        alert.setGraphic(new ImageView(icon));

        // Affichage de l'alerte
        alert.showAndWait();
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Personnalisation du design de l'alerte
        alert.getDialogPane().setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        alert.showAndWait();
    }

    // Méthode pour récupérer l'ID de la commande en cours
    private int getIdCommandeEnCours() {
        // Remplacer cette logique par la vraie logique de récupération de la commande en cours
        return -1;  // Valeur par défaut si aucune commande n'existe
    }



    @FXML
    private void afficherMesCommandes() {
        try {
            // Vérifier si le fichier FXML existe
            java.net.URL fxmlUrl = getClass().getResource("/MesCommandes.fxml");
            if (fxmlUrl == null) {
                showErrorMessage("Erreur", "Le fichier MesCommandes.fxml n'a pas été trouvé.");
                return;
            }

            // Charger l'interface MesCommandes.fxml
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), 800, 600); // Taille du popup

            // Créer une nouvelle fenêtre modale
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            popupStage.setTitle("Mes Commandes");
            popupStage.setScene(scene);
            popupStage.setResizable(false); // Empêche le redimensionnement

            // Afficher le popup et attendre sa fermeture
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("Erreur", "Impossible de charger l'interface des commandes : " + e.getMessage());
        }
    }


    @FXML
    private void afficherFavoris() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Favoris.fxml"));
            Parent root = loader.load();

            // Récupération du controller de la fenêtre Favoris
            FavorisController ctrlFavoris = loader.getController();

            // On lui passe la même liste `favoris` que celle où vous l'ajoutez
            ctrlFavoris.setFavorisList(this.favoris);

            // On ouvre la fenêtre
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Liste des Favoris");
            popup.setScene(new Scene(root));
            popup.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void afficherChatbot(ActionEvent event) {
        try {
            // Charger le fichier FXML pour la fenêtre popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatBotView.fxml"));
            Parent root = loader.load();

            // Créer un nouveau stage pour afficher la popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);  // Empêche l'interaction avec la fenêtre principale
            popupStage.setTitle("CHAT BOT HELP");  // Titre de la fenêtre popup

            // Créer une scène pour le popup et l'afficher
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.showAndWait();  // Afficher la popup et attendre que l'utilisateur ferme la fenêtre
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void startGame(ActionEvent event) {
        try {
            // Charger le fichier FXML pour la fenêtre popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameView.fxml"));
            Parent root = loader.load();

            // Créer un nouveau stage pour afficher la popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);  // Empêche l'interaction avec la fenêtre principale
            popupStage.setTitle("Game Personalisé");  // Titre de la fenêtre popup

            // Créer une scène pour le popup et l'afficher
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.showAndWait();  // Afficher la popup et attendre que l'utilisateur ferme la fenêtre
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}