package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

public class ModifierUser {

    @FXML
    private TextField Nom; // Matches fx:id="Nom" in FXML

    @FXML
    private TextField Email; // Matches fx:id="Email" in FXML

    @FXML
    private PasswordField MotDePasse; // Matches fx:id="MotDePasse" in FXML

    @FXML
    private DatePicker DateNaissance; // Matches fx:id="DateNaissance" in FXML

    @FXML
    private ComboBox<String> Role; // Matches fx:id="Role" in FXML

    @FXML
    private Button PhotoButton; // Matches fx:id="PhotoButton" in FXML

    private User user;

    private final UserService userService = new UserService();

    private String selectedPhotoPath = ""; // new photo path if selected

    // Initialize the ComboBox with the same possible role values as in AjouterUser
    @FXML
    public void initialize() {
        // Populate the Role ComboBox with the same values as in AjouterUser
        Role.getItems().addAll("Client", "Admin", "Coach", "Sponsor");
    }

    @FXML
    private void PhotoPopUp() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        Window stage = PhotoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();
            System.out.println("Nouvelle photo sélectionnée : " + selectedPhotoPath);
        }
    }

    public void setUser(User user) {
        this.user = user;
        Nom.setText(user.getNom());
        Email.setText(user.getEmail());
        MotDePasse.setText(user.getMotDePasse());
        DateNaissance.setValue(user.getDateDeNaissance());
        Role.setValue(user.getRole()); // Assuming role is a String
    }

    @FXML
    private void handleSave() {
        // Validate inputs
        String nom = Nom.getText().trim();
        String email = Email.getText().trim();
        String motDePasse = MotDePasse.getText();

        // Check Nom: no numbers, only letters and spaces
        if (!nom.matches("[a-zA-Z\\s]+")) {
            showAlert("Erreur", "Le nom ne doit contenir que des lettres et des espaces.");
            return;
        }

        // Check Email: valid email format
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert("Erreur", "Veuillez entrer une adresse email valide.");
            return;
        }

        // Check MotDePasse: at least 8 characters
        if (motDePasse.length() < 8) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        // Update user object if all validations pass
        user.setNom(nom);
        user.setEmail(email);
        user.setMotDePasse(motDePasse);
        user.setDateDeNaissance(DateNaissance.getValue());
        user.setRole(Role.getValue());

        if (!selectedPhotoPath.isEmpty()) {
            user.setPhoto(selectedPhotoPath); // update only if new photo is selected
        }

        // Save to database
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

        // Close the window
        Stage stage = (Stage) Nom.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}