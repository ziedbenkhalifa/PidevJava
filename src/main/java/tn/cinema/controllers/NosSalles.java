package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NosSalles {

    @FXML
    private Button filmsButton;
    @FXML
    private Button produitsButton;
    @FXML
    private Button coursButton;
    @FXML
    private Button courSubButton;
    @FXML
    private Button seanceSubButton;
    @FXML
    private Button publicitesButton;
    @FXML
    private Button demandeSubButton;
    @FXML
    private Button publiciteSubButton;
    @FXML
    private Button monCompteButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button backButton;
    @FXML
    private WebView mapView;
    @FXML
    private ComboBox<String> cinemaComboBox;

    // Coordonnées des cinémas (latitude, longitude, zoom)
    private final Map<String, CinemaLocation> cinemaLocations = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialiser les localisations des cinémas
        initializeCinemaLocations();

        // Configurer la ComboBox
        setupCinemaComboBox();

        // Charger la carte par défaut
        loadMapForCinema("SHOWTIME1");
    }

    private void initializeCinemaLocations() {
        // Ajouter les coordonnées des cinémas
        cinemaLocations.put("SHOWTIME1", new CinemaLocation(36.8000, 10.1800, 15));
        cinemaLocations.put("SHOWTIME2", new CinemaLocation(36.8500, 10.2000, 15));
        cinemaLocations.put("SHOWTIME3", new CinemaLocation(36.7500, 10.2200, 15));
    }

    private void setupCinemaComboBox() {
        // Ajouter les options de cinéma
        cinemaComboBox.getItems().addAll("SHOWTIME1", "SHOWTIME2", "SHOWTIME3");

        // Sélectionner le premier par défaut
        cinemaComboBox.getSelectionModel().selectFirst();

        // Ajouter un écouteur pour le changement de sélection
        cinemaComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMapForCinema(newVal);
            }
        });
    }

    private void loadMapForCinema(String cinemaName) {
        CinemaLocation location = cinemaLocations.get(cinemaName);
        if (location != null) {
            // Construire l'URL OpenStreetMap avec les coordonnées du cinéma
            String mapUrl = String.format("https://www.openstreetmap.org/export/embed.html?bbox=%f,%f,%f,%f&layer=mapnik&marker=%f,%f",
                    location.longitude - 0.01, location.latitude - 0.01,
                    location.longitude + 0.01, location.latitude + 0.01,
                    location.latitude, location.longitude);

            loadMap(mapUrl);
        }
    }

    private void loadMap() {
        // Charger la carte par défaut (pour compatibilité avec le code existant)
        loadMapForCinema("SHOWTIME1");
    }

    private void loadMap(String mapUrl) {
        // Enable JavaScript
        mapView.getEngine().setJavaScriptEnabled(true);

        // Load the map
        mapView.getEngine().load(mapUrl);

        // Add a listener for load errors
        mapView.getEngine().getLoadWorker().exceptionProperty().addListener((obs, oldExc, newExc) -> {
            if (newExc != null) {
                // Display error message if map fails to load
                String errorHtml = "<html><body style='background-color:#0b0f29; color:white; text-align:center;'>" +
                        "<h2>Map Loading Error</h2>" +
                        "<p>Failed to load map. Please check your internet connection.</p>" +
                        "</body></html>";
                mapView.getEngine().loadContent(errorHtml);
            }
        });
    }

    // Classe interne pour stocker les coordonnées des cinémas
    private static class CinemaLocation {
        double latitude;
        double longitude;
        int zoom;

        CinemaLocation(double latitude, double longitude, int zoom) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.zoom = zoom;
        }
    }

    // Les méthodes existantes restent inchangées
    @FXML
    public void goBackToPubliciteClient(ActionEvent event) {
        navigateTo("/PubliciteClient.fxml", "Publicité Client", event);
    }

    @FXML
    public void filmsButtonClicked(ActionEvent event) {
        System.out.println("Films button clicked");
    }

    @FXML
    public void affichage(ActionEvent event) {
        System.out.println("Produits button clicked");
    }

    @FXML
    public void toggleSubButtonss(ActionEvent event) {
        boolean isVisible = courSubButton.isVisible();
        courSubButton.setVisible(!isVisible);
        seanceSubButton.setVisible(!isVisible);
    }

    @FXML
    public void goCourAction(ActionEvent event) {
        System.out.println("Cour button clicked");
    }

    @FXML
    public void handleSeanceAction(ActionEvent event) {
        System.out.println("Séance button clicked");
    }

    @FXML
    public void toggleSubButtons(ActionEvent event) {
        boolean isVisible = demandeSubButton.isVisible();
        demandeSubButton.setVisible(!isVisible);
        publiciteSubButton.setVisible(!isVisible);
    }

    @FXML
    public void goToDemandeClient(ActionEvent event) {
        navigateTo("/DemandeClient.fxml", "Demande Client", event);
    }

    @FXML
    public void goToPubliciteClient(ActionEvent event) {
        navigateTo("/PubliciteClient.fxml", "Publicité Client", event);
    }

    @FXML
    public void handleMonCompteAction(ActionEvent event) {
        System.out.println("Mon Compte button clicked");
    }

    @FXML
    public void logout(ActionEvent event) {
        navigateTo("/Login.fxml", "Login", event);
    }

    private void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
            System.out.println("Navigated to " + fxmlPath);
        } catch (IOException e) {
            System.err.println("Error navigating to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}