package tn.cinema.controllers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.shape.Circle;
import javafx.animation.RotateTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
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
import java.util.ResourceBundle;

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
    @FXML private VBox chatBox;
    @FXML private TextField userInput;
    @FXML private Button sendButton;
    @FXML private ScrollPane chatScrollPane;

    // Éléments du loader
    @FXML private AnchorPane loaderPane;
    @FXML private Circle loaderCircle;
    private RotateTransition rotateTransition;

    // Dialogflow configuration
    private static final String PROJECT_ID = "my-project-4489-1741031296470";
    private static final String CREDENTIALS_PATH = "/my-project-4489-1741031296470-f405a23c74d6.json";
    private static final String LANGUAGE_CODE = "fr-FR";
    private static final String DIALOGFLOW_API_ENDPOINT = "https://dialogflow.googleapis.com/v2/projects/%s/agent/sessions/%s:detectIntent";
    private final String sessionId = UUID.randomUUID().toString();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private GoogleCredentials credentials;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des boutons
        demandeSubButton.setVisible(false);
        publiciteSubButton.setVisible(false);
        courSubButton.setVisible(false);
        seanceSubButton.setVisible(false);

        // Initialisation Dialogflow
        initializeDialogflow();

        // Configuration des actions pour le champ de texte (appuyer sur Entrée)
        userInput.setOnAction(event -> handleSendMessage(event));

        // Initialisation de l'animation du loader
        initializeLoader();

        // Message de bienvenue
        Platform.runLater(() -> {
            addBotMessage("Bonjour! Je suis l'assistant virtuel de CinemaShowtime. Comment puis-je vous aider aujourd'hui?");
            scrollToBottom();
        });
    }

    /**
     * Initialise l'animation du loader
     */
    private void initializeLoader() {
        // Configuration de l'animation de rotation
        rotateTransition = new RotateTransition(Duration.seconds(1.5), loaderCircle);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);
    }

    /**
     * Affiche le loader d'attente
     */
    private void showLoader() {
        Platform.runLater(() -> {
            loaderPane.setVisible(true);
            rotateTransition.play();
        });
    }

    /**
     * Cache le loader d'attente
     */
    private void hideLoader() {
        Platform.runLater(() -> {
            loaderPane.setVisible(false);
            rotateTransition.stop();
        });
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

    // Méthode pour ajouter un message utilisateur (aligné à gauche avec icône)
    private void addUserMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        messageContainer.setPadding(new Insets(5, 10, 5, 10));
        messageContainer.setSpacing(10);

        // Ajouter l'icône de l'utilisateur
        ImageView userIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-user-64.png")));
        userIcon.setFitHeight(30);
        userIcon.setFitWidth(30);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(message);
        text.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        text.setStyle("-fx-fill: white;");
        textFlow.getChildren().add(text);
        textFlow.setStyle("-fx-background-color: #3e2063; -fx-background-radius: 15; -fx-padding: 10;");
        textFlow.setPrefWidth(300);

        messageContainer.getChildren().addAll(userIcon, textFlow);

        Platform.runLater(() -> {
            chatBox.getChildren().add(messageContainer);
            scrollToBottom();
        });
    }

    // Méthode pour ajouter un message du bot (aligné à droite avec icône)
    private void addBotMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
        messageContainer.setPadding(new Insets(5, 10, 5, 10));
        messageContainer.setSpacing(10);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(message);
        text.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        text.setStyle("-fx-fill: white;");
        textFlow.getChildren().add(text);
        textFlow.setStyle("-fx-background-color: #6a4ba3; -fx-background-radius: 15; -fx-padding: 10;");
        textFlow.setPrefWidth(300);

        // Ajouter l'icône du chatbot
        ImageView botIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/chatbot-icon.png")));
        botIcon.setFitHeight(30);
        botIcon.setFitWidth(30);

        messageContainer.getChildren().addAll(textFlow, botIcon);

        Platform.runLater(() -> {
            chatBox.getChildren().add(messageContainer);
            scrollToBottom();
        });
    }

    // Méthode pour faire défiler le chat vers le bas
    private void scrollToBottom() {
        Platform.runLater(() -> {
            if (chatScrollPane != null && chatBox != null) {
                // Forcer la mise à jour du layout
                chatBox.layout();
                // Utiliser PauseTransition pour attendre que le rendu soit complet
                PauseTransition pause = new PauseTransition(Duration.millis(50));
                pause.setOnFinished(e -> chatScrollPane.setVvalue(1.0));
                pause.play();
                // Fallback: Si PauseTransition échoue, forcer le défilement immédiatement
                chatScrollPane.setVvalue(1.0);
            }
        });
    }

    @FXML
    private void handleSendMessage(ActionEvent event) {
        String message = userInput.getText().trim();
        if (!message.isEmpty()) {
            addUserMessage(message);
            userInput.clear();

            if (credentials == null) {
                addBotMessage("Service non disponible. Veuillez réessayer plus tard.");
                return;
            }

            // Afficher le loader pendant la réflexion
            showLoader();

            // Simulation d'un délai de réflexion et traitement asynchrone
            new Thread(() -> {
                try {
                    // On ajoute un petit délai pour simuler la réflexion (facultatif)
                    Thread.sleep(1200);

                    String response = detectIntent(message);

                    // Masquer le loader et afficher la réponse
                    Platform.runLater(() -> {
                        hideLoader();
                        addBotMessage(response);
                    });
                } catch (Exception e) {
                    LOGGER.severe("Error detecting intent: " + e.getMessage());
                    Platform.runLater(() -> {
                        hideLoader();
                        addBotMessage("Désolé, une erreur s'est produite. Veuillez reformuler votre question.");
                    });
                }
            }).start();
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

    // Méthodes existantes de navigation
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