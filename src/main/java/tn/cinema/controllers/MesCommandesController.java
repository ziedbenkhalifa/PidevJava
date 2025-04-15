package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;

import java.io.IOException;

public class MesCommandesController {

    @FXML
    private ListView<HBox> listViewCommandes;

    private CommandeService commandeService = new CommandeService();
    private ObservableList<Commande> commandes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Récupérer les commandes de l'utilisateur connecté
        commandes.addAll(commandeService.recupererCommandesUtilisateurConnecte());
        afficherCommandes();
    }

    private void afficherCommandes() {
        listViewCommandes.getItems().clear();

        for (Commande c : commandes) {
            // Labels pour afficher les détails de la commande
            Label lblId = new Label("ID: " + c.getId());
            Label lblDate = new Label("Date: " + c.getDateCommande().toString());
            Label lblMontant = new Label("Montant: " + c.getMontantPaye() + " DT");
            Label lblEtat = new Label("État: " + c.getEtat());

            lblId.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            lblDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            lblMontant.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60;");
            lblEtat.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");

            VBox infoBox = new VBox(5, lblId, lblDate, lblMontant, lblEtat);
            infoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            HBox commandeBox = new HBox(20, infoBox);
            commandeBox.setPadding(new javafx.geometry.Insets(10));
            commandeBox.setStyle(
                    "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #dfe6e9;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
            );

            listViewCommandes.getItems().add(commandeBox);
        }
    }


}