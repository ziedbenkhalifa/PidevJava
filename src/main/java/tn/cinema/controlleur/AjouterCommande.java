package tn.cinema.controlleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
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
        // Initialisation des options de la combo box
        cbEtat.getItems().addAll("en attente", "confirmée", "annulée");
        cbEtat.setValue("en attente"); // valeur par défaut
    }

    @FXML
    void ajouterCommande(ActionEvent event) {
        // Vérification si les champs ne sont pas vides
        if (tfUserId.getText().isEmpty() || tfMontantPaye.getText().isEmpty() || cbEtat.getValue() == null) {
            afficherAlerte("Champs manquants", "⚠️ Tous les champs doivent être remplis.");
            return;
        }

        // Vérification que l'ID utilisateur est un entier
        int userId;
        try {
            userId = Integer.parseInt(tfUserId.getText());
        } catch (NumberFormatException e) {
            afficherAlerte("ID utilisateur invalide", "⚠️ L'ID utilisateur doit être un nombre valide.");
            return;
        }

        // Vérification que le montant payé est un nombre valide
        double montantPaye;
        try {
            montantPaye = Double.parseDouble(tfMontantPaye.getText());
            if (montantPaye < 0) {
                afficherAlerte("Montant invalide", "⚠️ Le montant payé doit être un nombre positif.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Montant invalide", "⚠️ Le montant payé doit être un nombre valide.");
            return;
        }

        String etat = cbEtat.getValue();

        // Créer une nouvelle commande et l'ajouter au service
        Commande commande = new Commande(userId, montantPaye, etat);
        commande.setDateCommande(LocalDateTime.now());

        commandeService.ajouter(commande);

        // Réinitialiser les champs après l'ajout de la commande
        clearFields();
        afficherAlerte("Commande ajoutée", "✅ Commande ajoutée avec succès !");
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            // Charger la scène FXML qui affiche la liste des commandes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommande.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Remplacer le contenu de la scène actuelle par root

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
