package tn.cinema.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.json.JSONObject;

public class AjouterDemandeClientController implements Initializable {

    @FXML private TextField nombreJoursField;
    @FXML private TextArea descriptionField;
    @FXML private ChoiceBox<String> typeChoiceBox;
    @FXML private TextField lienSuppField;
    @FXML private WebView recaptchaWebView;
    @FXML private Button recordButton;

    private WebEngine webEngine;
    private String recaptchaResponse;
    private final DemandeService demandeService = new DemandeService();
    private DemandeClient parentController;

    // Bad Words API configuration
    private static final String BAD_WORDS_API_URL = "https://api.apilayer.com/bad_words";
    private static final String BAD_WORDS_API_KEY = "43DshU1XPDd4budDx6rnC4cKmZy4OLpl"; // Replace with your APILayer API key

    // Audio configuration
    private boolean isRecording = false;
    private AudioFormat audioFormat;
    private TargetDataLine line;
    private File audioFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ChoiceBox
        typeChoiceBox.setItems(FXCollections.observableArrayList("footerWeb", "backdrop", "integrefilm"));
        typeChoiceBox.setValue("footerWeb");

        // Configure WebView for reCAPTCHA
        webEngine = recaptchaWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setOnAlert(event -> System.out.println("WebView Alert: " + event.getData()));
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        // Load the reCAPTCHA page
        webEngine.load("http://localhost:8081/recaptcha.html");

