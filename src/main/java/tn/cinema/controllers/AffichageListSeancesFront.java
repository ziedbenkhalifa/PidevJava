package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
import tn.cinema.entities.Cour;
import tn.cinema.entities.Seance;
import tn.cinema.entities.User;
import tn.cinema.services.CourService;
import tn.cinema.services.SeanceService;
import tn.cinema.utils.SessionManager;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AffichageListSeancesFront {

    private static final Logger LOGGER = Logger.getLogger(AffichageListSeancesFront.class.getName());

    @FXML
    private ComboBox<Cour> courComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label monthLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Button courSubButton;

    @FXML
    private Button seanceSubButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button applySearchButton;

    @FXML
    private Button retourButton;

    @FXML
    private Button checkReminderButton;

    @FXML
    private Button showTomorrowButton; // Nouveau bouton pour afficher les séances de demain

    private YearMonth currentMonth;
    private Cour selectedCour;
    private final SeanceService seanceService = new SeanceService();
    private final CourService courService = new CourService();
    private List<Cour> allCours = new ArrayList<>();
    private List<Seance> allSeances = new ArrayList<>();
    private boolean showOnlyTomorrow = false; // Indicateur pour afficher uniquement les séances de demain

    @FXML
    public void initialize() {
        LOGGER.info("Initialisation de AffichageListSeancesFront");
        currentMonth = YearMonth.now();

        List<String> months = new ArrayList<>();
        for (Month month : Month.values()) {
            months.add(month.toString());
        }
        monthComboBox.getItems().addAll(months);
        monthComboBox.setValue(currentMonth.getMonth().toString());

        List<Integer> years = IntStream.rangeClosed(2020, 2030)
                .boxed()
                .collect(Collectors.toList());
        yearComboBox.getItems().addAll(years);
        yearComboBox.setValue(currentMonth.getYear());

        datePicker.setValue(LocalDate.now());

        try {
            allCours = courService.recuperer();
            allSeances = seanceService.recuperer();
            courComboBox.getItems().addAll(allCours);
            courComboBox.setOnAction(event -> {
                selectedCour = courComboBox.getValue();
                LOGGER.info("Cours sélectionné : " + (selectedCour != null ? selectedCour.getId() : "Tous"));
                updateCalendar();
            });
            courComboBox.getItems().add(0, null);
            courComboBox.setValue(null);
            LOGGER.info("ComboBox initialisée avec " + courComboBox.getItems().size() + " cours");

            checkSessionReminders();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des cours ou séances", e);
            showAlert("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
        updateCalendar();
    }

    public void initializeWithCour(Cour cour) {
        LOGGER.info("Initialisation avec cours ID: " + (cour != null ? cour.getId() : "null"));
        selectedCour = cour;
        try {
            allCours = courService.recuperer();
            allSeances = seanceService.recuperer();
            courComboBox.getItems().addAll(allCours);
            courComboBox.getItems().add(0, null);
            courComboBox.setValue(cour);
            LOGGER.info("ComboBox initialisée avec cours sélectionné ID: " + (cour != null ? cour.getId() : "null"));

            checkSessionReminders();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des cours ou séances", e);
            showAlert("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
        updateCalendar();
    }

    private void updateCalendar() {
        LOGGER.info("Mise à jour du calendrier pour " + currentMonth);
        calendarGrid.getChildren().clear();
        calendarGrid.add(new Label("Dim") {{ getStyleClass().add("day-header"); }}, 0, 0);
        calendarGrid.add(new Label("Lun") {{ getStyleClass().add("day-header"); }}, 1, 0);
        calendarGrid.add(new Label("Mar") {{ getStyleClass().add("day-header"); }}, 2, 0);
        calendarGrid.add(new Label("Mer") {{ getStyleClass().add("day-header"); }}, 3, 0);
        calendarGrid.add(new Label("Jeu") {{ getStyleClass().add("day-header"); }}, 4, 0);
        calendarGrid.add(new Label("Ven") {{ getStyleClass().add("day-header"); }}, 5, 0);
        calendarGrid.add(new Label("Sam") {{ getStyleClass().add("day-header"); }}, 6, 0);

        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        List<Seance> seances;
        try {
            if (showOnlyTomorrow) {
                LocalDate tomorrow = LocalDate.now().plusDays(1);
                seances = allSeances.stream()
                        .filter(s -> s.getDateSeance() != null && s.getDateSeance().equals(tomorrow))
                        .toList();
            } else {
                seances = selectedCour != null ? allSeances.stream()
                        .filter(s -> s.getCour() != null && s.getCour().getId() == selectedCour.getId())
                        .toList() : allSeances;
            }
            LOGGER.info("Nombre de séances récupérées : " + seances.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du filtrage des séances", e);
            showAlert("Erreur", "Erreur lors du chargement des séances : " + e.getMessage());
            return;
        }

        if (showOnlyTomorrow) {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            VBox dayVBox = new VBox(5);
            dayVBox.getStyleClass().add("day-box");

            Label dayLabel = new Label("Demain (" + tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
            dayLabel.getStyleClass().add("day-label");

            List<Seance> tomorrowSeances = seances.stream()
                    .filter(s -> s.getDateSeance().equals(tomorrow))
                    .toList();

            if (!tomorrowSeances.isEmpty()) {
                for (Seance seance : tomorrowSeances) {
                    String seanceText = (seance.getDuree() != null ? seance.getDuree().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A")
                            + " - " + seance.getObjectifs();
                    Label seanceLabel = new Label(seanceText);
                    seanceLabel.getStyleClass().add("seance-label");
                    seanceLabel.setOnMouseClicked(event -> saveSeanceToCalendar(seance));
                    dayVBox.getChildren().add(seanceLabel);
                }
                dayVBox.getChildren().add(0, dayLabel);
                calendarGrid.add(dayVBox, 0, 1, 7, 1);
            } else {
                dayVBox.getChildren().add(dayLabel);
                dayVBox.getChildren().add(new Label("Aucune séance prévue pour demain."));
                calendarGrid.add(dayVBox, 0, 1, 7, 1);
            }
        } else {
            int row = 1;
            for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
                LocalDate date = currentMonth.atDay(day);
                VBox dayVBox = new VBox(5);
                dayVBox.getStyleClass().add("day-box");

                Label dayLabel = new Label(String.valueOf(day));
                dayLabel.getStyleClass().add("day-label");

                List<Seance> daySeances = seances.stream()
                        .filter(s -> s.getDateSeance() != null && s.getDateSeance().equals(date))
                        .toList();

                if (!daySeances.isEmpty()) {
                    for (Seance seance : daySeances) {
                        String seanceText = (seance.getDuree() != null ? seance.getDuree().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A")
                                + " - " + seance.getObjectifs();
                        Label seanceLabel = new Label(seanceText);
                        seanceLabel.getStyleClass().add("seance-label");
                        seanceLabel.setOnMouseClicked(event -> saveSeanceToCalendar(seance));
                        dayVBox.getChildren().add(seanceLabel);
                    }
                    dayVBox.getChildren().add(0, dayLabel);
                    dayVBox.getStyleClass().remove("day-box");
                    dayVBox.getStyleClass().add("day-box-with-seance");
                } else {
                    dayVBox.getChildren().add(dayLabel);
                }

                calendarGrid.add(dayVBox, (dayOfWeek + day - 1) % 7, row + ((dayOfWeek + day - 1) / 7));
            }
        }
    }

    private void saveSeanceToCalendar(Seance seance) {
        try {
            Calendar calendar = new Calendar();
            calendar.getProperties().add(new ProdId("-//xAI//CinemaApp//FR"));
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            LocalDateTime startDateTime = LocalDateTime.of(seance.getDateSeance(), seance.getDuree() != null ? seance.getDuree() : LocalTime.of(0, 0));
            LocalDateTime endDateTime = startDateTime.plusHours(1);
            DateTime start = new DateTime(java.util.Date.from(startDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()));
            DateTime end = new DateTime(java.util.Date.from(endDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()));

            String eventSummary = "Séance: " + seance.getObjectifs();
            VEvent event = new VEvent(start, end, eventSummary);

            RandomUidGenerator ug = new RandomUidGenerator();
            event.getProperties().add(ug.generateUid());

            String description = "Cours: " + (seance.getCour() != null ? seance.getCour().getTypeCour() : "N/A") +
                    "\nObjectifs: " + seance.getObjectifs();
            event.getProperties().add(new net.fortuna.ical4j.model.property.Description(description));

            event.getProperties().add(new net.fortuna.ical4j.model.property.Location("Cinéma"));

            calendar.getComponents().add(event);

            File tempFile = File.createTempFile("seance_", ".ics");

            try (FileOutputStream fout = new FileOutputStream(tempFile)) {
                CalendarOutputter outputter = new CalendarOutputter();
                outputter.output(calendar, fout);
            }

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(tempFile);
                    showAlert("Succès", "La séance a été ajoutée au calendrier.");
                } else {
                    showAlert("Erreur", "L'ouverture automatique n'est pas supportée. Fichier : " + tempFile.getAbsolutePath());
                }
            } else {
                showAlert("Erreur", "L'ouverture automatique n'est pas supportée. Fichier : " + tempFile.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création ou de l'ouverture du fichier .ics", e);
            showAlert("Erreur", "Erreur lors de l'ouverture du calendrier : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de l'enregistrement de la séance", e);
            showAlert("Erreur", "Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    private void checkSessionReminders() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1); // Rappel uniquement pour demain

            List<Seance> upcomingSeances = allSeances.stream()
                    .filter(s -> s.getDateSeance() != null && s.getDateSeance().equals(tomorrow))
                    .collect(Collectors.toList());

            if (upcomingSeances.isEmpty()) {
                showAlert("Information", "Aucune séance prévue pour demain.");
            } else {
                StringBuilder reminderMessage = new StringBuilder("Rappel : Séances prévues pour demain :\n\n");
                for (Seance seance : upcomingSeances) {
                    String seanceInfo = (seance.getDuree() != null ? seance.getDuree().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A")
                            + " - " + seance.getObjectifs() +
                            " (Date: " + seance.getDateSeance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")" +
                            " [Cours: " + (seance.getCour() != null ? seance.getCour().getTypeCour() : "N/A") + "]";
                    reminderMessage.append("- ").append(seanceInfo).append("\n");
                }
                showAlert("Rappel de séances", reminderMessage.toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification des rappels", e);
            showAlert("Erreur", "Erreur lors de la vérification des rappels : " + e.getMessage());
        }
    }

    @FXML
    private void showTomorrowSeances() {
        showOnlyTomorrow = true;
        updateCalendar();
    }

    @FXML
    private void updateMonthFromComboBox() {
        String selectedMonth = monthComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();
        if (selectedMonth != null && selectedYear != null) {
            Month month = Month.valueOf(selectedMonth);
            currentMonth = YearMonth.of(selectedYear, month);
            LOGGER.info("Mois mis à jour via ComboBox : " + currentMonth);
            showOnlyTomorrow = false; // Réinitialiser pour afficher le mois complet
            updateCalendar();
            datePicker.setValue(currentMonth.atDay(1));
        }
    }

    @FXML
    private void updateMonthFromDatePicker() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            currentMonth = YearMonth.from(selectedDate);
            LOGGER.info("Mois mis à jour via DatePicker : " + currentMonth);
            monthComboBox.setValue(currentMonth.getMonth().toString());
            yearComboBox.setValue(currentMonth.getYear());
            showOnlyTomorrow = false; // Réinitialiser pour afficher le mois complet
            updateCalendar();
        }
    }

    @FXML
    private void handleSeanceAction() {
        LOGGER.info("Rafraîchissement du calendrier");
        showOnlyTomorrow = false; // Réinitialiser pour afficher le mois complet
        updateCalendar();
    }

    @FXML
    private void applySearchAction() {
        LOGGER.info("Recherche appliquée : " + searchField.getText());
    }

    @FXML
    private void toggleSubButtons() {
        LOGGER.info("Toggle des sous-boutons");
        boolean areSubButtonsVisible = courSubButton.isVisible();
        courSubButton.setVisible(!areSubButtonsVisible);
        seanceSubButton.setVisible(!areSubButtonsVisible);
    }

    @FXML
    private void goCourAction() {
        try {
            LOGGER.info("Tentative de chargement de AffichageListCoursFront.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            AffichageListCoursFront controller = loader.getController();
            controller.refreshParticipations();
            controller.displayCours(allCours);
            Stage stage = (Stage) courSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            LOGGER.info("Redirection vers AffichageListCoursFront.fxml réussie");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de AffichageListCoursFront.fxml", e);
            showAlert("Erreur", "Erreur lors de la navigation vers la page Cours : " + e.getMessage());
        }
    }

    @FXML
    private void retourCoursAction() {
        try {
            LOGGER.info("Tentative de retour vers AffichageListCoursFront.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            AffichageListCoursFront controller = loader.getController();
            controller.refreshParticipations();
            controller.displayCours(allCours);
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            LOGGER.info("Retour vers AffichageListCoursFront.fxml réussi");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du retour vers AffichageListCoursFront.fxml", e);
            showAlert("Erreur", "Erreur lors du retour vers la page Cours : " + e.getMessage());
        }
    }

    @FXML
    private void goParticipationAction() {
        try {
            LOGGER.info("Tentative d'affichage des participations");
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                LOGGER.warning("Aucun utilisateur connecté");
                showAlert("Erreur", "Veuillez vous connecter pour voir vos participations.");
                return;
            }

            List<Integer> participationIds = courService.recupererParticipations(loggedInUser.getId());
            LOGGER.info("Participations récupérées pour utilisateur ID " + loggedInUser.getId() + ": " + participationIds);
            List<Cour> participatedCourses = new ArrayList<>();
            for (Cour cour : allCours) {
                if (participationIds.contains(cour.getId())) {
                    participatedCourses.add(cour);
                }
            }
            LOGGER.info("Cours participés : " + participatedCourses.size());

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

            popupStage.setOnCloseRequest(event -> {
                LOGGER.info("Fermeture de la popup de participations");
                popupStage.close();
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de PopupParticipation.fxml", e);
            showAlert("Erreur", "Erreur lors de l'affichage des participations : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de l'affichage des participations", e);
            showAlert("Erreur", "Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    private void logoutAction() {
        try {
            LOGGER.info("Déconnexion de l'utilisateur");
            SessionManager.getInstance().setLoggedInUser(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) seanceSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
            LOGGER.info("Redirection vers login.fxml réussie");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de login.fxml", e);
            showAlert("Erreur", "Impossible de charger la page de connexion : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de la déconnexion", e);
            showAlert("Erreur", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (title.equals("Erreur")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}