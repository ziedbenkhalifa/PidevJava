package tn.cinema.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static NotificationService instance;
    private List<String> notifications;
    private Connection connection;

    private NotificationService() {
        notifications = new ArrayList<>();
        System.out.println("🚨 Instance NotificationService créée");
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            System.out.println("⚠️ Nouvelle instance de NotificationService");
            instance = new NotificationService();
        } else {
            System.out.println("✅ Instance NotificationService existante réutilisée");
        }
        return instance;
    }


    public void setConnection(Connection connection) {
        this.connection = connection;
        if (this.connection != null) {
            chargerNotificationsDepuisBaseDeDonnees();
        } else {
            System.out.println("⚠️ Connexion non définie.");
        }
    }

    public void ajouterNotification(String message) {
        notifications.add(message);
        sauvegarderNotificationDansBase(message);
    }

    private void sauvegarderNotificationDansBase(String message) {
        if (connection == null) {
            System.out.println("⚠️ Connexion non définie.");
            return;
        }

        String query = "INSERT INTO notification (message, created_at, expires_at) VALUES (?, NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY))";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, message);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Notification sauvegardée dans la base.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la sauvegarde de la notification.");
            e.printStackTrace();
        }
    }

    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    private void chargerNotificationsDepuisBaseDeDonnees() {
        if (connection == null) {
            System.out.println("⚠️ Connexion non définie.");
            return;
        }

        String query = "SELECT message FROM notification WHERE expires_at IS NULL OR expires_at > NOW()";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            notifications.clear();
            while (rs.next()) {
                notifications.add(rs.getString("message"));
            }
            System.out.println("✅ Notifications chargées depuis la base.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement notifications.");
            e.printStackTrace();
        }
    }
}