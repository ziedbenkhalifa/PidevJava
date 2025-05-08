package tn.cinema.services;

import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatBotServices {

    private Connection connection;

    // Constructeur pour initialiser la connexion à la base de données
    public ChatBotServices() {
        this.connection = Mydatabase.getInstance().getCnx();
    }

    // Méthode pour traiter la commande de l'utilisateur
    public String processCommand(String input) {
        if (input.toLowerCase().contains("équipement")) {
            return getEquipementStatus(input);
        } else if (input.toLowerCase().contains("maintenance")) {
            return getMaintenanceSchedule();
        } else if (input.toLowerCase().contains("salle")) {
            return getSalleStatus(input);
        } else {
            return "Commande non reconnue. Essayez quelque chose comme 'Équipement' ou 'Maintenance'.";
        }
    }

    // Récupérer le statut des équipements depuis la base de données
    private String getEquipementStatus(String input) {
        List<String> equipements = new ArrayList<>();
        String query = "SELECT nom, type, etat FROM equipement WHERE 1=1";

        // Vérification si l'utilisateur a demandé des équipements en panne ou en maintenance
        if (input.toLowerCase().contains("panne")) {
            query += " AND etat = 'en panne'";
        } else if (input.toLowerCase().contains("maintenance")) {
            query += " AND etat = 'en maintenance'";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nom = rs.getString("nom");
                String type = rs.getString("type");
                String etat = rs.getString("etat");
                equipements.add(nom + " (" + type + ") - État: " + etat);
            }

            if (equipements.isEmpty()) {
                return "Tous les équipements sont en bon état.";
            }

            return "Équipements en panne ou en maintenance :\n" + String.join("\n", equipements);

        } catch (SQLException e) {
            return "Erreur lors de la récupération des équipements : " + e.getMessage();
        }
    }

    // Récupérer le planning de maintenance des salles depuis la base de données
    private String getMaintenanceSchedule() {
        List<String> sallesEnMaintenance = new ArrayList<>();
        String query = "SELECT nom_salle, statut FROM salle WHERE statut = 'En maintenance'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String nomSalle = rs.getString("nom_salle");
                String statut = rs.getString("statut");
                sallesEnMaintenance.add(nomSalle + " : Statut - " + statut);
            }

            if (sallesEnMaintenance.isEmpty()) {
                return "Aucune maintenance prévue pour le moment.";
            }

            return "Planning de maintenance prévu :\n" + String.join("\n", sallesEnMaintenance);

        } catch (SQLException e) {
            return "Erreur lors de la récupération du planning de maintenance : " + e.getMessage();
        }
    }

    // Récupérer l'état des salles depuis la base de données
    private String getSalleStatus(String input) {
        List<String> salles = new ArrayList<>();
        String query = "SELECT nom_salle, disponibilite, type_salle, emplacement FROM salle WHERE 1=1";

        // Vérification de la disponibilité des salles
        if (input.toLowerCase().contains("disponible")) {
            query += " AND disponibilite = 'oui'";
        } else if (input.toLowerCase().contains("indisponible")) {
            query += " AND disponibilite = 'non'";
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String nomSalle = rs.getString("nom_salle");
                String disponibilite = rs.getString("disponibilite");
                String typeSalle = rs.getString("type_salle");
                String emplacement = rs.getString("emplacement");

                salles.add(nomSalle + " - Type : " + typeSalle + ", Emplacement : " + emplacement + ", Disponibilité : " + disponibilite);
            }

            if (salles.isEmpty()) {
                return "Aucune salle disponible avec ces critères.";
            }

            return "État des salles :\n" + String.join("\n", salles);

        } catch (SQLException e) {
            return "Erreur lors de la récupération de l'état des salles : " + e.getMessage();
        }
    }
}