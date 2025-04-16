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
            Label lblId = new Label("Commande n°" + c.getId());
            Label lblDate = new Label("Date : " + c.getDateCommande());
            Label lblMontant = new Label("Montant : " + c.getMontantPaye() + " DT");
            Label lblEtat = new Label("État : " + c.getEtat());

            lblId.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            lblDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            lblMontant.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60;");
            lblEtat.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");

            VBox infoBox = new VBox(5, lblId, lblDate, lblMontant, lblEtat);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            HBox commandeBox = new HBox(20, infoBox);
            commandeBox.setPadding(new Insets(10));
            commandeBox.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-background-radius: 10; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-color: #dcdde1; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
            );

            listViewCommandes.getItems().add(commandeBox);
        }
    }

    private void validerCommande(MouseEvent event) {
        if (commandes.isEmpty()) {
            showAlert("Votre panier est vide !", AlertType.INFORMATION);
            return;
        }

        // Tu peux appeler ici la méthode de validation dans CommandeService
        showAlert("Commande validée avec succès ✅", AlertType.INFORMATION);

        // Nettoyer la liste après validation (tu peux aussi recharger depuis la BDD)
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
}
