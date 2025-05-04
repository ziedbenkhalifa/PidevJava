package tn.cinema.controllers;

import javafx.scene.layout.AnchorPane;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import tn.cinema.services.FacePlusPlusService;
import tn.cinema.utils.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.github.sarxos.webcam.Webcam;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Login {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Button forgetPasswordButton;
    @FXML
    private Button capturePhotoButton;

    private final UserService userService = new UserService();
    private final FacePlusPlusService facePlusPlusService;
    private String capturedPhotoPath = "";
    private static final String FACEPP_API_KEY = "-yjUYXoeiNroPwlAxENU2VO1AENZ-xBq";
    private static final String FACEPP_API_SECRET = "bZ1k1RzrO9hrT0OQSL8ip8bnNzbI43ou";

    // Webcam variables
    private Webcam webcam;
    private volatile boolean isCapturing = false;
    private Stage cameraStage;
    private ImageView cameraView;

    public Login() {
        this.facePlusPlusService = new FacePlusPlusService(FACEPP_API_KEY, FACEPP_API_SECRET);
    }

    @FXML
    public void initialize() {
        capturePhotoButton.setDisable(true);

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            String email = newValue.trim();
            if (email.isEmpty()) {
                capturePhotoButton.setDisable(true);
                return;
            }

            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!email.matches(emailRegex)) {
                capturePhotoButton.setDisable(true);
                return;
            }

            List<User> users = userService.recuperer().stream()
                    .filter(u -> u.getEmail().equals(email))
                    .toList();

            capturePhotoButton.setDisable(users.isEmpty() || users.stream().allMatch(u -> u.getFaceToken() == null || u.getFaceToken().isEmpty()));
        });

        // Removed the call to regenerateAllFaceTokens from initialize
        // It should be called once elsewhere, e.g., in the main method or a singleton
    }

    @FXML
    private void capturePhoto(ActionEvent event) {
        webcam = Webcam.getDefault();
        if (webcam == null) {
            showAlert("Error", "No webcam detected. Please ensure a webcam is connected.", Alert.AlertType.ERROR);
            return;
        }

        try {
            webcam.setViewSize(new Dimension(1280, 720));
            webcam.open();
        } catch (Exception e) {
            try {
                webcam.setViewSize(new Dimension(640, 480));
                webcam.open();
            } catch (Exception fallbackException) {
                showAlert("Error", "Failed to open webcam: " + fallbackException.getMessage(), Alert.AlertType.ERROR);
                return;
            }
        }

        cameraStage = new Stage();
        cameraStage.setTitle("Capture Photo for Face Login");
        cameraView = new ImageView();
        cameraView.setFitWidth(640);
        cameraView.setFitHeight(480);

        Button captureButton = new Button("Capture");
        captureButton.setStyle("-fx-background-color: #04a9f4; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");
        captureButton.setOnAction(e -> captureAndSavePhoto());

        VBox cameraLayout = new VBox(10, cameraView, captureButton);
        cameraLayout.setAlignment(javafx.geometry.Pos.CENTER);
        Scene cameraScene = new Scene(cameraLayout);
        cameraStage.setScene(cameraScene);
        cameraStage.setOnCloseRequest(e -> stopCamera());
        cameraStage.show();

        isCapturing = true;
        new Thread(() -> {
            while (isCapturing && webcam.isOpen()) {
                try {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        Image fxImage = bufferedImageToFXImage(image);
                        Platform.runLater(() -> cameraView.setImage(fxImage));
                    }
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void captureAndSavePhoto() {
        BufferedImage image = webcam.getImage();
        if (image != null) {
            try {
                capturedPhotoPath = "temp_face_login_photo.jpg";
                ImageIO.write(image, "jpg", new File(capturedPhotoPath));
                System.out.println("Photo captured and saved to: " + capturedPhotoPath);
                stopCamera();
                cameraStage.close();
                handleFaceLogin();
            } catch (IOException e) {
                showAlert("Error", "Failed to save captured photo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            System.out.println("Failed to capture photo from webcam.");
        }
    }

    private void stopCamera() {
        isCapturing = false;
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    private Image bufferedImageToFXImage(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new Image(bais);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleFaceLogin() {
        if (capturedPhotoPath.isEmpty()) {
            showAlert("Validation Error", "Please capture a photo for face login.", Alert.AlertType.ERROR);
            return;
        }

        String email = emailField.getText().trim();
        List<User> users;

        if (!email.isEmpty()) {
            users = userService.recuperer().stream()
                    .filter(u -> u.getEmail().equals(email))
                    .toList();
        } else {
            users = userService.recuperer();
        }

        if (users.isEmpty()) {
            showAlert("Error", "No users found" + (email.isEmpty() ? "." : " for email: " + email), Alert.AlertType.ERROR);
            return;
        }

        String newFaceToken = null;
        try {
            JsonObject detectionResult = facePlusPlusService.detectFace(capturedPhotoPath);
            System.out.println("Face detection result for login attempt: " + detectionResult);
            JsonArray faces = detectionResult.getAsJsonArray("faces");
            if (faces == null || faces.size() == 0) {
                System.out.println("No faces detected in the captured photo: " + detectionResult);
            } else {
                newFaceToken = faces.get(0).getAsJsonObject().get("face_token").getAsString();
                System.out.println("Detected face token: " + newFaceToken);
            }
        } catch (IOException e) {
            System.out.println("Error detecting face: " + e.getMessage());
        }

        if (newFaceToken == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Face Detection Failed");
            alert.setHeaderText(null);
            alert.setContentText("No face detected in the captured photo. Would you like to try again? (Ensure good lighting and clear visibility of your face.)");

            ButtonType tryAgainButton = new ButtonType("Try Again");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(tryAgainButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == tryAgainButton) {
                    capturePhoto(null);
                }
            });
            return;
        }

        User matchedUser = null;
        try {
            String outerId = "user_faceset";
            JsonObject searchResult = facePlusPlusService.searchFace(outerId, newFaceToken);
            System.out.println("Face search result: " + searchResult);
            JsonArray results = searchResult.getAsJsonArray("results");
            if (results != null && results.size() > 0) {
                JsonObject topResult = results.get(0).getAsJsonObject();
                double confidence = topResult.get("confidence").getAsDouble();
                System.out.println("Top match confidence: " + confidence + ", Face token: " + topResult.get("face_token").getAsString());
                if (confidence > 70.0) {
                    String matchedFaceToken = topResult.get("face_token").getAsString();
                    for (User user : users) {
                        if (user.getFaceToken() != null && user.getFaceToken().equals(matchedFaceToken)) {
                            matchedUser = user;
                            System.out.println("Matched user: " + user.getEmail());
                            break;
                        }
                    }
                    if (matchedUser == null) {
                        System.out.println("No user found with face token: " + matchedFaceToken);
                    }
                } else {
                    System.out.println("Confidence too low: " + confidence);
                }
            } else {
                System.out.println("No matching faces found in FaceSet.");
            }
        } catch (IOException e) {
            System.out.println("Error searching face: " + e.getMessage());
            showAlert("Error", "Failed to perform face authentication: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        if (matchedUser != null) {
            SessionManager.getInstance().setLoggedInUser(matchedUser);
            showAlert("Login Successful", "Face authentication successful! Welcome, " + matchedUser.getNom(), Alert.AlertType.INFORMATION);
            if ("Admin".equalsIgnoreCase(matchedUser.getRole())) {
                loadAdminScreen();
            } else {
                loadFrontzScreen();
            }
        } else {
            showAlert("Login Failed", "Face authentication failed. No matching user found.", Alert.AlertType.ERROR);
        }
    }

    public void regenerateAllFaceTokens() {
        String facesDirPath = "src/main/resources/faces/";
        File facesDir = new File(facesDirPath);
        if (!facesDir.exists()) {
            facesDir.mkdirs();
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
            System.out.println("Error creating FaceSet during token regeneration: " + e.getMessage());
            // Continue processing even if FaceSet creation fails due to FACESET_EXIST
        } catch (Exception e) {
            System.out.println("Unexpected error during FaceSet creation: " + e.getMessage());
        }

        List<User> users = userService.recuperer();
        System.out.println("Found " + users.size() + " users for token regeneration.");
        for (User user : users) {
            try {
                String photoPath = user.getPhoto();
                if (photoPath == null || photoPath.isEmpty()) {
                    System.out.println("No photo for user " + user.getEmail() + ", skipping.");
                    user.setFaceToken("");
                    userService.modifier(user);
                    continue;
                }

                String cleanedPath = photoPath.startsWith("file:") ? photoPath.replace("file:", "") : photoPath;
                File sourceFile = new File(cleanedPath);
                if (!sourceFile.exists()) {
                    System.out.println("Photo file does not exist for user " + user.getEmail() + ": " + cleanedPath + ", skipping.");
                    user.setFaceToken("");
                    userService.modifier(user);
                    continue;
                }

                String newFileName = "user_" + user.getId() + "_" + sourceFile.getName();
                Path targetPath = Paths.get(facesDirPath, newFileName);
                try {
                    Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copied photo for user " + user.getEmail() + " to: " + targetPath);
                } catch (IOException e) {
                    System.out.println("Failed to copy photo for user " + user.getEmail() + ": " + e.getMessage());
                    user.setFaceToken("");
                    userService.modifier(user);
                    continue;
                }

                String classpathPath = "/faces/" + newFileName;
                user.setPhoto(classpathPath);

                String newFaceToken = null;
                try {
                    JsonObject detectionResult = facePlusPlusService.detectFace(targetPath.toString());
                    System.out.println("Face detection result for user " + user.getEmail() + ": " + detectionResult);
                    JsonArray faces = detectionResult.getAsJsonArray("faces");
                    if (faces != null && faces.size() > 0) {
                        newFaceToken = faces.get(0).getAsJsonObject().get("face_token").getAsString();
                        System.out.println("Generated face token for user " + user.getEmail() + ": " + newFaceToken);
                    } else {
                        System.out.println("No faces detected for user " + user.getEmail());
                    }
                } catch (IOException e) {
                    System.out.println("Error detecting face for user " + user.getEmail() + ": " + e.getMessage());
                }

                if (newFaceToken != null) {
                    user.setFaceToken(newFaceToken);
                    try {
                        JsonObject addResult = facePlusPlusService.addFaceToSet(outerId, newFaceToken);
                        System.out.println("Added face token to FaceSet for user " + user.getEmail() + ": " + newFaceToken + ", Result: " + addResult);
                    } catch (IOException e) {
                        System.out.println("Failed to add face token to FaceSet for user " + user.getEmail() + ": " + e.getMessage());
                        user.setFaceToken("");
                    }
                } else {
                    user.setFaceToken("");
                    System.out.println("Failed to generate face token for user " + user.getEmail() + ", cleared token.");
                }
                try {
                    userService.modifier(user);
                    System.out.println("Updated user " + user.getEmail() + " with new face token: " + user.getFaceToken());
                } catch (Exception e) {
                    System.out.println("Failed to update user " + user.getEmail() + ": " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Unexpected error processing user " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLoginAction(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Email and password cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            showAlert("Validation Error", "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }

        if (password.length() < 8) {
            showAlert("Validation Error", "Password must be at least 8 characters long.", Alert.AlertType.ERROR);
            return;
        }

        User user;
        try {
            user = userService.recuperer().stream()
                    .filter(u -> u.getEmail().equals(email) && u.getMotDePasse().equals(password))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve user: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        if (user != null) {
            SessionManager.getInstance().setLoggedInUser(user);
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

    @FXML
    private void handleForgetPasswordAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgetPassword.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = (Stage) forgetPasswordButton.getScene().getWindow();
            stage.setScene(new Scene(pane));
            stage.setTitle("Reset Password");
        } catch (Exception e) {
            showAlert("Error", "Failed to load ForgetPassword.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAdminScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(pane));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            showAlert("Error", "Failed to load Dashboard.fxml: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadFrontzScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontZ.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(pane));
            stage.setTitle("FrontZ");
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