package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;

import java.io.IOException;
import java.time.LocalDateTime;

public class AjouterCommande {

    @FXML
    private TextField tfUserId;

    @FXML
    private TextField tfMontantPaye;

    @FXML
    private ComboBox<String> cbEtat;

    @FXML
    private Button btnAjouter;

    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        cbEtat.getItems().addAll("en attente", "confirmée", "annulée");
        cbEtat.setValue("en attente");
    }

    @FXML
    void ajouterCommande(ActionEvent event) {
        String userIdText = tfUserId.getText().trim();
        String montantText = tfMontantPaye.getText().trim();
        String etat = cbEtat.getValue();

        // Vérification des champs vides
        if (userIdText.isEmpty() || montantText.isEmpty() || etat == null || etat.isEmpty()) {
            afficherAlerte("Champs manquants", "⚠️ Tous les champs doivent être remplis.");
            return;
        }

        // Validation de l'ID utilisateur
        int userId;
        if (!userIdText.matches("\\d+")) {
            afficherAlerte("ID utilisateur invalide", "⚠️ L'ID utilisateur doit être un nombre entier positif.");
            return;
        } else {
            userId = Integer.parseInt(userIdText);
            if (userId <= 0) {
                afficherAlerte("ID utilisateur invalide", "⚠️ L'ID utilisateur doit être supérieur à 0.");
                return;
            }
        }

        // Validation du montant
        double montantPaye;
        try {
            montantPaye = Double.parseDouble(montantText);
            if (montantPaye <= 0) {
                afficherAlerte("Montant invalide", "⚠️ Le montant payé doit être strictement positif.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Montant invalide", "⚠️ Le montant payé doit être un nombre valide.");
            return;
        }

        // Vérification de l’état sélectionné
        if (!cbEtat.getItems().contains(etat)) {
            afficherAlerte("État invalide", "⚠️ Veuillez sélectionner un état valide.");
            return;
        }

        // Création et ajout de la commande
        Commande commande = new Commande(userId, montantPaye, etat);
        commande.setDateCommande(LocalDateTime.now());

        commandeService.ajouter(commande);

        clearFields();
        afficherAlerte("Commande ajoutée", "✅ Commande ajoutée avec succès !");
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommande.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des commandes.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        tfUserId.clear();
        tfMontantPaye.clear();
        cbEtat.setValue("en attente");
    }
}