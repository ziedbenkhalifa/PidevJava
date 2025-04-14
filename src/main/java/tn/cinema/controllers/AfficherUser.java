package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import tn.cinema.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherUser {

    @FXML
    private ListView<User> listView;

    @FXML
    private Button modifierButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button monCompteButton;

    @FXML
    private Button backToDashboardButton;

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        loadUsers();
        modifierButton.setOnAction(event -> handleModifierButtonClick());
        deleteButton.setOnAction(event -> handleDeleteButtonClick());
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backToDashboardButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger Dashboard.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleMonCompteAction() {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MonCompte.fxml"));
            Parent root = loader.load();
            MonCompte controller = loader.getController();
            controller.setLoggedInUser(SessionManager.getInstance().getLoggedInUser());
            Stage stage = (Stage) monCompteButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Compte");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page Mon Compte: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouterUser() {
        try {
            System.out.println("Attempting to load AjouterUser.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un utilisateur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger AjouterUser.fxml: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            List<User> users = userService.recuperer();
            System.out.println("Fetched " + users.size() + " users from database");
            if (users.isEmpty()) {
                showAlert("Information", "Aucun utilisateur trouvé dans la base de données.");
            }
            ObservableList<User> observableList = FXCollections.observableArrayList(users);
            listView.setItems(observableList);

            listView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        System.out.println("Rendering user: " + user.getNom());
                        HBox card = new HBox(15);
                        card.setStyle("-fx-background-color: #1a2a44; -fx-padding: 15; -fx-background-radius: 10; -fx-margin: 5;");
                        card.setPrefWidth(1000);

                        ImageView photoView = new ImageView();
                        photoView.setFitHeight(50);
                        photoView.setFitWidth(50);
                        photoView.setPreserveRatio(true);
                        photoView.setStyle("-fx-background-radius: 25; -fx-border-radius: 25; -fx-border-color: #162c4f; -fx-border-width: 2;");

                        String photoPath = user.getPhoto();
                        if (photoPath != null && !photoPath.trim().isEmpty()) {
                            try {
                                String imagePath = photoPath;
                                if (!photoPath.startsWith("http") && !photoPath.startsWith("file:")) {
                                    File file = new File(photoPath);
                                    if (file.exists()) {
                                        imagePath = "file:" + file.getAbsolutePath().replace("\\", "/");
                                        System.out.println("Using photo path for " + user.getNom() + ": " + imagePath);
                                    } else {
                                        System.out.println("Photo file does not exist for user " + user.getNom() + ": " + photoPath);
                                        imagePath = "/images/userwhite.png";
                                    }
                                }
                                Image image = new Image(imagePath, true);
                                if (image.isError()) {
                                    System.out.println("Image loading error for user " + user.getNom() + ": " + image.getException().getMessage());
                                    photoView.setImage(new Image("/images/userwhite.png"));
                                } else {
                                    photoView.setImage(image);
                                    System.out.println("Photo loaded successfully for " + user.getNom());
                                }
                            } catch (Exception e) {
                                System.out.println("Exception loading photo for user " + user.getNom() + ": " + e.getMessage());
                                photoView.setImage(new Image("/images/userwhite.png"));
                            }
                        } else {
                            System.out.println("Photo path is null or empty for user: " + user.getNom());
                            photoView.setImage(new Image("/images/userwhite.png"));
                        }

                        VBox infoBox = new VBox(5);
                        infoBox.setPrefWidth(900);

                        Label nomLabel = new Label("Nom: " + (user.getNom() != null ? user.getNom() : "N/A"));
                        nomLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

                        Label emailLabel = new Label("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
                        emailLabel.setStyle("-fx-text-fill: #d7d7d9; -fx-font-size: 14;");

                        Label mdpLabel = new Label("Mot de passe: " + (user.getMotDePasse() != null ? user.getMotDePasse() : "N/A"));
                        mdpLabel.setStyle("-fx-text-fill: #d7d7d9; -fx-font-size: 14;");

                        Label naissanceLabel = new Label("Date de Naissance: " +
                                (user.getDateDeNaissance() != null
                                        ? user.getDateDeNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        : "N/A"));
                        naissanceLabel.setStyle("-fx-text-fill: #d7d7d9; -fx-font-size: 14;");

                        Label roleLabel = new Label("Rôle: " + (user.getRole() != null ? user.getRole() : "N/A"));
                        roleLabel.setStyle("-fx-text-fill: #d7d7d9; -fx-font-size: 14;");

                        infoBox.getChildren().addAll(nomLabel, emailLabel, mdpLabel, naissanceLabel, roleLabel);
                        card.getChildren().addAll(photoView, infoBox);
                        setGraphic(card);
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Exception in loadUsers: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Échec du chargement des utilisateurs : " + e.getMessage());
        }
    }

    private void handleModifierButtonClick() {
        User selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à modifier.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUser.fxml"));
            Parent root = loader.load();
            ModifierUser controller = loader.getController();
            controller.setUser(selectedUser);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Utilisateur");
            stage.showAndWait();
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire de modification: " + e.getMessage());
        }
    }

    private void handleDeleteButtonClick() {
        User selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + selectedUser.getNom() + " ?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.supprimer(selectedUser.getId());
                    loadUsers();
                    showAlert("Succès", "Utilisateur supprimé avec succès.");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}