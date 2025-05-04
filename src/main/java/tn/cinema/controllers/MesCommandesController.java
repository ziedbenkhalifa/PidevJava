package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;
import javafx.scene.control.ButtonType;
import javafx.animation.ScaleTransition;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;


public class MesCommandesController {

    @FXML
    private ListView<HBox> listViewCommandes;

    @FXML
    private Button btnValider;

    private final CommandeService commandeService = new CommandeService();
    private final ObservableList<Commande> commandes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Charger les commandes depuis le service
        commandes.addAll(commandeService.recupererCommandesUtilisateurConnecte());
        afficherCommandes();

        btnValider.setOnMouseClicked(this::validerCommande);
    }

    private void afficherCommandes() {
        listViewCommandes.getItems().clear();

        for (Commande c : commandes) {
            // Labels d'informations
            Label lblId      = new Label("Commande n°" + c.getId());
            Label lblDate    = new Label("Date : " + c.getDateCommande());
            Label lblMontant = new Label("Montant : " + c.getMontantPaye() + " DT");
            Label lblEtat    = new Label("État : " + c.getEtat());

            lblId     .setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            lblDate   .setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            lblMontant.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60;");
            lblEtat   .setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");

            VBox infoBox = new VBox(5, lblId, lblDate, lblMontant, lblEtat);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            Button btnRemove = new Button("🗑️");
            btnRemove.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-size: 20px; " +
                            "-fx-text-fill: #e74c3c;" // couleur rouge pour le bouton supprimer
            );

// Animation pour l'effet de survol
            btnRemove.setOnMouseEntered(e -> {
                btnRemove.setStyle("-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 20px; " +
                        "-fx-text-fill: #c0392b;"); // couleur rouge plus foncée sur survol
            });

// Animation pour l'effet de quitter le survol
            btnRemove.setOnMouseExited(e -> {
                btnRemove.setStyle("-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 20px; " +
                        "-fx-text-fill: #e74c3c;");
            });

// Ajouter une transition d'animation (agrandir et rétrécir lors du clic)
            btnRemove.setOnAction(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), btnRemove);
                scaleTransition.setByX(1.2);
                scaleTransition.setByY(1.2);
                scaleTransition.setCycleCount(2);
                scaleTransition.setAutoReverse(true);
                scaleTransition.play();

                boolean confirmed = showConfirmation("Supprimer la commande",
                        "Voulez-vous vraiment supprimer la commande n°" + c.getId() + " ?");

                if (confirmed) {
                    commandeService.supprimer(c.getId());
                    commandes.remove(c);
                    afficherCommandes();
                }
            });

// Ajout d'une ombre portée pour améliorer le design
            DropShadow dropShadow = new DropShadow();
            dropShadow.setOffsetX(4);
            dropShadow.setOffsetY(4);
            dropShadow.setColor(javafx.scene.paint.Color.GRAY);
            btnRemove.setEffect(dropShadow);

            HBox hbox = new HBox(20, infoBox, btnRemove);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setPadding(new Insets(10));
            hbox.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-background-radius: 10; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-color: #dcdde1; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
            );

            HBox.setMargin(btnRemove, new Insets(0, 0, 0, 20));
            listViewCommandes.getItems().add(hbox);
        }
    }

    private void validerCommande(MouseEvent event) {
        if (commandes.isEmpty()) {
            showAlert("Votre panier est vide !", AlertType.INFORMATION);
            return;
        }

        // Appel de la validation
        showAlert("Commande validée avec succès ✅", AlertType.INFORMATION);

        // Nettoyer la liste après validation
        commandes.clear();
        listViewCommandes.getItems().clear();
    }

    private void showAlert(String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Wait for the user to respond to the confirmation
        alert.showAndWait();

        // Return true if the user clicked "OK" (confirmed the action)
        return alert.getResult() == ButtonType.OK;
    }
}