package tn.cinema.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.cinema.services.ChatBotService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tn.cinema.services.ChatBotServices;

import java.io.IOException;

public class ChatBotControllers {

    @FXML
    private TextField inputField; // Champ de saisie utilisateur

    @FXML
    private TextArea outputArea; // Zone d'affichage des réponses

    private ChatBotServices chatBotServices;

    // Initialisation du service ChatBot
    public void initialize() {
        chatBotServices = new ChatBotServices();
    }


    // Méthode pour traiter la commande de l'utilisateur
    @FXML
    private void handleUserInput() {
        String userInput = inputField.getText();
        if (userInput != null && !userInput.trim().isEmpty()) {
            String response = chatBotServices.processCommand(userInput);
            outputArea.setText(response);
            inputField.clear();
        } else {
            outputArea.setText("Veuillez entrer une commande valide.");
        }
    }
}