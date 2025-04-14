package tn.cinema.controllers;

import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import tn.cinema.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Login {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;

    private final UserService userService = new UserService();

    @FXML
    private void handleLoginAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Email and password cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        User user = userService.recuperer().stream()
                .filter(u -> u.getEmail().equals(email) && u.getMotDePasse().equals(password))
                .findFirst().orElse(null);

        if (user != null) {
            SessionManager.getInstance().setLoggedInUser(user); // Store user in SessionManager
            showAlert("Login Successful", "Welcome, " + user.getNom(), Alert.AlertType.INFORMATION);
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                loadAdminScreen();
            } else {
                loadFrontzScreen();
            }
        } else {
            showAlert("Login Failed", "Invalid email or password.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSignUpAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.setScene(new Scene(pane));
            stage.setTitle("Sign Up");
        } catch (Exception e) {
            showAlert("Error", "Failed to load SignUp.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAdminScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(pane));
        } catch (Exception e) {
            showAlert("Error", "Failed to load AfficherUser.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadFrontzScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontZ.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(pane));
        } catch (Exception e) {
            showAlert("Error", "Failed to load FrontZ.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}