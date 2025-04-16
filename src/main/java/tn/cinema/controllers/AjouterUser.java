package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import tn.cinema.utils.SessionManager;

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




    @FXML
    private Button gestionPubButton;

    @FXML
    private void goToInterfaceChoixGP() {
        try {
            // Load InterfaceChoixGP.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceChoixGP.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gestionPubButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion Publicités");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceChoixGP.fxml: " + e.getMessage());
        }
    }

    @FXML
    private Button gererDemandesButton;
    public void goToInterfaceDemandes(ActionEvent actionEvent) {
        try {
            // Load InterfaceDemandes.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceDemandes.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gererDemandesButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceDemandes.fxml: " + e.getMessage());
        }
    }
    @FXML
    private Button gererPublictesButton;
    public void goToInterfacePublictes(ActionEvent actionEvent) {
        try {
            // Load InterfaceDemandes.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfacePublicites.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gererDemandesButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceDemandes.fxml: " + e.getMessage());
        }
    }

    @FXML
    private Button backButtonn;
    public void goBackToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
        }
    }






    /// aliiiiiiiiii


    @FXML
    private Button gestionUserButton;

    @FXML
    private Button monCompteButtonn;

    @FXML
    private void handleGestionUserAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gestionUserButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger AfficherUser.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleMonCompteActionn() {
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger MonCompte.fxml: " + e.getMessage());
        }
    }
    private void showAlertt(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    void gestionFilm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionFilms.fxml")); // Adjust path if needed
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();   // Get current window
            Scene scene = new Scene(root);
            stage.setScene(scene); // Set the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debug
        }
    }

    @FXML
    private Button btnAjouterEquipement;
    @FXML
    private void ouvrirInterfaceAjout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeSalle.fxml"));
            Parent root = loader.load();
            btnAjouterEquipement.getScene().setRoot(root); // Remplacer la scène actuelle
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur de chargement");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible d'ouvrir l'interface Ajouter Équipement.");
            errorAlert.showAndWait();
        }
    }



    @FXML
    void affichage(ActionEvent event) {
        try {
            // Charger la scène FXML qui affiche la liste des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChoixG.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Remplacer le contenu de la scène actuelle par root

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des produits.");
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleGestinCour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCour.fxml"));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}