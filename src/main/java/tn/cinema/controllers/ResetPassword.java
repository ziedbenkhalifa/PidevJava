package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;

public class ResetPassword {

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    private final UserService userService = new UserService();
    private String email; // Email passé via le lien

    // Méthode pour initialiser l'email (à appeler lors du chargement)
    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void handleUpdatePasswordAction(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation des champs
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Validation Error", "Both password fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        if (newPassword.length() < 8) {
            showAlert("Validation Error", "Password must be at least 8 characters long.", Alert.AlertType.ERROR);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Validation Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier si l'utilisateur existe
        User user;
        try {
            user = userService.recuperer().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve user: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        if (user == null) {
            showAlert("Error", "User not found.", Alert.AlertType.ERROR);
            return;
        }

        // Debugging: Vérifier les informations de l'utilisateur
        System.out.println("User ID before update: " + user.getId());
        System.out.println("User email: " + user.getEmail());
        System.out.println("New password to set: " + newPassword);

        // Mettre à jour le mot de passe dans l'objet utilisateur
        user.setMotDePasse(newPassword);

        try {
            // Appeler la méthode modifier avec les paramètres explicites
            userService.modifier(
                    user.getId(),
                    user.getNom(),
                    user.getDateDeNaissance(),
                    user.getEmail(),
                    user.getRole(),
                    user.getMotDePasse(),
                    user.getPhoto(),
                    user.getFaceToken()
            );
            showAlert("Success", "Password updated successfully!", Alert.AlertType.INFORMATION);

            // Rediriger vers l'écran de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) newPasswordField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
        } catch (Exception e) {
            showAlert("Error", "Failed to update password: " + e.getMessage(), Alert.AlertType.ERROR);
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