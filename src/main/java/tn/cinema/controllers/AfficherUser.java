package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherUser implements Initializable {

    @FXML
    private ListView<User> listView; // Replaces TableView

    @FXML
    private Button modifierButton;

    @FXML
    private Button deleteButton;

    private UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load users into the ListView
        loadUsers();

        // Set up modifier button action
        modifierButton.setOnAction(event -> handleModifierButtonClick());

        // Set up delete button action
        deleteButton.setOnAction(event -> handleDeleteButtonClick());
    }

    @FXML
    private void handleAjouterUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUser.fxml"));
            Parent root = loader.load();

            // Get the current stage and close it
            Stage currentStage = (Stage) listView.getScene().getWindow();
            currentStage.close();

            // Open the new AjouterUser stage
            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire d'ajout.");
        }
    }


    private void loadUsers() {
        try {
            List<User> users = userService.recuperer();
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
                        HBox row = new HBox(10);
                        row.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-alignment: CENTER_LEFT;");
                        row.setPrefHeight(60);

                        // Remove PHOTO logic, no ImageView setup anymore

                        // INFOS UTILISATEUR
                        Label nom = createLabel(user.getNom(), 150);
                        Label email = createLabel(user.getEmail(), 200);
                        Label mdp = createLabel(user.getMotDePasse(), 150);
                        Label naissance = createLabel(
                                user.getDateDeNaissance() != null
                                        ? user.getDateDeNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        : "N/A",
                                150
                        );
                        Label role = createLabel(user.getRole(), 100);

                        row.getChildren().addAll(nom, email, mdp, naissance, role);
                        setGraphic(row);
                    }
                }

                private Label createLabel(String text, double width) {
                    Label label = new Label(text != null ? text : "N/A");
                    label.setPrefWidth(width);
                    label.setWrapText(true);
                    return label;
                }
            });
        } catch (Exception e) {
            showAlert("Erreur", "Échec du chargement des utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshList() {
        loadUsers();
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
            // Reload users after edit
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire de modification.");
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