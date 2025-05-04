package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tn.cinema.services.ChatBotService;
import javafx.application.Platform;

public class ChatBotController {

    @FXML
    private TextField userInputField;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea chatArea;

    private ChatBotService chatbotService;

    @FXML
    public void initialize() {
        chatbotService = new ChatBotService(); // Initialisation du service du chatbot

        sendButton.setOnAction(event -> handleSendMessage());
    }

    private void handleSendMessage() {
        String message = userInputField.getText().trim();
        if (!message.isEmpty()) {
            // Message de l'utilisateur, animation ajoutée
            chatArea.appendText("Vous: " + message + "\n");
            chatArea.setStyle("-fx-background-color: #4CAF50; color: white;");
            chatArea.appendText("\n");

            new Thread(() -> {
                String response = chatbotService.askQuestion(message);
                Platform.runLater(() -> {
                    // Message du bot, animation ajoutée
                    chatArea.appendText("Bot: " + response + "\n");
                    chatArea.setStyle("-fx-background-color: #e4e6eb; color: #333;");
                });
            }).start();

            userInputField.clear();
        }
    }
}