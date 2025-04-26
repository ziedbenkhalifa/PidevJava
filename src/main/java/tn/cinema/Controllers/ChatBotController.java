package tn.cinema.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.cinema.services.ChatBotService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ChatBotController {

    @FXML
    private TextField inputField; // Champ de saisie utilisateur

    @FXML
    private TextArea outputArea; // Zone d'affichage des réponses

    private ChatBotService chatBotService;

    // Initialisation du service ChatBot
    public void initialize() {
        chatBotService = new ChatBotService();
    }


    // Méthode pour traiter la commande de l'utilisateur
    @FXML
    private void handleUserInput() {
        String userInput = inputField.getText();
        if (userInput != null && !userInput.trim().isEmpty()) {
            String response = chatBotService.processCommand(userInput);
            outputArea.setText(response);
            inputField.clear();
        } else {
            outputArea.setText("Veuillez entrer une commande valide.");
        }
    }
}
