package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AjouterUser extends BaseController implements Initializable {

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

    @FXML
    private Button monCompteButton;

    private UserService us = new UserService();

    private String selectedPhotoPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Role.getItems().addAll("Client", "Admin", "Coach", "Sponsor");
        selectedPhotoPath = "";
    }

    @FXML
    void PhotoPopUp(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();
            System.out.println("Photo sélectionnée : " + selectedPhotoPath);
        }
    }

    @FXML
    void Sinscrire(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        try {
            User newUser = new User(
                    Nom.getText(),
                    DateNaissance.getValue(),
                    Email.getText(),
                    Role.getValue(),
                    MotDePasse.getText(),
                    selectedPhotoPath,
                    ""
            );
            us.ajouter(newUser);
            clearForm();
            showAlert("Succès", "Utilisateur ajouté avec succès.");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'inscription: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
        }
    }

    @FXML
    private void handleMonCompteAction() {
        Stage stage = (Stage) monCompteButton.getScene().getWindow();
        super.handleMonCompteAction(stage);
    }

    private boolean validateForm() {
        String errorMessage = "";
        if (!Nom.getText().matches("^[a-zA-Z\\s]+$")) {
            errorMessage += "Le nom ne doit pas contenir de chiffres.\n";
        }
        if (MotDePasse.getText().length() < 8) {
            errorMessage += "Le mot de passe doit contenir au moins 8 caractères.\n";
        }
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(Email.getText());
        if (!matcher.matches()) {
            errorMessage += "L'email n'est pas valide.\n";
        }
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

    @FXML
    private Button backButton;

    @FXML
    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Scene afficherUserScene = new Scene(loader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(afficherUserScene);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement d'AfficherUser.fxml.");
        }
    }

    private void clearForm() {
        Nom.clear();
        DateNaissance.setValue(null);
        Email.clear();
        Role.setValue(null);
        MotDePasse.clear();
        selectedPhotoPath = "";
    }
}