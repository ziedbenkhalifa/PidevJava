package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AjouterUser implements Initializable {

    @FXML
    private DatePicker DateNaissance;

    @FXML
    private TextField Email;

    @FXML
    private TextField MotDePasse;

    @FXML
    private TextField Nom;

    @FXML
    private ComboBox<String> Role;

    private UserService us = new UserService();

    private String selectedPhotoPath; // To store the path of the selected photo

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the Role ComboBox with values
        Role.getItems().addAll("Client", "Admin", "Coach", "Sponsor");
        selectedPhotoPath = ""; // Initialize photo path as empty
    }

    @FXML
    void PhotoPopUp(ActionEvent event) {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");

        // Set extension filters to allow only image files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        // Get the stage from the event source
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // Show the file chooser dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Store the file path
            selectedPhotoPath = selectedFile.getAbsolutePath();
            // Optionally, you can display the file name or path to the user
            System.out.println("Photo sélectionnée : " + selectedPhotoPath);
        }
    }

    @FXML
    void Sinscrire(ActionEvent event) {
        // Validate input before creating the user
        if (!validateForm()) {
            return; // Stop execution if validation fails
        }

        try {
            // Create a new User object with form data
            User newUser = new User(
                    Nom.getText(),
                    DateNaissance.getValue(),
                    Email.getText(),
                    Role.getValue(),
                    MotDePasse.getText(),
                    selectedPhotoPath, // Use the selected photo path
                    "" // faceToken - empty for now
            );

            // Add the user using UserService
            us.ajouter(newUser);

            // Clear the form after successful addition
            clearForm();

        } catch (Exception e) {
            System.out.println("Erreur lors de l'inscription: " + e.getMessage());
            // Optionally, show an alert to the user
        }
    }

    private boolean validateForm() {
        String errorMessage = "";

        // Validate Name (No numbers allowed)
        if (!Nom.getText().matches("^[a-zA-Z\\s]+$")) {
            errorMessage += "Le nom ne doit pas contenir de chiffres.\n";
        }

        // Validate Password (At least 8 characters)
        if (MotDePasse.getText().length() < 8) {
            errorMessage += "Le mot de passe doit contenir au moins 8 caractères.\n";
        }

        // Validate Email (Must be a valid email format)
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(Email.getText());
        if (!matcher.matches()) {
            errorMessage += "L'email n'est pas valide.\n";
        }

        // Show error message if there are validation issues
        if (!errorMessage.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void clearForm() {
        Nom.clear();
        DateNaissance.setValue(null);
        Email.clear();
        Role.setValue(null);
        MotDePasse.clear();
        selectedPhotoPath = ""; // Reset photo path
    }
}
