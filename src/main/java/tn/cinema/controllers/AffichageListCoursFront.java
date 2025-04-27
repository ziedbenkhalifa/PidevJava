package tn.cinema.controllers;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import tn.cinema.entities.Cour;
import tn.cinema.entities.User;
import tn.cinema.services.CourService;
import tn.cinema.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private DatePicker dateFilterPicker;
    @FXML
    private Button clearDateButton;
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
    private TextField searchField;
    @FXML
    private Button applySearchButton;
    @FXML
    private Label noResultsLabel;



    private CourService courService = new CourService();
    public List<Cour> allCours = new ArrayList<>();
    private List<Integer> participatedCoursIds = new ArrayList<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            allCours = courService.recuperer();
            System.out.println("=== Début du chargement des cours ===");
            System.out.println("Nombre total de cours récupérés : " + allCours.size());
            for (Cour cour : allCours) {
                System.out.println("Cours ID: " + cour.getId() + ", Type: '" + (cour.getTypeCour() != null ? cour.getTypeCour() : "NULL") +
                        "', Coût: " + cour.getCout() + " DT, Date Début: " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "NULL") +
                        ", Date Fin: " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "NULL"));
            }
            System.out.println("=== Fin du chargement des cours ===");

            allCours.sort(Comparator.comparingDouble(Cour::getCout));
            refreshParticipations();
            displayCours(allCours);

            // Le ComboBox est déjà rempli dans le FXML, on sélectionne simplement l'option par défaut
            typeFilterComboBox.getSelectionModel().select("Tous");

            searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

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

    public void refreshParticipations() {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser != null) {
                participatedCoursIds = courService.recupererParticipations(loggedInUser.getId());
                System.out.println("Participations rafraîchies pour utilisateur ID " + loggedInUser.getId() + ": " + participatedCoursIds);
            } else {
                participatedCoursIds = new ArrayList<>();
                System.out.println("Aucun utilisateur connecté, participatedCoursIds vidé.");
                throw new IllegalStateException("Aucun utilisateur connecté.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du rafraîchissement des participations : " + e.getMessage());
            participatedCoursIds = new ArrayList<>();
        }
    }

    private void applyFilters() {
        String selectedType = typeFilterComboBox.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = dateFilterPicker.getValue();
        String searchQuery = searchField.getText().trim().toLowerCase();
        List<Cour> filteredCours = new ArrayList<>(allCours);

        System.out.println("=== Début application des filtres ===");
        System.out.println("Type sélectionné: " + selectedType);
        System.out.println("Date sélectionnée: " + selectedDate);
        System.out.println("Recherche: " + searchQuery);

        // Étape 1 : Filtrer par date si une date est sélectionnée
        if (selectedDate != null) {
            filteredCours = filteredCours.stream()
                    .filter(cour -> {
                        if (cour.getDateDebut() == null || cour.getDateFin() == null) {
                            return false;
                        }
                        LocalDate startDate = cour.getDateDebut().toLocalDate();
                        LocalDate endDate = cour.getDateFin().toLocalDate();
                        boolean matches = !selectedDate.isBefore(startDate) && !selectedDate.isAfter(endDate);
                        return matches;
                    })
                    .collect(Collectors.toList());

            // Si aucun cours ne correspond à la date, arrêter le filtrage et afficher "Aucun cours trouvé"
            if (filteredCours.isEmpty()) {
                System.out.println("Aucun cours trouvé pour la date sélectionnée: " + selectedDate);
                displayCours(filteredCours); // Affichera "Aucun cours trouvé"
                return;
            }
        }

        // Étape 2 : Filtrer par type si un type spécifique est sélectionné
        if (selectedType != null && !"Tous".equals(selectedType)) {
            String filterType = selectedType.toLowerCase().trim();
            filteredCours = filteredCours.stream()
                    .filter(cour -> {
                        String typeCour = cour.getTypeCour() != null ? cour.getTypeCour().toLowerCase().trim() : "";
                        return typeCour.equals(filterType);
                    })
                    .collect(Collectors.toList());
        }

        // Étape 3 : Filtrer par recherche si une requête est entrée
        if (!searchQuery.isEmpty()) {
            filteredCours = filteredCours.stream()
                    .filter(cour -> {
                        String typeCour = cour.getTypeCour() != null ? cour.getTypeCour().toLowerCase().trim() : "";
                        String cout = String.valueOf(cour.getCout()).toLowerCase();
                        return typeCour.contains(searchQuery) || cout.contains(searchQuery);
                    })
                    .collect(Collectors.toList());
        }

        // Étape 4 : Trier par coût
        filteredCours.sort(Comparator.comparingDouble(Cour::getCout));
        System.out.println("Nombre de cours trouvés: " + filteredCours.size());
        for (Cour cour : filteredCours) {
            System.out.println("Cours filtré ID: " + cour.getId() + ", Type: '" + (cour.getTypeCour() != null ? cour.getTypeCour() : "NULL") +
                    "', Coût: " + cour.getCout() + " DT");
        }
        System.out.println("=== Fin du filtrage ===");

        displayCours(filteredCours);
    }

    @FXML
    private void filterByDate() {
        applyFilters();
    }

    @FXML
    private void clearDateFilter() {
        dateFilterPicker.setValue(null);
        applyFilters();
    }

    @FXML
    private void filterByType() {
        applyFilters();
    }

    @FXML
    private void applySearchAction() {
        applyFilters();
    }

    public void displayCours(List<Cour> coursList) {
        coursGrid.getChildren().clear();

        if (coursList.isEmpty()) {
            LocalDate selectedDate = dateFilterPicker.getValue();
            if (selectedDate != null) {
                noResultsLabel.setText("Aucun cours trouvé pour la date : " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                noResultsLabel.setText("Aucun cours trouvé");
            }
            noResultsLabel.setVisible(true);
            System.out.println("Aucun cours à afficher - Affichage du label : " + noResultsLabel.getText());
            return;
        }

        noResultsLabel.setVisible(false);
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
            boolean isParticipated = participatedCoursIds.contains(cour.getId());
            System.out.println("Affichage cours ID: " + cour.getId() + ", Participé: " + isParticipated);
            if (isParticipated) {
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

    private void handleParticiperAction(Cour cour) {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            // Vérifier si l'utilisateur est déjà inscrit
            List<Integer> currentParticipations = courService.recupererParticipations(loggedInUser.getId());
            if (currentParticipations.contains(cour.getId())) {
                showAlert("Information", "Vous êtes déjà inscrit à ce cours : " + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini"));
                return;
            }

            courService.ajouterParticipation(cour.getId());
            refreshParticipations();
            System.out.println("Participation ajoutée pour cours ID: " + cour.getId());

            String userEmail = loggedInUser.getEmail();
            String subject = "Confirmation de participation au cours";
            String plainText = "Bonjour " + loggedInUser.getNom() + ",\n\n" +
                    "Votre participation au cours \"" + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "\" a été confirmée avec succès !\n\n" +
                    "Détails du cours :\n" +
                    "- Cours : " + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "\n" +
                    "- Coût : " + cour.getCout() + " DT\n" +
                    "- Date de début : " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "Non défini") + "\n" +
                    "- Date de fin : " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "Non défini") + "\n" +
                    "- Email : " + userEmail + "\n\n" +
                    "Nous vous remercions de votre participation et avons hâte de vous accueillir !\n" +
                    "Cordialement,\nL'équipe Cinema";

            String htmlContent = "<!DOCTYPE html>" +
                    "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<style>body { font-family: Arial, Helvetica, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    ".container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                    ".header { background: #007bff; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }" +
                    ".header h1 { color: #ffffff; margin: 10px 0; font-size: 24px; }" +
                    ".content { padding: 20px; }" +
                    ".content h2 { color: #333; font-size: 20px; }" +
                    ".content p { color: #555; line-height: 1.6; }" +
                    ".details-table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
                    ".details-table th, .details-table td { padding: 10px; border: 1px solid #ddd; text-align: left; }" +
                    ".details-table th { background: #007bff; color: #ffffff; }" +
                    ".footer { background: #f4f4f4; padding: 10px; text-align: center; border-radius: 0 0 8px 8px; }" +
                    ".footer p { color: #777; font-size: 12px; }" +
                    ".footer a { color: #007bff; text-decoration: none; }" +
                    "</style></head><body><div class='container'><div class='header'><h1>Confirmation de participation</h1></div>" +
                    "<div class='content'><h2>Bonjour " + loggedInUser.getNom() + ",</h2>" +
                    "<p>Votre participation au cours <strong>" + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "</strong> a été confirmée avec succès !</p>" +
                    "<p>Voici les détails de votre participation :</p>" +
                    "<table class='details-table'>" +
                    "<tr><th>Cours</th><td>" + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "</td></tr>" +
                    "<tr><th>Coût</th><td>" + cour.getCout() + " DT</td></tr>" +
                    "<tr><th>Date de début</th><td>" + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "Non défini") + "</td></tr>" +
                    "<tr><th>Date de fin</th><td>" + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "Non défini") + "</td></tr>" +
                    "<tr><th>Email</th><td>" + userEmail + "</td></tr></table>" +
                    "<p>Nous vous remercions de votre participation et avons hâte de vous accueillir !</p></div>" +
                    "<div class='footer'><p>Contactez-nous : <a href='mailto:support@cinema.com'>support@cinema.com</a></p>" +
                    "<p>© 2025 Cinema. Tous droits réservés.</p></div></div></body></html>";

            new Thread(() -> {
                try {
                    sendEmail(userEmail, subject, plainText, htmlContent);
                    Platform.runLater(() -> showAlert("Succès", "Vous avez participé au cours : " +
                            (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") +
                            ". Un email de confirmation a été envoyé à " + userEmail));
                } catch (MessagingException e) {
                    Platform.runLater(() -> showAlert("Avertissement", "Participation enregistrée, mais l'email de confirmation n'a pas pu être envoyé."));
                    e.printStackTrace();
                }
            }).start();

            displayCours(allCours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la participation au cours : " + e.getMessage());
        }
    }

    private void handleQuitterAction(Cour cour) {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            int userId = loggedInUser.getId();
            courService.supprimerParticipation(userId, cour.getId());
            refreshParticipations();
            System.out.println("Participation supprimée pour cours ID: " + cour.getId());

            String userEmail = loggedInUser.getEmail();
            String subject = "Confirmation d'annulation de participation au cours";
            String plainText = "Bonjour " + loggedInUser.getNom() + ",\n\n" +
                    "Votre participation au cours \"" + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "\" a été annulée avec succès.\n\n" +
                    "Détails du cours :\n" +
                    "- Cours : " + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "\n" +
                    "- Coût : " + cour.getCout() + " DT\n" +
                    "- Date de début : " + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "Non défini") + "\n" +
                    "- Date de fin : " + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "Non défini") + "\n" +
                    "- Email : " + userEmail + "\n\n" +
                    "Nous sommes désolés de vous voir partir. Si vous changez d'avis, vous pouvez vous réinscrire à tout moment.\n" +
                    "Cordialement,\nL'équipe Cinema";

            String htmlContent = "<!DOCTYPE html>" +
                    "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<style>body { font-family: Arial, Helvetica, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    ".container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                    ".header { background: #dc3545; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }" +
                    ".header h1 { color: #ffffff; margin: 10px 0; font-size: 24px; }" +
                    ".content { padding: 20px; }" +
                    ".content h2 { color: #333; font-size: 20px; }" +
                    ".content p { color: #555; line-height: 1.6; }" +
                    ".details-table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
                    ".details-table th, .details-table td { padding: 10px; border: 1px solid #ddd; text-align: left; }" +
                    ".details-table th { background: #dc3545; color: #ffffff; }" +
                    ".footer { background: #f4f4f4; padding: 10px; text-align: center; border-radius: 0 0 8px 8px; }" +
                    ".footer p { color: #777; font-size: 12px; }" +
                    ".footer a { color: #dc3545; text-decoration: none; }" +
                    "</style></head><body><div class='container'><div class='header'><h1>Confirmation d'annulation</h1></div>" +
                    "<div class='content'><h2>Bonjour " + loggedInUser.getNom() + ",</h2>" +
                    "<p>Votre participation au cours <strong>" + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "</strong> a été annulée avec succès.</p>" +
                    "<p>Voici les détails du cours annulé :</p>" +
                    "<table class='details-table'>" +
                    "<tr><th>Cours</th><td>" + (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") + "</td></tr>" +
                    "<tr><th>Coût</th><td>" + cour.getCout() + " DT</td></tr>" +
                    "<tr><th>Date de début</th><td>" + (cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "Non défini") + "</td></tr>" +
                    "<tr><th>Date de fin</th><td>" + (cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "Non défini") + "</td></tr>" +
                    "<tr><th>Email</th><td>" + userEmail + "</td></tr></table>" +
                    "<p>Nous sommes désolés de vous voir partir. Si vous changez d'avis, vous pouvez vous réinscrire à tout moment.</p></div>" +
                    "<div class='footer'><p>Contactez-nous : <a href='mailto:support@cinema.com'>support@cinema.com</a></p>" +
                    "<p>© 2025 Cinema. Tous droits réservés.</p></div></div></body></html>";

            new Thread(() -> {
                try {
                    sendEmail(userEmail, subject, plainText, htmlContent);
                    Platform.runLater(() -> showAlert("Succès", "Vous avez quitté le cours : " +
                            (cour.getTypeCour() != null ? cour.getTypeCour() : "Type non défini") +
                            ". Un email de confirmation a été envoyé à " + userEmail));
                } catch (MessagingException e) {
                    Platform.runLater(() -> showAlert("Avertissement", "Annulation enregistrée, mais l'email de confirmation n'a pas pu être envoyé."));
                    e.printStackTrace();
                }
            }).start();

            displayCours(allCours);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'annulation de la participation : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goCourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            AffichageListCoursFront controller = loader.getController();
            controller.refreshParticipations();
            controller.displayCours(controller.allCours);
            Stage stage = (Stage) courSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers la page Cours : " + e.getMessage());
        }
    }

    @FXML
    private void goSeanceAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListSeancesFront.fxml"));
            Parent root = loader.load();
            AffichageListSeancesFront controller = loader.getController();
            controller.initializeWithCour(null); // Affiche toutes les séances (pas de cours spécifique sélectionné)
            Stage stage = (Stage) seanceSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers la page Séances : " + e.getMessage());
        }
    }

    @FXML
    private void toggleSubButtons() {
        boolean areSubButtonsVisible = courSubButton.isVisible();
        courSubButton.setVisible(!areSubButtonsVisible);
        seanceSubButton.setVisible(!areSubButtonsVisible);
    }

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

    @FXML
    private void goParticipationAction() {
        try {
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                showAlert("Erreur", "Veuillez vous connecter pour voir vos participations.");
                return;
            }

            List<Integer> participationIds = courService.recupererParticipations(loggedInUser.getId());
            System.out.println("Participations récupérées pour popup (utilisateur ID " + loggedInUser.getId() + "): " + participationIds);
            List<Cour> participatedCourses = new ArrayList<>();
            for (Cour cour : allCours) {
                if (participationIds.contains(cour.getId())) {
                    participatedCourses.add(cour);
                }
            }
            System.out.println("Cours participés pour popup: " + participatedCourses.size());

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

            popupStage.setOnCloseRequest(event -> popupStage.close());
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'affichage des participations : " + e.getMessage());
        }
    }

    @FXML
    private void logoutAction() {
        SessionManager.getInstance().setLoggedInUser(null);
        Platform.exit();
        System.exit(0);
    }

    private void sendEmail(String to, String subject, String plainText, String htmlContent) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.debug", "true");

        final String username = "farahboukesra4@gmail.com";
        final String password = "afgdmorpxwnlinxa";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(plainText);
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw e;
        }
    }



}