package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.cinema.services.ProduitService;

public class SMSPromotion {

    @FXML private TextField phoneField;
    @FXML private TextArea messageArea;
    @FXML private Button sendButton;

    @FXML
    public void initialize() {
        // Message par défaut pour les promotions
        messageArea.setText("🎉 Découvrez nos nouveaux produits en promotion dès aujourd'hui ! Profitez de tarifs exceptionnels.");

        sendButton.setOnAction(e -> sendPromotionSMS());
    }

    private void sendPromotionSMS() {
        String phone = phoneField.getText().trim();
        String msg   = messageArea.getText().trim();

        if (phone.isEmpty() || msg.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez saisir le numéro et le message.");
            return;
        }

        // Envoi du SMS
        ProduitService.envoyerSMS(phone, msg);

        // Afficher une alerte de succès
        showAlert(Alert.AlertType.INFORMATION, "Succès", "SMS envoyé à " + phone + " !");

        // Fermer la fenêtre actuelle
        closeWindow();
    }

    private void closeWindow() {
        // Récupérer le stage actuel (la fenêtre)
        Stage stage = (Stage) sendButton.getScene().getWindow();
        // Fermer la fenêtre
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}