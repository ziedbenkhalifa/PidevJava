package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;
import java.io.IOException;
import tn.cinema.entities.User;
import tn.cinema.utils.SessionManager;

public class ModifierCommande {

    @FXML
    private TextField tfMontantPaye;
    @FXML
    private ComboBox<String> cbEtat;

    private CommandeService commandeService = new CommandeService();
    private Commande commandeSelectionnee;

    @FXML
    public void initialize() {
        // Initialisation de la ComboBox pour l'état
        cbEtat.setItems(FXCollections.observableArrayList("En attente", "Payée", "Annulée", "Livrée"));

        // Charger les informations de la commande à modifier si disponible
        if (commandeSelectionnee != null) {
            tfMontantPaye.setText(String.valueOf(commandeSelectionnee.getMontantPaye()));
            cbEtat.setValue(commandeSelectionnee.getEtat());
        }
    }

    // Méthode pour récupérer la commande sélectionnée
    public void setCommandeSelectionnee(Commande commande) {
        this.commandeSelectionnee = commande;
    }

    // Méthode pour modifier la commande
    @FXML
    private void modifierCommande() {
        try {
            // Vérifier que les champs sont remplis
            if (tfMontantPaye.getText().isEmpty() || cbEtat.getValue() == null) {
                showAlert(AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            // Valider que les données sont correctes
            double montantPaye = validateMontantPaye(tfMontantPaye.getText());
            if (montantPaye == -1) return;  // Montant invalide

            // Mise à jour de la commande
            commandeSelectionnee.setMontantPaye(montantPaye);
            commandeSelectionnee.setEtat(cbEtat.getValue());
            commandeService.modifier(commandeSelectionnee);

            // Envoi d’email si livrée
            if ("Livrée".equals(cbEtat.getValue())) {
                sendLivraisonEmail(commandeSelectionnee);
            }

            showAlert(AlertType.INFORMATION, "Succès", "La commande a été modifiée avec succès.");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de format", "Le format des données est incorrect : " + e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur inconnue", "Une erreur est survenue : " + e.getMessage());
        }
    }

    // Méthode pour valider le montant payé
    private double validateMontantPaye(String montantPayeText) {
        try {
            double montantPaye = Double.parseDouble(montantPayeText);
            if (montantPaye < 0) {
                showAlert(AlertType.ERROR, "Erreur", "Le montant payé ne peut pas être négatif.");
                return -1; // Retourner une valeur invalide pour signifier une erreur
            }
            return montantPaye;
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur", "Le montant payé doit être un nombre valide.");
            return -1; // Retourner une valeur invalide pour signifier une erreur
        }
    }

    // Méthode pour envoyer un email de confirmation de livraison
// Méthode pour envoyer un email de confirmation de livraison
    private void sendLivraisonEmail(Commande commande) {
        // Récupérer l'utilisateur connecté
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            showAlert(AlertType.WARNING, "Email non envoyé", "Aucun utilisateur connecté, impossible d'envoyer l'email.");
            return;
        }

        String recipientEmail = loggedInUser.getEmail();  // récupère automatiquement
        String subject = "Votre commande a été livrée";
        String messageBody = "Bonjour " + loggedInUser.getNom() + ",\n\n" +
                "Votre commande n°" + commande.getId() + " a bien été livrée.\n" +
                "Merci de votre confiance !\n\n" +
                "Cordialement,\nL’équipe Showtime";

        boolean emailEnvoye = CommandeService.envoyerEmailConfirmation(recipientEmail, subject, messageBody);
        if (emailEnvoye) {
            showAlert(AlertType.INFORMATION, "Email envoyé", "Un email de confirmation a été envoyé à : " + recipientEmail);
        } else {
            showAlert(AlertType.ERROR, "Erreur d'email", "L'envoi de l'email a échoué. Veuillez vérifier votre configuration SMTP.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page des commandes.");
        }
    }

    // Méthode pour réinitialiser les champs
    private void clearFields() {
        tfMontantPaye.clear();
        cbEtat.setValue("En attente");
    }
}