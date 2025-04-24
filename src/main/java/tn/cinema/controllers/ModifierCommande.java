package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;

import java.io.IOException;

public class ModifierCommande extends Dashboard {

    @FXML
    private TextField tfUserId;
    @FXML
    private TextField tfMontantPaye;
    @FXML
    private ComboBox<String> cbEtat;

    private CommandeService commandeService = new CommandeService();
    private Commande commandeSelectionnee;

    @FXML
    public void initialize() {
        // Initialisation de la ComboBox pour l'état
        cbEtat.setItems(FXCollections.observableArrayList("En attente", "Payée", "Annulée"));

        // Charger les informations de la commande à modifier si disponible
        if (commandeSelectionnee != null) {
            tfUserId.setText(String.valueOf(commandeSelectionnee.getUserId()));
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
            if (commandeSelectionnee != null) {
                // Vérifier que les champs sont remplis
                if (tfUserId.getText().isEmpty() || tfMontantPaye.getText().isEmpty() || cbEtat.getValue() == null) {
                    showAlert(AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs.");
                    return;
                }

                // Valider que les données sont correctes
                int userId;
                try {
                    userId = Integer.parseInt(tfUserId.getText());
                } catch (NumberFormatException e) {
                    showAlert(AlertType.ERROR, "Erreur", "L'ID utilisateur doit être un nombre valide.");
                    return;
                }

                double montantPaye;
                try {
                    montantPaye = Double.parseDouble(tfMontantPaye.getText());
                    if (montantPaye < 0) {
                        showAlert(AlertType.ERROR, "Erreur", "Le montant payé ne peut pas être négatif.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert(AlertType.ERROR, "Erreur", "Le montant payé doit être un nombre valide.");
                    return;
                }

                // Mettre à jour les informations de la commande
                commandeSelectionnee.setUserId(userId);
                commandeSelectionnee.setMontantPaye(montantPaye);
                commandeSelectionnee.setEtat(cbEtat.getValue());

                // Mettre à jour la commande dans le service
                commandeService.modifier(commandeSelectionnee);

                showAlert(AlertType.INFORMATION, "Succès", "La commande a été modifiée avec succès.");
            } else {
                showAlert(AlertType.WARNING, "Erreur", "Commande non trouvée.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
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

    private void clearFields() {
        tfUserId.clear();
        tfMontantPaye.clear();
        cbEtat.setValue("En attente");
    }
}
