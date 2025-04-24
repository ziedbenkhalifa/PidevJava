package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp {

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private DatePicker dateNaissance;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button photoButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Button backButton;

    private final UserService userService = new UserService();
    private String selectedPhotoPath = "";

    @FXML
    public void initialize() {
        // Populate ComboBox with allowed roles
        roleComboBox.getItems().addAll("Client", "Coach", "Sponsor");
        roleComboBox.setPromptText("Select Role");
    }

    @FXML
    private void photoPopUp(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        Stage stage = (Stage) photoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            selectedPhotoPath = "file:" + selectedFile.getAbsolutePath().replace("\\", "/");
            System.out.println("Selected photo: " + selectedPhotoPath);
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            User newUser = new User(
                    nomField.getText(),
                    dateNaissance.getValue(),
                    emailField.getText(),
                    roleComboBox.getValue(), // Use selected role
                    passwordField.getText(),
                    selectedPhotoPath.isEmpty() ? null : selectedPhotoPath,
                    "" // faceToken
            );

            userService.ajouter(newUser);
            showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);

            // Navigate back to Login
            handleBackAction(null);
        } catch (Exception e) {
            showAlert("Error", "Failed to create account: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
        } catch (IOException e) {
            showAlert("Error", "Failed to load Login.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        // Validate Name (No numbers)
        if (!nomField.getText().matches("^[a-zA-Z\\s]+$")) {
            errorMessage.append("Name must not contain numbers.\n");
        }

        // Validate Email
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(emailField.getText());
        if (!matcher.matches()) {
            errorMessage.append("Invalid email format.\n");
        }

        // Validate Password (At least 8 characters)
        if (passwordField.getText().length() < 8) {
            errorMessage.append("Password must be at least 8 characters.\n");
        }

        // Validate Date of Birth
        if (dateNaissance.getValue() == null) {
            errorMessage.append("Date of birth is required.\n");
        }

        // Validate Role
        if (roleComboBox.getValue() == null) {
            errorMessage.append("Please select a role.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}