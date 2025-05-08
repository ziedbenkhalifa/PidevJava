package tn.cinema.controllers;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.cinema.services.NotificationService;
import tn.cinema.tools.Mydatabase;

import java.sql.*;

public class Notification {

    @FXML
    private ListView<String> notificationListView;

    @FXML
    private TextField tfNouvelleNotification;

    @FXML
    private Label notificationCount;

    @FXML
    private Text notificationEmoji; // Nouvel ajout pour l'icône 🔔

    private NotificationService notificationService;
    private ObservableList<String> notifications = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        notificationService = NotificationService.getInstance();
        rafraichirNotifications();
        recupererEtAfficherNotifications(null);

        // Animation sur l'icône de notification 🔔
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.7), notificationEmoji);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();

        // Ajouter un listener pour détecter les clics sur les notifications
        notificationListView.setOnMouseClicked(event -> {
            if (notificationListView.getSelectionModel().getSelectedIndex() >= 0) {
                String selectedNotification = notificationListView.getSelectionModel().getSelectedItem();
                appliquerAnimationMarquee(selectedNotification);
                updateNotificationStyle();
            }
        });
    }

    @FXML
    private void ajouterNotification() {
        String message = tfNouvelleNotification.getText().trim();
        if (!message.isEmpty()) {
            notificationService.ajouterNotification(message);
            tfNouvelleNotification.clear();
            rafraichirNotifications();
        }
    }

    @FXML
    public void recupererEtAfficherNotifications(ActionEvent event) {
        notifications.clear();
        Connection connexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connexion = Mydatabase.getInstance().getCnx();
            String sql = "SELECT message FROM notification ORDER BY created_at DESC";
            ps = connexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String message = rs.getString("message");
                notifications.add(message);
            }

            notificationListView.setItems(notifications);

            // Animation : vibration au lieu de fade
            appliquerAnimationClignotement();

            // Mettre à jour le badge
            mettreAJourBadgeNotification(notifications.size());

            // Mise en forme : dernière notification en gras
            formaterDerniereNotification();

        } catch (SQLException e) {
            afficherErreur("Erreur SQL", "Impossible de récupérer les notifications !", e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Erreur de fermeture SQL : " + e.getMessage());
            }
        }
    }

    private void rafraichirNotifications() {
        recupererEtAfficherNotifications(null);
    }

    // Nouvelle animation : vibration horizontale
    private void appliquerAnimationClignotement() {
        TranslateTransition vibrate = new TranslateTransition(Duration.millis(60), notificationListView);
        vibrate.setByX(5);
        vibrate.setCycleCount(6); // 3 allers-retours
        vibrate.setAutoReverse(true);
        vibrate.play();
    }

    private void formaterDerniereNotification() {
        if (notifications.size() > 0) {
            notificationListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                        if (item.equals(notifications.get(0))) {
                            setStyle("-fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-font-weight: normal;");
                        }
                        if (item.equals(notificationListView.getSelectionModel().getSelectedItem())) {
                            setStyle("-fx-font-weight: normal;");
                        }
                    }
                }
            });
        }
    }

    private void appliquerAnimationMarquee(String notification) {
        ListCell<String> selectedCell = (ListCell<String>) notificationListView.getCellFactory().call(notificationListView);
        if (selectedCell != null) {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(5), selectedCell);
            transition.setByX(300);
            transition.setCycleCount(TranslateTransition.INDEFINITE);
            transition.play();
        }
    }

    private void mettreAJourBadgeNotification(int count) {
        notificationCount.setText(count + " nouvelles");
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) notificationListView.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String titre, String header, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void updateNotificationStyle() {
        for (int i = 0; i < notificationListView.getItems().size(); i++) {
            String item = notificationListView.getItems().get(i);
            if (item.equals(notificationListView.getSelectionModel().getSelectedItem())) {
                notificationListView.getItems().set(i, item);
            }
        }
        notificationListView.refresh();
    }
}