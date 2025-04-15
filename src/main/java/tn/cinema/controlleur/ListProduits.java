package tn.cinema.controlleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import javafx.util.Duration;
import tn.cinema.entities.Produit;
import tn.cinema.services.ProduitService;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ListProduits {

    @FXML
    private ListView<HBox> listViewProduits;

    private ObservableList<Produit> produits = FXCollections.observableArrayList();
    private ProduitService produitService = new ProduitService();
    private CommandeService commandeService = new CommandeService();  // Instancier CommandeService

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
            // Image
            ImageView imageView = new ImageView();
            try {
                // Vérification si l'image existe
                String imagePath = "file:" + p.getImage();
                Image image = new Image(imagePath, 200, 200, true, true);
                if (image.isError()) {
                    throw new Exception("Image invalide.");
                }
                imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Erreur image: " + e.getMessage());
                // Image par défaut en cas d'erreur
                Image defaultImage = new Image("file:default-image.png", 200, 200, true, true);
                imageView.setImage(defaultImage);
            }

            // Appliquer l'animation de zoom au survol de l'image
            applyZoomOnHover(imageView);

            imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.1, 2, 2);");

            // Labels
            Label lblNom = new Label(p.getNom());
            Label lblPrix = new Label(p.getPrix() + " DT");
            Label lblDescription = new Label(p.getDescription());  // Ajout de la description

            lblNom.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            lblPrix.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");
            lblDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-wrap-text: true;");  // Style pour la description

            VBox infoBox = new VBox(5, lblNom, lblPrix, lblDescription);  // Ajouter description au VBox
            infoBox.setAlignment(Pos.CENTER_LEFT);

            // === Button Commander ===
            Button btnCommander = new Button("Commander");
            btnCommander.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 20; -fx-padding: 15 30; -fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 5);");

            // Animation lors du survol du bouton
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

            // Animation lors du clic sur le bouton
            btnCommander.setOnAction(e -> {
                commanderProduit(p);
                // Animation de clic
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

            // Layout
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox produitBox = new HBox(20, imageView, infoBox, spacer, btnCommander);
            produitBox.setAlignment(Pos.CENTER_LEFT);
            produitBox.setPadding(new Insets(15));
            produitBox.setStyle(
                    "-fx-background-color: linear-gradient(to right, #ffffff, #f4f6f8);" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: #dfe6e9;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
            );

            listViewProduits.getItems().add(produitBox);
        }
    }

    private void commanderProduit(Produit p) {
        // Vérifier si une commande en cours existe
        int commandeId = getIdCommandeEnCours();

        if (commandeId == -1) {  // Si aucune commande en cours n'existe
            // Créer une nouvelle commande
            Commande nouvelleCommande = new Commande();  // Créer une nouvelle commande
            nouvelleCommande.setUserId(1);  // Remplacer par l'ID réel de l'utilisateur

            // Convertir LocalDate en LocalDateTime (minuit comme heure par défaut)
            LocalDateTime dateCommande = LocalDate.now().atStartOfDay();  // Utilisation de atStartOfDay()

            nouvelleCommande.setDateCommande(dateCommande);  // Date de la commande avec heure

            // Ajouter la commande à la base de données
            commandeService.ajouter(nouvelleCommande);

            // Récupérer l'ID de la nouvelle commande
            commandeId = commandeService.recupererDerniereCommandeId();  // Méthode corrigée dans CommandeService
            if (commandeId == -1) {
                System.out.println("❌ Impossible de récupérer l'ID de la nouvelle commande.");
                return;
            }
            System.out.println("Nouvelle commande créée avec l'ID : " + commandeId);
        }

        // Ajouter le produit à la commande existante
        if (commandeId != -1) {
            try {
                // Ajouter le produit à la commande dans la base de données
                commandeService.ajouterProduitACommande(commandeId, p.getId());

                // Ajouter la commande à la liste des commandes en mémoire
                Commande commandeExistante = commandeService.recupererParId(commandeId);
                if (commandeExistante != null) {
                    // Vous pouvez avoir une liste des commandes en mémoire si vous le souhaitez.
                    // Par exemple, une liste observable des commandes que vous mettez à jour ici.
                    // Si vous voulez l'ajouter à une liste dans le contrôleur :
                    ObservableList<Commande> commandesList = FXCollections.observableArrayList();
                    commandesList.add(commandeExistante);  // Ajouter la commande à la liste

                    System.out.println("🛒 Produit commandé : " + p.getNom());

                    // Afficher un message de confirmation
                    showConfirmationMessage("Commande passée avec succès", "Le produit '" + p.getNom() + "' a été ajouté à votre commande.");
                } else {
                    System.out.println("❌ Impossible de récupérer la commande existante.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de l'ajout du produit à la commande : " + e.getMessage());
                showErrorMessage("Erreur", "Une erreur est survenue lors de l'ajout du produit à votre commande.");
            }
        } else {
            System.out.println("❌ Aucune commande valide trouvée.");
            showErrorMessage("Erreur", "Aucune commande valide trouvée.");
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
}
