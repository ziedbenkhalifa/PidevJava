package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AfficherCommande {

    @FXML
    private ListView<HBox> listViewCommandes;

    private ObservableList<Commande> commandes = FXCollections.observableArrayList();
    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        commandes.addAll(commandeService.recuperer()); // Récupérer toutes les commandes
        afficherCommandes();
    }

    private void afficherCommandes() {
        listViewCommandes.getItems().clear();

        for (Commande c : commandes) {
            // Création des labels pour chaque information de la commande
            Label lblId = new Label("Commande ID: " + c.getId());
            Label lblUserId = new Label("Utilisateur ID: " + c.getUserId());
            Label lblMontant = new Label("Montant payé: " + c.getMontantPaye() + " DT");
            Label lblEtat = new Label("État: " + c.getEtat());
            Label lblDate = new Label("Date: " + c.getDateCommande().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            // Style des labels
            lblId.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
            lblMontant.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");
            lblEtat.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12;");
            lblDate.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

            // Création des boutons Modifier et Supprimer
            Button btnModifier = new Button("Modifier");
            Button btnSupprimer = new Button("Supprimer");

            btnModifier.setOnAction(e -> modifierCommande(c, e));
            btnSupprimer.setOnAction(e -> supprimerCommande(c));

            // Style des boutons
            String btnStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand; -fx-background-color: #3498db; -fx-text-fill: white;";
            String btnDeleteStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand; -fx-background-color: #e74c3c; -fx-text-fill: white;";
            btnModifier.setStyle(btnStyle);
            btnSupprimer.setStyle(btnDeleteStyle);

            // Hover Animation for Buttons
            btnModifier.setOnMouseEntered(e -> {
                btnModifier.setStyle(btnStyle + "-fx-background-color: #2980b9;");
                btnModifier.setScaleX(1.1);
                btnModifier.setScaleY(1.1);
            });
            btnModifier.setOnMouseExited(e -> {
                btnModifier.setStyle(btnStyle);
                btnModifier.setScaleX(1);
                btnModifier.setScaleY(1);
            });

            btnSupprimer.setOnMouseEntered(e -> {
                btnSupprimer.setStyle(btnDeleteStyle + "-fx-background-color: #c0392b;");
                btnSupprimer.setScaleX(1.1);
                btnSupprimer.setScaleY(1.1);
            });
            btnSupprimer.setOnMouseExited(e -> {
                btnSupprimer.setStyle(btnDeleteStyle);
                btnSupprimer.setScaleX(1);
                btnSupprimer.setScaleY(1);
            });

            // Press Animation for Buttons
            btnModifier.setOnMousePressed(e -> {
                btnModifier.setStyle(btnStyle + "-fx-background-color: #1f618d;");
            });
            btnModifier.setOnMouseReleased(e -> {
                btnModifier.setStyle(btnStyle);
            });

            btnSupprimer.setOnMousePressed(e -> {
                btnSupprimer.setStyle(btnDeleteStyle + "-fx-background-color: #922b21;");
            });
            btnSupprimer.setOnMouseReleased(e -> {
                btnSupprimer.setStyle(btnDeleteStyle);
            });

            // Créer un VBox pour les informations de la commande et les boutons
            VBox infoBox = new VBox(5, lblId, lblUserId, lblMontant, lblEtat, lblDate);
            infoBox.setSpacing(8);
            infoBox.setPadding(new Insets(10, 0, 0, 0));

            // Créer un HBox pour les boutons Modifier et Supprimer
            HBox btnBox = new HBox(10, btnModifier, btnSupprimer);
            btnBox.setAlignment(Pos.CENTER_LEFT);
            btnBox.setPadding(new Insets(10, 0, 0, 0));

            // Création de la boîte de la commande avec un style personnalisé
            VBox commandeBox = new VBox(10, infoBox, btnBox);
            commandeBox.setStyle(
                    "-fx-padding: 15px;" +
                            "-fx-background-color: #ffffff;" +
                            "-fx-border-radius: 12px;" +
                            "-fx-border-color: #ecf0f1;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);"
            );

            // Ajout de l'élément HBox pour chaque commande
            HBox card = new HBox(commandeBox);
            card.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10px; -fx-margin: 10px;");
            listViewCommandes.getItems().add(card);
        }
    }

    @FXML
    private void modifierCommande(Commande commande, ActionEvent event) {
        try {
            // Charger le fichier FXML pour la fenêtre de modification de commande
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommande.fxml"));
            Parent root = loader.load();

            // Obtenez le contrôleur de la nouvelle fenêtre (ModifierCommande)
            ModifierCommande controller = loader.getController();

            // Passer l'objet commande à la méthode setCommande du contrôleur
            controller.setCommandeSelectionnee(commande);  // Modifié pour correspondre à la méthode de votre contrôleur

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre de modification.");
        }
    }

    private void supprimerCommande(Commande commande) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Suppression");
        confirmation.setHeaderText("Supprimer cette commande ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            commandeService.supprimer(commande.getId());
            commandes.remove(commande);
            afficherCommandes();

            showAlert("Succès", "Commande supprimée avec succès.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCommande.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listViewCommandes.getScene().getWindow();
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
