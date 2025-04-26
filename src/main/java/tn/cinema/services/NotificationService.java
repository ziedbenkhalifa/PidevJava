package tn.cinema.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static NotificationService instance;
    private List<String> notifications;
    private Connection connection; // Assumes a connection is provided via constructor or setter

    private NotificationService() {
        notifications = new ArrayList<>();
        System.out.println("🚨 Instance NotificationService créée");
        chargerNotificationsDepuisBaseDeDonnees();  // Charger les notifications existantes depuis la DB
    }

    // Méthode pour obtenir l'instance unique de NotificationService
    public static NotificationService getInstance() {
        if (instance == null) {
            System.out.println("⚠️ Nouvelle instance de NotificationService");
            instance = new NotificationService();
        } else {
            System.out.println("✅ Instance NotificationService existante réutilisée");
        }
        return instance;
    }

    // Setter pour la connexion (ou tu peux passer la connexion via le constructeur)
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // Ajouter une notification
    public void ajouterNotification(String notification) {
        notifications.add(notification);
        System.out.println("Notification ajoutée : " + notification); // Pour debug
        sauvegarderNotificationDansBase(notification); // Sauvegarder dans la base de données
    }

    // Retourner la liste des notifications
    public List<String> getNotifications() {
        System.out.println("🔍 getNotifications() appelé, contenu = " + notifications);
        return new ArrayList<>(notifications); // Pour éviter les modifications externes
    }

    // Supprimer toutes les notifications
    public void clearNotifications() {
        notifications.clear();
        viderNotificationsDansBase(); // Vider les notifications expirées de la base de données
    }

    // Sauvegarder la notification dans la base de données
    private void sauvegarderNotificationDansBase(String message) {
        if (connection == null) {
            System.out.println("⚠️ La connexion à la base de données n'est pas définie.");
            return;
        }

        String query = "INSERT INTO notification (message, created_at, expires_at) VALUES (?, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY))"; // expire après 7 jours

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, message);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Notification sauvegardée dans la base de données.");
            } else {
                System.out.println("Échec de l'insertion de la notification.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Charger les notifications depuis la base de données
    private void chargerNotificationsDepuisBaseDeDonnees() {
        if (connection == null) {
            System.out.println("⚠️ La connexion à la base de données n'est pas définie.");
            return;
        }

        String query = "SELECT message FROM notification WHERE expires_at > NOW()"; // Charger les notifications non expirées
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                notifications.add(rs.getString("message"));
            }
            System.out.println("Notifications chargées depuis la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vider les notifications expirées dans la base de données
    private void viderNotificationsDansBase() {
        if (connection == null) {
            System.out.println("⚠️ La connexion à la base de données n'est pas définie.");
            return;
        }

        String query = "DELETE FROM notification WHERE expires_at <= NOW()"; // Supprimer les notifications expirées
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.executeUpdate();
            System.out.println("Notifications expirées supprimées.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
