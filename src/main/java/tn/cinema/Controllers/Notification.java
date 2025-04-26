package tn.cinema.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import tn.cinema.services.NotificationService;

import java.io.IOException;
import java.util.List;

public class Notification {

    @FXML
    private ListView<String> notificationListView;

    private final NotificationService notificationService = NotificationService.getInstance();
    @FXML
    public void initialize() {
        chargerNotifications(); // recharge les notifications automatiquement à l'ouverture
    }

    // 👇 Appelée pour recharger les notifications dans la liste
    public void chargerNotifications() {
        List<String> notifications = notificationService.getNotifications();
        notificationListView.setItems(FXCollections.observableArrayList(notifications));
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) notificationListView.getScene().getWindow();
        stage.close();
    }
}
