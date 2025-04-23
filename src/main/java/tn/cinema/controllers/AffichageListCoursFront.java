package tn.cinema.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;
import tn.cinema.entities.User;
import tn.cinema.services.CourService;
import tn.cinema.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contrôleur pour l'affichage de la liste des cours dans l'interface front.
 */
public class AffichageListCoursFront implements Initializable {

    @FXML
    private GridPane coursGrid;
    @FXML
    private Button courSubButton;
    @FXML
    private Button seanceSubButton;
    @FXML
    private ComboBox<String> typeFilterComboBox;
    @FXML
    private Button filmsButton;
    @FXML
    private Button produitsButton;
    @FXML
    private Button coursButton;
    @FXML
    private Button publicitesButton;
    @FXML
    private Button participationButton;
    @FXML
    private Button monCompteButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button retourButton;
    @FXML
    private TextField searchField; // Champ de recherche
    @FXML
    private Button applySearchButton; // Bouton Appliquer

    private CourService courService = new CourService();
    private List<Cour> allCours = new ArrayList<>();
    private List<Integer> participatedCoursIds;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Initialisation du contrôleur, appelée après le chargement du FXML.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Récupérer l'utilisateur connecté depuis la session
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            int userId = loggedInUser.getId();

            // Charger tous les cours
            allCours = courService.recuperer();