        // Listener for WebView loading state
        webEngine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
            System.out.println("WebView state changed to: " + newState);
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("WebView loaded successfully: " + webEngine.getLocation());
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javafx", new JavaScriptBridge());
                System.out.println("JavaFX bridge set");
                try {
                    Object grecaptcha = webEngine.executeScript("typeof grecaptcha !== 'undefined' ? 'reCAPTCHA loaded' : 'reCAPTCHA not loaded'");
                    System.out.println("reCAPTCHA status: " + grecaptcha);
                } catch (Exception e) {
                    System.out.println("Error checking reCAPTCHA status: " + e.getMessage());
                }
            } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                System.out.println("WebView failed to load: " + webEngine.getLoadWorker().getException());
            }
        });

        // Reset reCAPTCHA response when WebView starts loading
        webEngine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SCHEDULED) {
                recaptchaResponse = null;
                System.out.println("reCAPTCHA response reset");
            }
        });

        // Initialize audio format for Vosk (16kHz, mono, 16-bit)
        audioFormat = new AudioFormat(16000, 16, 1, true, false);

        // Initialize Vosk
        try {
            LibVosk.setLogLevel(LogLevel.DEBUG);
        } catch (Exception e) {
            System.out.println("Error initializing Vosk: " + e.getMessage());
        }
    }

    public class JavaScriptBridge {
        public void setRecaptchaResponse(String token) {
            Platform.runLater(() -> {
                recaptchaResponse = token;
                System.out.println("reCAPTCHA token received in JavaFX: " + token);
            });
        }

        public void logMessage(String message) {
            System.out.println("JavaScript log: " + message);
        }
    }

    public void setParentController(DemandeClient parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void recordAudio() {
        if (!isRecording) {
            // Start recording
            isRecording = true;
            recordButton.setText("Arrêter l'enregistrement");
            audioFile = new File("recording.wav");

            new Thread(() -> {
                try {
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
                    line = (TargetDataLine) AudioSystem.getLine(info);
                    line.open(audioFormat);
                    line.start();

                    AudioInputStream ais = new AudioInputStream(line);
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);

                } catch (Exception e) {
                    Platform.runLater(() -> showErrorAlert("Erreur d'enregistrement: " + e.getMessage()));
                    e.printStackTrace();
                }
            }).start();
        } else {
            // Stop recording and transcribe
            isRecording = false;
            recordButton.setText("Enregistrer la description");
            if (line != null) {
                line.stop();
                line.close();
            }

            // Transcribe the audio
            transcribeAudio();
        }
    }

    private void transcribeAudio() {
        new Thread(() -> {
            try {
                String transcribedText = transcribeWithVosk(audioFile);
                if (transcribedText == null || transcribedText.trim().isEmpty()) {
                    Platform.runLater(() -> showErrorAlert("Aucune parole détectée"));
                    return;
                }

                // Check for bad words
                if (containsBadWords(transcribedText)) {
                    Platform.runLater(() -> showErrorAlert("Texte inapproprié détecté"));
                    return;
                }

                // Update description field
                Platform.runLater(() -> descriptionField.setText(transcribedText));

            } catch (Exception e) {
                Platform.runLater(() -> showErrorAlert("Erreur de transcription: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    private String transcribeWithVosk(File audioFile) throws Exception {
        URL modelUrl = getClass().getResource("/vosk-model-small-fr-0.22");
        if (modelUrl == null) {
            throw new FileNotFoundException("Modèle Vosk non trouvé");
        }

        try (Model model = new Model(modelUrl.getPath());
             Recognizer recognizer = new Recognizer(model, 16000);
             AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = ais.read(buffer)) != -1) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    // Process partial results if needed
                }
            }

            JSONObject finalResult = new JSONObject(recognizer.getFinalResult());
            String transcribedText = finalResult.getString("text");
            System.out.println("Transcription finale: " + transcribedText);
            return transcribedText;
        }
    }

    @FXML
    private void ajouterDemande() {
        System.out.println("Adding demande, checking reCAPTCHA response...");

        // Attempt to manually retrieve the token if not set
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            try {
                System.out.println("Attempting to manually retrieve reCAPTCHA token...");
                String token = (String) webEngine.executeScript("grecaptcha.getResponse()");
                if (token != null && !token.isEmpty()) {
                    recaptchaResponse = token;
                    System.out.println("Manually retrieved reCAPTCHA token: " + token);
                } else {
                    System.out.println("No token found via manual retrieval");
                }
            } catch (Exception e) {
                System.out.println("Error during manual token retrieval: " + e.getMessage());
            }
        }

        // Add a delay to ensure the token is set
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            try {
                System.out.println("Waiting for reCAPTCHA token...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Check if reCAPTCHA response is still null
        System.out.println("reCAPTCHA response after wait: " + recaptchaResponse);
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            showErrorAlert("Veuillez compléter la vérification reCAPTCHA.");
            return;
        }

        // Simulate reCAPTCHA validation
        if (!simulateRecaptchaValidation(recaptchaResponse)) {
            showErrorAlert("Échec de la vérification reCAPTCHA.");
            return;
        }

        // Validate form inputs
        String nombreJoursText = nombreJoursField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = typeChoiceBox.getValue();
        String lienSupp = lienSuppField.getText().trim();

        // Check for bad words in description using Bad Words API
        try {
            if (containsBadWords(description)) {
                showErrorAlert("La description contient des mots inappropriés. Veuillez modifier le texte.");
                return;
            }
        } catch (Exception e) {
            showErrorAlert("Erreur lors de la vérification des mots inappropriés : " + e.getMessage());
            return;
        }

        if (nombreJoursText.isEmpty()) {
            showErrorAlert("Le champ 'Nombre de Jours' est requis.");
            return;
        }
        if (description.isEmpty()) {
            showErrorAlert("Le champ 'Description' est requis.");
            return;
        }
        if (type == null) {
            showErrorAlert("Le champ 'Type' est requis.");
            return;
        }
        if (lienSupp.isEmpty()) {
            showErrorAlert("Le champ 'Lien Supplémentaire' est requis.");
            return;
        }

        int nombreJours;
        try {
            nombreJours = Integer.parseInt(nombreJoursText);
            if (nombreJours <= 0) {
                showErrorAlert("Le 'Nombre de Jours' doit être un entier positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Le 'Nombre de Jours' doit être un entier valide.");
            return;
        }

        // Validate if the URL exists
        if (!isWebsiteAccessible(lienSupp)) {
            showErrorAlert("Le 'Lien Supplémentaire' doit être une URL valide et accessible.");
            return;
        }

        // Add the demand
        try {
            Demande demande = new Demande(nombreJours, description, type, lienSupp);
            demandeService.ajoutDemande(demande);
            if (parentController != null) parentController.refreshList();
            ((Stage) nombreJoursField.getScene().getWindow()).close();
        } catch (SQLException e) {
            showErrorAlert("Erreur lors de l'ajout de la demande : " + e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        ((Stage) nombreJoursField.getScene().getWindow()).close();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private boolean isWebsiteAccessible(String url) {
        // Ensure URL has a protocol
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URL(url).toURI())
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .timeout(java.time.Duration.ofSeconds(5))
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            int statusCode = response.statusCode();
            System.out.println("URL: " + url + ", Status Code: " + statusCode);
            return statusCode >= 200 && statusCode < 300; // Consider 2xx as success
        } catch (Exception e) {
            System.out.println("Error checking URL accessibility: " + e.getMessage());
            return false;
        }
    }

    private boolean simulateRecaptchaValidation(String token) {
        return token != null && !token.isEmpty();
    }

    private boolean containsBadWords(String text) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String payload = new JSONObject().put("text", text).toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URL(BAD_WORDS_API_URL).toURI())
                .header("Content-Type", "application/json")
                .header("apikey", BAD_WORDS_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode != 200) {
            System.out.println("Bad Words API error: Status " + statusCode + ", Response: " + response.body());
            throw new Exception("Échec de la vérification des mots inappropriés (Code: " + statusCode + ")");
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        int badWordsCount = jsonResponse.getInt("bad_words_total");

        if (badWordsCount > 0) {
            System.out.println("Bad words detected: " + jsonResponse.getJSONArray("bad_words_list").toString());
            return true;
        }

        return false;
    }
}