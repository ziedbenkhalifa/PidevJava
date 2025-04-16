package tn.cinema.services;

import tn.cinema.entities.Cour;
import tn.cinema.tools.Mydatabase;
import tn.cinema.utils.SessionManager;
import tn.cinema.entities.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourService implements IServices<Cour> {

    private Connection cnx;
    private String sql;
    private PreparedStatement ps;
    private Statement st;

    public CourService() {
        cnx = Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Cour cour) throws SQLException {
        sql = "INSERT INTO cour (type_cour, cout, date_debut, date_fin) VALUES (?, ?, ?, ?)";
        ps = cnx.prepareStatement(sql);
        ps.setString(1, cour.getTypeCour());
        ps.setDouble(2, cour.getCout());
        ps.setTimestamp(3, Timestamp.valueOf(cour.getDateDebut()));
        ps.setTimestamp(4, Timestamp.valueOf(cour.getDateFin()));
        ps.executeUpdate();
        System.out.println("Cour ajouté");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        sql = "DELETE FROM cour WHERE id = ?";
        ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Cour supprimé");
    }

    @Override
    public void modifier(Cour cour) throws SQLException {
        sql = "UPDATE cour SET type_cour = ?, cout = ?, date_debut = ?, date_fin = ? WHERE id = ?";
        ps = cnx.prepareStatement(sql);
        ps.setString(1, cour.getTypeCour());
        ps.setDouble(2, cour.getCout());
        ps.setTimestamp(3, Timestamp.valueOf(cour.getDateDebut()));
        ps.setTimestamp(4, Timestamp.valueOf(cour.getDateFin()));
        ps.setInt(5, cour.getId());
        ps.executeUpdate();
        System.out.println("Cour modifié");
    }

    @Override
    public List<Cour> recuperer() throws SQLException {
        sql = "SELECT * FROM cour";
        st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Cour> cours = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            String typeCour = rs.getString("type_cour");
            Double cout = rs.getDouble("cout");
            LocalDateTime dateDebut = rs.getTimestamp("date_debut").toLocalDateTime();
            LocalDateTime dateFin = rs.getTimestamp("date_fin").toLocalDateTime();

            Cour c = new Cour(id, typeCour, cout, dateDebut, dateFin);
            cours.add(c);
        }
        return cours;
    }

    public void ajouterParticipation(int courId) throws SQLException {
        // Get logged-in user's ID from SessionManager
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté.");
        }
        int userId = loggedInUser.getId(); // Utilisateur connecté

        String sql = "INSERT INTO participation (user_id, cour_id) VALUES (?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, courId);
            ps.executeUpdate();
            System.out.println("✅ Participation ajoutée avec succès pour l'utilisateur ID: " + userId + ", Cour ID: " + courId);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la participation: " + e.getMessage());
            throw e; // Re-throw to let the controller handle the error
        }
    }


    public void supprimerParticipation(int courId) throws SQLException {
        // Get logged-in user's ID from SessionManager
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté.");
        }
        int userId = loggedInUser.getId(); // Utilisateur connecté

        String sql = "DELETE FROM participation WHERE user_id = ? AND cour_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, courId);
            ps.executeUpdate();
            System.out.println("✅ Participation supprimée pour l'utilisateur ID: " + userId + ", Cour ID: " + courId);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la participation: " + e.getMessage());
            throw e;
        }
    }


    public List<Integer> recupererParticipations() throws SQLException {
        // Get logged-in user's ID from SessionManager
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté.");
        }
        int userId = loggedInUser.getId(); // Utilisateur connecté

        List<Integer> coursIds = new ArrayList<>();
        String sql = "SELECT cour_id FROM participation WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coursIds.add(rs.getInt("cour_id"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des participations: " + e.getMessage());
            throw e;
        }
        return coursIds;
    }

}