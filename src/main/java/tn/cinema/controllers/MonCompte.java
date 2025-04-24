package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import tn.cinema.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MonCompte {

    @FXML
    private Label userNameLabel;

    @FXML
    private ListView<String> userInfoList;

    @FXML
    private Button editProfileButton;

    @FXML
    private ImageView userPhoto;

    @FXML
    private Button logoutButton;

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        displayUserInfo();
    }

    private void displayUserInfo() {
        if (loggedInUser != null) {
            userNameLabel.setText("Bienvenue, " + (loggedInUser.getNom() != null ? loggedInUser.getNom() : "N/A"));
            userInfoList.getItems().clear();
            userInfoList.getItems().add("Nom: " + (loggedInUser.getNom() != null ? loggedInUser.getNom() : "N/A"));
            userInfoList.getItems().add("Email: " + (loggedInUser.getEmail() != null ? loggedInUser.getEmail() : "N/A"));
            userInfoList.getItems().add("Date de Naissance: " +
                    (loggedInUser.getDateDeNaissance() != null
                            ? loggedInUser.getDateDeNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "N/A"));
            userInfoList.getItems().add("Rôle: " + (loggedInUser.getRole() != null ? loggedInUser.getRole() : "N/A"));

            String photoPath = loggedInUser.getPhoto();
            System.out.println("Attempting to load photo for user: " + loggedInUser.getNom());
            System.out.println("Photo path from database: " + photoPath);
            if (photoPath != null && !photoPath.trim().isEmpty()) {
                try {
                    String imagePath = photoPath;
                    if (photoPath.startsWith("http")) {
                        System.out.println("Treating as URL: " + photoPath);
                    } else if (photoPath.startsWith("file:")) {
                        System.out.println("Treating as file URL: " + photoPath);
                    } else {
                        File file = new File(photoPath);
                        if (file.exists()) {
                            imagePath = "file:" + file.getAbsolutePath().replace("\\", "/");
                            System.out.println("Converted to file URL: " + imagePath);
                        } else {
                            System.out.println("Local file does not exist: " + photoPath);
                            imagePath = "/images/userwhite.png";
                        }
                    }
                    Image image = new Image(imagePath, true);
                    if (image.isError()) {
                        System.out.println("Image loading failed: " + image.getException().getMessage());
                        userPhoto.setImage(new Image("/images/userwhite.png"));
                    } else {
                        userPhoto.setImage(image);
                        System.out.println("Image loaded successfully: " + imagePath);
                    }
                } catch (Exception e) {
                    System.out.println("Exception loading image: " + e.getMessage());
                    userPhoto.setImage(new Image("/images/userwhite.png"));
                }
            } else {
                System.out.println("Photo path is null or empty for user: " + loggedInUser.getNom());
                userPhoto.setImage(new Image("/images/userwhite.png"));
            }
        } else {
            System.out.println("Logged-in user is null");
            userPhoto.setImage(new Image("/images/userwhite.png"));
        }
    }

    @FXML
    private void handleEditProfile() {
        if (loggedInUser == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUser.fxml"));
            Parent root = loader.load();
            ModifierUser controller = loader.getController();
            controller.setUser(loggedInUser);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Profil");
            stage.showAndWait();

            if (loggedInUser.getId() != 0) {
                User updatedUser = new UserService().recuperer().stream()
                        .filter(u -> u.getId() == loggedInUser.getId())
                        .findFirst()
                        .orElse(loggedInUser);
                SessionManager.getInstance().setLoggedInUser(updatedUser);
                setLoggedInUser(updatedUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire de modification.");
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Connexion");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}