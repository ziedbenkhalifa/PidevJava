package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class ForgetPassword {

    @FXML
    private TextField emailField;
    @FXML
    private TextField codeField;

    private final UserService userService = new UserService();
    private String emailForReset;
    private String generatedCode;
    private long codeGenerationTime; // Timestamp de génération du code

    private static final long CODE_EXPIRATION_MINUTES = 10; // Durée de validité en minutes

    @FXML
    private void handleSendResetCodeAction(ActionEvent event) {
        String email = emailField.getText().trim();

        // Validation de l'email
        if (email.isEmpty()) {
            showAlert("Validation Error", "Email cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Validation Error", "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier si l'utilisateur existe
        User user;
        try {
            user = userService.recuperer().stream()
                    .filter(u -> u.getEmail().equals(email))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve user: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        if (user == null) {
            showAlert("Error", "No user found with this email address.", Alert.AlertType.ERROR);
            return;
        }

        // Stocker l'email
        this.emailForReset = email;

        // Générer un code aléatoire et enregistrer le timestamp
        this.generatedCode = generateRandomCode();
        this.codeGenerationTime = System.currentTimeMillis();

        // Envoyer l'email avec le code
        boolean emailSent = sendResetEmail(email, generatedCode);

        if (emailSent) {
            showAlert("Success", "A reset code has been sent to your email. The code is valid for " + CODE_EXPIRATION_MINUTES + " minutes.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to send reset code. Please try again later.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVerifyCodeAction(ActionEvent event) {
        String enteredCode = codeField.getText().trim();

        if (enteredCode.isEmpty()) {
            showAlert("Validation Error", "Please enter the reset code.", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier l'expiration du code
        long currentTime = System.currentTimeMillis();
        long elapsedMinutes = (currentTime - codeGenerationTime) / (1000 * 60);

        if (elapsedMinutes > CODE_EXPIRATION_MINUTES) {
            showAlert("Error", "The reset code has expired. Please request a new code.", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier le code
        if (!enteredCode.equals(generatedCode)) {
            showAlert("Error", "Invalid reset code.", Alert.AlertType.ERROR);
            return;
        }

        // Si le code est correct et non expiré, ouvrir la fenêtre de réinitialisation
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Scene resetPasswordScene = new Scene(loader.load());
            ResetPassword controller = loader.getController();
            controller.setEmail(emailForReset);
            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.setScene(resetPasswordScene);
            stage.setTitle("Set New Password");
        } catch (IOException e) {
            showAlert("Error", "Failed to load ResetPassword.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
        } catch (IOException e) {
            showAlert("Error", "Failed to load Login.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Génère un code à 6 chiffres
        return String.valueOf(code);
    }

    private boolean sendResetEmail(String toEmail, String code) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        final String fromEmail = "alizouaoui111@gmail.com"; // Remplace par ton email
        final String password = "amrvdcyicsmxyzrr"; // Remplace par ton mot de passe d'application

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Code");
            message.setText("Dear user,\n\n" +
                    "You have requested to reset your password. Use the following code to proceed:\n\n" +
                    "Reset Code: " + code + "\n\n" +
                    "This code is valid for " + CODE_EXPIRATION_MINUTES + " minutes.\n" +
                    "Enter this code in the application to reset your password.\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Best regards,\nCinema App Team");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}