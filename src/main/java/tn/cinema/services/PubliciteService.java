package tn.cinema.services;

import tn.cinema.entities.Publicite;
import tn.cinema.entities.User;
import tn.cinema.tools.Mydatabase;
import tn.cinema.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PubliciteService implements IServices<Publicite> {
    Connection cnx;

    public PubliciteService() {
        cnx = Mydatabase.getInstance().getCnx();
        if (cnx == null) {
            System.err.println("Database connection is null in PubliciteService!");
        } else {
            System.out.println("Database connection established successfully in PubliciteService.");
        }
    }

    @Override
    public void ajouter(Publicite publicite) {
    }

    public void ajouterpub(Publicite publicite) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Database connection is null, cannot add Publicite.");
        }
        String sql = "INSERT INTO publicite(demande_id, date_debut, date_fin, support, montant) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, publicite.getDemandeId());
            ps.setDate(2, publicite.getDateDebut());
            ps.setDate(3, publicite.getDateFin());
            ps.setString(4, publicite.getSupport());
            ps.setFloat(5, publicite.getMontant());
            ps.executeUpdate();
            System.out.println("Publicité ajoutée avec succès !");
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de la publicité : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
    }

    public void supprimerpub(int id) {
        if (cnx == null) {
            throw new RuntimeException("Database connection is null, cannot delete Publicite.");
        }
        String sql = "DELETE FROM publicite WHERE id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Publicité supprimée avec succès !");
            } else {
                System.out.println("Aucune publicité trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Publicite publicite) {
    }

    public void modifierpub(int publiciteId, Publicite updatedPublicite) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Database connection is null, cannot update Publicite.");
        }
        String sql = "UPDATE publicite SET date_debut = ?, date_fin = ?, support = ?, montant = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setDate(1, updatedPublicite.getDateDebut());
            ps.setDate(2, updatedPublicite.getDateFin());
            ps.setString(3, updatedPublicite.getSupport());
            ps.setFloat(4, updatedPublicite.getMontant());
            ps.setInt(5, publiciteId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Publicité mise à jour avec succès !");
            }
        }
    }

    @Override
    public List<Publicite> recuperer() throws SQLException {
        return List.of();
    }

    public List<Publicite> recupererpub() throws SQLException {
        if (cnx == null) {
            throw new SQLException("Database connection is null, cannot retrieve Publicites.");
        }
        String sql = "SELECT * FROM publicite";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Publicite> publicite = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            int demandeId = rs.getInt(2);
            Date dateDebut = rs.getDate(3);
            Date dateFin = rs.getDate(4);
            String support = rs.getString(5);
            Float montant = rs.getFloat(6);
            Publicite p = new Publicite(id, demandeId, dateDebut, dateFin, support, montant);
            publicite.add(p);
        }
        return publicite;
    }


    public List<Publicite> recupererPublicitesParClient(int clientId) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Database connection is null, cannot retrieve Publicites.");
        }
        String sql = "SELECT p.* FROM publicite p " +
                "JOIN demande d ON p.demande_id = d.id " +
                "WHERE d.user_id = ?";
        PreparedStatement pstmt = cnx.prepareStatement(sql);
        pstmt.setInt(1, clientId);
        ResultSet rs = pstmt.executeQuery();
        List<Publicite> publicites = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int demandeId = rs.getInt("demande_id");
            Date dateDebut = rs.getDate("date_debut");
            Date dateFin = rs.getDate("date_fin");
            String support = rs.getString("support");
            Float montant = rs.getFloat("montant");
            Publicite p = new Publicite(id, demandeId, dateDebut, dateFin, support, montant);
            publicites.add(p);
        }
        return publicites;
    }

    public List<Publicite> recuperePublicitesParClient() throws SQLException {
        if (cnx == null) {
            throw new SQLException("Database connection is null, cannot retrieve Publicites.");
        }

        // Get logged-in user's ID from SessionManager
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté.");
        }
        int userId = loggedInUser.getId();

        String sql = "SELECT p.* FROM publicite p " +
                "JOIN demande d ON p.demande_id = d.id " +
                "WHERE d.user_id = ?";
        PreparedStatement pstmt = cnx.prepareStatement(sql);
        pstmt.setInt(1, userId); // Dynamic user_id
        ResultSet rs = pstmt.executeQuery();

        List<Publicite> publicites = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int demandeId = rs.getInt("demande_id");
            Date dateDebut = rs.getDate("date_debut");
            Date dateFin = rs.getDate("date_fin");
            String support = rs.getString("support");
            Float montant = rs.getFloat("montant");
            Publicite p = new Publicite(id, demandeId, dateDebut, dateFin, support, montant);
            publicites.add(p);
        }

        return publicites;
    }



    public boolean existsByDemandeId(int demandeId) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Database connection is null, cannot check for Publicite.");
        }
        String query = "SELECT COUNT(*) FROM publicite WHERE demande_id = ?";
        System.out.println("Executing query: " + query + " with demande_id = " + demandeId);
        PreparedStatement stmt = cnx.prepareStatement(query);
        stmt.setInt(1, demandeId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            System.out.println("Found " + count + " Publicite(s) for demande_id = " + demandeId);
            return count > 0;
        }
        System.out.println("No Publicite found for demande_id = " + demandeId);
        return false;
    }
}