            // Débogage : Afficher toutes les valeurs de typeCour, coût et dates
            System.out.println("=== Début du chargement des cours ===");
            System.out.println("Nombre total de cours récupérés : " + allCours.size());
            System.out.println("Valeurs dans allCours :");
            for (Cour cour : allCours) {
                System.out.println("Cours ID: " + cour.getId() + ", Type: '" + (cour.getTypeCour() != null ? cour.getTypeCour() : "NULL") +
                        "', Coût: " + cour.getCout() + " DT, Date Début: " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "NULL") +
                        ", Date Fin: " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "NULL"));
            }
            System.out.println("=== Fin du chargement des cours ===");

            // Trier les cours par coût croissant pour l'affichage initial
            allCours.sort(Comparator.comparingDouble(Cour::getCout));

            // Charger les participations de l'utilisateur connecté
            participatedCoursIds = courService.recupererParticipations(userId);

            // Afficher tous les cours par défaut (triés par coût)
            displayCours(allCours);

            // Sélectionner "Tous" par défaut dans le ComboBox
            typeFilterComboBox.getSelectionModel().select("Tous");

            // Ajouter un écouteur pour la recherche en temps réel
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                applySearchAction();
            });

            // Gérer la fermeture de la fenêtre principale
            if (coursGrid != null && coursGrid.getScene() != null && coursGrid.getScene().getWindow() != null) {
                Stage stage = (Stage) coursGrid.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    Platform.exit();
                    System.exit(0);
                });
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des cours : " + e.getMessage());
        }
    }

    /**
     * Affiche les cours dans le GridPane.
     * @param coursList Liste des cours à afficher.
     */
    private void displayCours(List<Cour> coursList) {
        coursGrid.getChildren().clear();

        int row = 0;
        int col = 0;

        for (Cour cour : coursList) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #1c2526; -fx-background-radius: 10; -fx-padding: 10;");
            card.setPrefWidth(250);
            card.setPrefHeight(150);

            Label typeLabel = new Label(cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini");
            typeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

            Label coutLabel = new Label(cour.getCout() + " DT");
            coutLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

            Label dateDebutLabel = new Label("Date Début: " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "Non défini"));
            dateDebutLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

            Label dateFinLabel = new Label("Date Fin: " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "Non défini"));
            dateFinLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

            Button showButton = new Button("SHOW");
            showButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5;");
            showButton.setOnAction(event -> handleShowAction(cour));

            Button actionButton;
            if (participatedCoursIds.contains(cour.getId())) {
                actionButton = new Button("QUITTER");
                actionButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                actionButton.setOnAction(event -> handleQuitterAction(cour));
            } else {
                actionButton = new Button("PARTICIPER");
                actionButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5;");
                actionButton.setOnAction(event -> handleParticiperAction(cour));
            }

            HBox buttonsBox = new HBox(10);
            buttonsBox.getChildren().addAll(showButton, actionButton);
            card.getChildren().addAll(typeLabel, coutLabel, dateDebutLabel, dateFinLabel, buttonsBox);

            card.setUserData(cour);
            coursGrid.add(card, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Gère l'action d'afficher les détails d'un cours.
     * @param cour Le cours à afficher.
     */
    private void handleShowAction(Cour cour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailsCour.fxml"));
            Parent root = loader.load();

            AfficherDetailsCour controller = loader.getController();
            controller.loadDetails(cour);

            Stage stage = (Stage) coursGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers les détails du cours : " + e.getMessage());
        }
    }

    /**
     * Gère l'action de participation à un cours.
     * @param cour Le cours auquel participer.
     */
    private void handleParticiperAction(Cour cour) {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            courService.ajouterParticipation(cour.getId());
            participatedCoursIds.add(cour.getId());

            showAlert("Succès", "Vous avez participé au cours : " + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini"));
            displayCours(allCours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la participation au cours : " + e.getMessage());
        }
    }

    /**
     * Gère l'action de quitter un cours.
     * @param cour Le cours à quitter.
     */
    private void handleQuitterAction(Cour cour) {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            int userId = loggedInUser.getId();
            courService.supprimerParticipation(userId, cour.getId());
            participatedCoursIds.remove(Integer.valueOf(cour.getId()));

            showAlert("Succès", "Vous avez quitté le cours : " + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini"));
            displayCours(allCours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'annulation de la participation : " + e.getMessage());
        }
    }

    /**
     * Affiche une alerte avec un titre et un message.
     * @param title Titre de l'alerte.
     * @param message Message de l'alerte.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigue vers la page des cours.
     */
    @FXML
    private void goCourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) courSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers la page Cours : " + e.getMessage());
        }
    }

    /**
     * Alterne la visibilité des sous-boutons (Cour, Séance).
     */
    @FXML
    private void toggleSubButtons() {
        boolean areSubButtonsVisible = courSubButton.isVisible();
        courSubButton.setVisible(!areSubButtonsVisible);
        seanceSubButton.setVisible(!areSubButtonsVisible);
    }

    /**
     * Retourne à la page d'accueil.
     * @param event Événement déclenché.
     */
    @FXML
    private void retourAccueilAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Front.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du retour à l'accueil : " + e.getMessage());
        }
    }

    /**
     * Affiche les participations de l'utilisateur.
     */
    @FXML
    private void goParticipationAction() {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Veuillez vous connecter pour voir vos participations.");
                return;
            }

            List<Integer> participationIds = courService.recupererParticipations(loggedInUser.getId());
            List<Cour> participatedCourses = new ArrayList<>();
            for (Cour cour : allCours) {
                if (participationIds.contains(cour.getId())) {
                    participatedCourses.add(cour);
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PopupParticipation.fxml"));
            Parent root = loader.load();

            PopupParticipationController controller = loader.getController();
            controller.setParticipations(participatedCourses);

            Stage popupStage = new Stage();
            controller.setStage(popupStage);

            Scene scene = new Scene(root);
            popupStage.setTitle("Mes participations");
            popupStage.setScene(scene);
            popupStage.setResizable(false);
            popupStage.show();

            // Gérer la fermeture de la popup
            popupStage.setOnCloseRequest(event -> popupStage.close());
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'affichage des participations : " + e.getMessage());
        }
    }

    /**
     * Filtre les cours par type (ComboBox).
     */
    @FXML
    private void filterByType() {
        String selectedType = typeFilterComboBox.getSelectionModel().getSelectedItem();
        List<Cour> filteredCours;
        String searchQuery = searchField.getText().trim().toLowerCase();

        if ("Tous".equals(selectedType)) {
            filteredCours = new ArrayList<>(allCours);
        } else {
            String filterType = selectedType.toLowerCase().trim();
            filteredCours = allCours.stream()
                    .filter(cour -> {
                        String typeCour = cour.getTypeCour() != null ? cour.getTypeCour().toLowerCase().trim() : "";
                        return typeCour.contains(filterType);
                    })
                    .collect(Collectors.toList());
        }

        // Appliquer la recherche si le champ de recherche n'est pas vide
        if (!searchQuery.isEmpty()) {
            filteredCours = filteredCours.stream()
                    .filter(cour -> {
                        String typeCour = cour.getTypeCour() != null ? cour.getTypeCour().toLowerCase().trim() : "";
                        String cout = String.valueOf(cour.getCout()).toLowerCase();
                        String dateDebut = cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER).toLowerCase() : "";
                        String dateFin = cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER).toLowerCase() : "";
                        return typeCour.contains(searchQuery) || cout.contains(searchQuery) ||
                                dateDebut.contains(searchQuery) || dateFin.contains(searchQuery);
                    })
                    .collect(Collectors.toList());
        }

        // Trier les cours filtrés par coût croissant
        filteredCours.sort(Comparator.comparingDouble(Cour::getCout));

        // Débogage : Afficher les cours filtrés et triés
        System.out.println("=== Filtre appliqué: " + selectedType + ", Recherche: " + searchQuery + " ===");
        System.out.println("Nombre de cours trouvés: " + filteredCours.size());
        for (Cour cour : filteredCours) {
            System.out.println("Cours filtré ID: " + cour.getId() + ", Type: '" + (cour.getTypeCour() != null ? cour.getTypeCour() : "NULL") +
                    "', Coût: " + cour.getCout() + " DT, Date Début: " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "NULL") +
                    ", Date Fin: " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "NULL"));
        }
        System.out.println("=== Fin du filtrage ===");
        displayCours(filteredCours);
    }

    /**
     * Applique la recherche basée sur le texte saisi.
     */
    @FXML
    private void applySearchAction() {
        String searchQuery = searchField.getText().trim().toLowerCase();
        String selectedType = typeFilterComboBox.getSelectionModel().getSelectedItem();
        List<Cour> filteredCours;

        // Commencer avec tous les cours ou ceux filtrés par type
        if ("Tous".equals(selectedType)) {
            filteredCours = new ArrayList<>(allCours);
        } else {
            String filterType = selectedType.toLowerCase().trim();
            filteredCours = allCours.stream()
                    .filter(cour -> {
                        String typeCour = cour.getTypeCour() != null ? cour.getTypeCour().toLowerCase().trim() : "";
                        return typeCour.contains(filterType);
                    })
                    .collect(Collectors.toList());
        }

        // Appliquer la recherche si le champ n'est pas vide
        if (!searchQuery.isEmpty()) {
            filteredCours = filteredCours.stream()
                    .filter(cour -> {
                        String typeCour = cour.getTypeCour() != null ? cour.getTypeCour().toLowerCase().trim() : "";
                        String cout = String.valueOf(cour.getCout()).toLowerCase();
                        String dateDebut = cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER).toLowerCase() : "";
                        String dateFin = cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER).toLowerCase() : "";
                        return typeCour.contains(searchQuery) || cout.contains(searchQuery) ||
                                dateDebut.contains(searchQuery) || dateFin.contains(searchQuery);
                    })
                    .collect(Collectors.toList());
        }

        // Trier les cours filtrés par coût
        filteredCours.sort(Comparator.comparingDouble(Cour::getCout));

        // Débogage : Afficher les résultats de la recherche
        System.out.println("=== Recherche appliquée: " + searchQuery + " ===");
        System.out.println("Nombre de cours trouvés: " + filteredCours.size());
        for (Cour cour : filteredCours) {
            System.out.println("Cours trouvé ID: " + cour.getId() + ", Type: '" + (cour.getTypeCour() != null ? cour.getTypeCour() : "NULL") +
                    "', Coût: " + cour.getCout() + " DT, Date Début: " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "NULL") +
                    ", Date Fin: " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "NULL"));
        }
        System.out.println("=== Fin de la recherche ===");

        // Mettre à jour l'affichage
        displayCours(filteredCours);
    }

    /**
     * Déconnexion de l'utilisateur.
     */
    @FXML
    private void logoutAction() {
        SessionManager.getInstance().setLoggedInUser(null);
        Platform.exit();
        System.exit(0);
    }
}