package tn.cinema.controllers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

public class DemandeChatbot implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DemandeChatbot.class.getName());

    // Boutons existants
    @FXML private Button retourButton;
    @FXML private Button publicitesButton;
    @FXML private Button demandeSubButton;
    @FXML private Button publiciteSubButton;
    @FXML private Button coursButton;
    @FXML private Button courSubButton;
    @FXML private Button seanceSubButton;
    @FXML private Button filmsButton;
    @FXML private Button produitsButton;
    @FXML private Button monCompteButton;
    @FXML private Button logoutButton;

    // Chatbot UI
    @FXML private TextArea chatArea;
    @FXML private TextField userInput;
    @FXML private Button sendButton;

    // Dialogflow configuration
    private static final String PROJECT_ID = "my-project-4489-1741031296470";
    private static final String CREDENTIALS_PATH = "/my-project-4489-1741031296470-f405a23c74d6.json";
    private static final String LANGUAGE_CODE = "fr-FR";
    private static final String DIALOGFLOW_API_ENDPOINT = "https://dialogflow.googleapis.com/v2/projects/%s/agent/sessions/%s:detectIntent";
    private final String sessionId = UUID.randomUUID().toString();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private GoogleCredentials credentials;

    @Override
    public void initialize(URL location, java.util.ResourceBundle resources) {
        // Initialisation des boutons
        demandeSubButton.setVisible(false);
        publiciteSubButton.setVisible(false);
        courSubButton.setVisible(false);
        seanceSubButton.setVisible(false);

        // Initialisation Dialogflow
        initializeDialogflow();

        // Message de bienvenue
        chatArea.appendText("Chatbot: Bonjour! Comment puis-je vous aider aujourd'hui?\n");
    }

    private void initializeDialogflow() {
        try {
            // Load credentials from classpath
            InputStream credentialsStream = getClass().getResourceAsStream(CREDENTIALS_PATH);
            if (credentialsStream == null) {
                String errorMsg = "Credentials file not found in classpath: " + CREDENTIALS_PATH;
                LOGGER.severe(errorMsg);
                showAlert("Erreur", errorMsg);
                return;
            }
            credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        } catch (IOException e) {
            LOGGER.severe("Dialogflow initialization failed: " + e.getMessage());
            showAlert("Erreur", "Failed to load credentials: " + e.getMessage());
        }
    }

    @FXML
    private void handleSendMessage(ActionEvent event) {
        String message = userInput.getText().trim();
        if (!message.isEmpty()) {
            chatArea.appendText("Vous: " + message + "\n");

            if (credentials == null) {
                chatArea.appendText("Chatbot: Service non disponible. Veuillez réessayer plus tard.\n");
                userInput.clear();
                return;
            }

            try {
                String response = detectIntent(message);
                chatArea.appendText("Chatbot: " + response + "\n");
            } catch (Exception e) {
                LOGGER.severe("Error detecting intent: " + e.getMessage());
                chatArea.appendText("Chatbot: Désolé, une erreur s'est produite: " + e.getMessage() + "\n");
            }

            userInput.clear();
        }
    }

    private String detectIntent(String text) throws Exception {
        // Refresh credentials to get a new access token
        credentials.refreshIfExpired();
        AccessToken accessToken = credentials.getAccessToken();
        if (accessToken == null) {
            throw new IOException("Failed to obtain access token");
        }

        // Construct JSON payload
        JSONObject queryInput = new JSONObject()
                .put("text", new JSONObject()
                        .put("text", text)
                        .put("languageCode", LANGUAGE_CODE));
        JSONObject payload = new JSONObject()
                .put("queryInput", queryInput);

        // Build HTTP request
        String url = String.format(DIALOGFLOW_API_ENDPOINT, PROJECT_ID, sessionId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken.getTokenValue())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        // Send request and get response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Dialogflow API error: " + response.body());
        }

        // Parse response
        JSONObject responseJson = new JSONObject(response.body());
        return responseJson.getJSONObject("queryResult").getString("fulfillmentText");
    }

    // Méthodes existantes de navigation (inchangées)
    @FXML
    public void toggleSubButtons(ActionEvent event) {
        boolean isVisible = demandeSubButton.isVisible();
        demandeSubButton.setVisible(!isVisible);
        publiciteSubButton.setVisible(!isVisible);
    }

    @FXML
    public void toggleSubButtonss(ActionEvent event) {
        boolean areSubButtonsVisible = courSubButton.isVisible();
        courSubButton.setVisible(!areSubButtonsVisible);
        seanceSubButton.setVisible(!areSubButtonsVisible);
    }

    @FXML
    public void goToDemandeClient(ActionEvent event) {
        navigateTo(event, "/DemandeClient.fxml", "Demande Client");
    }

    @FXML
    public void goToPubliciteClient(ActionEvent event) {
        navigateTo(event, "/PubliciteClient.fxml", "Publicité Client");
    }

    @FXML
    public void logout(ActionEvent event) {
        navigateTo(event, "/Login.fxml", "Login");
    }

    @FXML
    public void filmsButtonClicked(ActionEvent event) {
        navigateTo(event, "/FrontFilm.fxml", "Films");
    }

    @FXML
    public void affichage(ActionEvent event) {
        navigateTo(event, "/ListProduits.fxml", "Produits");
    }

    @FXML
    public void goCourAction(ActionEvent event) {
        navigateTo(event, "/AffichageListCoursFront.fxml", "Cours");
    }

    @FXML
    public void handleSeanceAction(ActionEvent event) {
        navigateTo(event, "/affichageListSeances.fxml", "Séances");
    }

    @FXML
    public void handleMonCompteAction(ActionEvent event) {
        navigateTo(event, "/MonCompte.fxml", "Mon Compte");
    }

    private void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find resource: " + fxmlPath);
            }
            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}