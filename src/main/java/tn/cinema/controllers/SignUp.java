package tn.cinema.controllers;

import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import tn.cinema.services.FacePlusPlusService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;
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
    @FXML
    private ImageView captchaImageView;
    @FXML
    private TextField captchaInputField;
    @FXML
    private Button refreshCaptchaButton;

    private final UserService userService = new UserService();
    private final FacePlusPlusService facePlusPlusService;
    private String selectedPhotoPath = "";
    private static final String FACEPP_API_KEY = "-yjUYXoeiNroPwlAxENU2VO1AENZ-xBq";
    private static final String FACEPP_API_SECRET = "bZ1k1RzrO9hrT0OQSL8ip8bnNzbI43ou";
    private String currentCaptchaCode;

    public SignUp() {
        this.facePlusPlusService = new FacePlusPlusService(FACEPP_API_KEY, FACEPP_API_SECRET);
    }

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Client", "Coach", "Sponsor");
        roleComboBox.setPromptText("Select Role");
        generateCaptcha();
    }

    private void generateCaptcha() {
        Canvas canvas = new Canvas(150, 50);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Random random = new Random();

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captchaCode = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            captchaCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        currentCaptchaCode = captchaCode.toString();

        gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
        gc.fillRect(0, 0, 150, 50);
        for (int i = 0; i < 50; i++) {
            gc.setFill(javafx.scene.paint.Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 0.3));
            gc.fillOval(random.nextInt(150), random.nextInt(50), 3, 3);
        }

        for (int i = 0; i < currentCaptchaCode.length(); i++) {
            gc.setFill(javafx.scene.paint.Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            gc.setFont(new javafx.scene.text.Font("Arial", 24));
            double angle = random.nextDouble() * 30 - 15;
            gc.save();
            gc.translate(20 + i * 25, 35);
            gc.rotate(angle);
            gc.fillText(String.valueOf(currentCaptchaCode.charAt(i)), 0, 0);
            gc.restore();
        }

        for (int i = 0; i < 3; i++) {
            gc.setStroke(javafx.scene.paint.Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 0.5));
            gc.strokeLine(random.nextInt(150), random.nextInt(50), random.nextInt(150), random.nextInt(50));
        }

        Image captchaImage = canvas.snapshot(null, null);
        captchaImageView.setImage(captchaImage);
    }

    @FXML
    private void handleRefreshCaptcha(ActionEvent event) {
        generateCaptcha();
        captchaInputField.clear();
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
            selectedPhotoPath = selectedFile.getAbsolutePath();
            System.out.println("Selected photo: " + selectedPhotoPath);
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        String userCaptchaInput = captchaInputField.getText().trim();
        if (!userCaptchaInput.equalsIgnoreCase(currentCaptchaCode)) {
            showAlert("CAPTCHA Error", "Incorrect CAPTCHA code. Please try again.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateForm()) {
            return;
        }

        String faceToken = enrollFace();
        if (faceToken == null) {
            showAlert("Error", "Failed to enroll face. Please ensure the photo contains a face.", Alert.AlertType.ERROR);
            return;
        }

        String outerId = "user_faceset";
        try {
            JsonObject result = facePlusPlusService.createFaceSet(outerId);
            if (result.has("error_message") && result.get("error_message").getAsString().contains("FACESET_EXIST")) {
                System.out.println("FaceSet already exists: " + outerId);
            } else {
                System.out.println("FaceSet created successfully: " + outerId);
            }
        } catch (IOException e) {
            System.out.println("Error creating FaceSet during signup: " + e.getMessage());
            // Continue processing even if FaceSet creation fails due to FACESET_EXIST
        }

        boolean faceAddedToSet = false;
        try {
            JsonObject addResult = facePlusPlusService.addFaceToSet(outerId, faceToken);
            System.out.println("Added face token to FaceSet: " + faceToken + ", Result: " + addResult);
            faceAddedToSet = true;
        } catch (IOException e) {
            System.out.println("Error adding face to FaceSet during signup: " + e.getMessage());
            showAlert("Error", "Failed to add face to FaceSet: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        if (faceAddedToSet) {
            try {
                User newUser = new User(
                        nomField.getText(),
                        dateNaissance.getValue(),
                        emailField.getText(),
                        roleComboBox.getValue(),
                        passwordField.getText(),
                        selectedPhotoPath.isEmpty() ? null : "file:" + selectedPhotoPath.replace("\\", "/"),
                        faceToken
                );

                userService.ajouter(newUser);
                System.out.println("Utilisateur ajouté avec succès");
                showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);
                handleBackAction(null);
            } catch (Exception e) {
                System.out.println("Error creating user: " + e.getMessage());
                showAlert("Error", "Failed to create account: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private String enrollFace() {
        if (selectedPhotoPath.isEmpty()) {
            System.out.println("No photo selected for face enrollment.");
            return null;
        }

        try {
            File file = new File(selectedPhotoPath);
            if (!file.exists()) {
                System.out.println("Photo file does not exist: " + selectedPhotoPath);
                return null;
            }

            JsonObject detectionResult = facePlusPlusService.detectFace(selectedPhotoPath);
            System.out.println("Face detection result for signup: " + detectionResult);
            JsonArray faces = detectionResult.getAsJsonArray("faces");
            if (faces == null || faces.size() == 0) {
                System.out.println("No faces detected in the image: " + detectionResult);
                return null;
            }

            String faceToken = faces.get(0).getAsJsonObject().get("face_token").getAsString();
            System.out.println("Detected face token for signup: " + faceToken);
            return faceToken;
        } catch (IOException e) {
            System.out.println("Error enrolling face: " + e.getMessage());
            return null;
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

        if (!nomField.getText().matches("^[a-zA-Z\\s]+$")) {
            errorMessage.append("Name must not contain numbers.\n");
        }

        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(emailField.getText());
        if (!matcher.matches()) {
            errorMessage.append("Invalid email format.\n");
        } else {
            try {
                boolean emailExists = userService.recuperer().stream()
                        .anyMatch(u -> u.getEmail().equalsIgnoreCase(emailField.getText()));
                if (emailExists) {
                    errorMessage.append("Email is already in use.\n");
                }
            } catch (Exception e) {
                errorMessage.append("Failed to check email uniqueness: " + e.getMessage() + "\n");
            }
        }

        if (passwordField.getText().length() < 8) {
            errorMessage.append("Password must be at least 8 characters.\n");
        }

        if (dateNaissance.getValue() == null) {
            errorMessage.append("Date of birth is required.\n");
        }

        if (roleComboBox.getValue() == null) {
            errorMessage.append("Please select a role.\n");
        }

        if (selectedPhotoPath.isEmpty()) {
            errorMessage.append("Please select a photo for face recognition.\n");
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