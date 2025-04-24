package tn.cinema.services;

import tn.cinema.entities.Demande;
import tn.cinema.entities.User;
import tn.cinema.tools.Mydatabase;
import tn.cinema.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DemandeService implements IServices<Demande>{
    Connection cnx;
    public DemandeService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Demande demande) throws SQLException {
        String sql = "INSERT INTO demande(user_id, nbr_Jours, description, type, lien_supp) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, 3);
            ps.setInt(2, demande.getNombreJours());
            ps.setString(3, demande.getDescription());
            ps.setString(4, demande.getType());
            ps.setString(5, demande.getLienSupplementaire());
            ps.executeUpdate();
            System.out.println("Demande ajoutée");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void ajouterDemande(Demande demande) throws SQLException {
        String sql = "INSERT INTO demande(user_id, nbr_Jours, description, type, lien_supp) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, demande.getUserId());
            ps.setInt(2, demande.getNombreJours());
            ps.setString(3, demande.getDescription());
            ps.setString(4, demande.getType());
            ps.setString(5, demande.getLienSupplementaire());
            ps.executeUpdate();
            System.out.println("Demande ajoutée");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void ajoutDemande(Demande demande) throws SQLException {
        String sql = "INSERT INTO demande(user_id, nbr_Jours, description, type, lien_supp) VALUES(?,?,?,?,?)";
        try {
            // Get logged-in user's ID from SessionManager
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                throw new IllegalStateException("Aucun utilisateur connecté.");
            }
            int userId = loggedInUser.getId();

            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, userId); // Dynamic user_id
            ps.setInt(2, demande.getNombreJours());
            ps.setString(3, demande.getDescription());
            ps.setString(4, demande.getType());
            ps.setString(5, demande.getLienSupplementaire());
            ps.executeUpdate();
            System.out.println("Demande ajoutée pour user_id: " + userId);
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de la demande: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM demande WHERE id = ?"; // Correction de la requête SQL
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id); // Assignation de l'ID à supprimer

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Demande supprimée avec succès !");
            } else {
                System.out.println("Aucune demande trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        }


    }


    @Override
    public void modifier(Demande demande) throws SQLException {

        String sql = "UPDATE demande SET " +
                "nbr_Jours = " + demande.getNombreJours() + ", " +
                "description = '" + demande.getDescription() + "', " +
                "type = '" + demande.getType() + "', " +
                "lien_supp = '" + demande.getLienSupplementaire() + "' " +
                "WHERE id = " + demande.getId();

        try (Statement stmt = cnx.createStatement()) {
            int rowsUpdated = stmt.executeUpdate(sql);
            if (rowsUpdated > 0) {
                System.out.println("Demande avec ID " + demande.getId() + " modifiée avec succès.");
            } else {
                System.out.println("Aucune demande trouvée avec ID " + demande.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void modifier(int id, Demande demande) throws SQLException {
        String sql = "UPDATE demande SET " +
                "nbr_Jours = " + demande.getNombreJours() + ", " +
                "description = '" + demande.getDescription() + "', " +
                "type = '" + demande.getType() + "', " +
                "lien_supp = '" + demande.getLienSupplementaire() + "' " +
                "WHERE id = " + id;

        try {
            Statement stmt = cnx.createStatement();
            int rowsUpdated = stmt.executeUpdate(sql);
            if (rowsUpdated > 0) {
                System.out.println("Demande cette id " + id + " est modifier aveeeec succées");
            } else {
                System.out.println("id non trouvée " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void modifierAdmin(int id, Demande demande) throws SQLException {
        String sql = "UPDATE demande SET nbr_Jours = ?, description = ?, type = ?, lien_supp = ?, statut = ? WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = cnx.prepareStatement(sql);
            ps.setInt(1, demande.getNombreJours());
            ps.setString(2, demande.getDescription());
            ps.setString(3, demande.getType());
            ps.setString(4, demande.getLienSupplementaire());
            ps.setString(5, demande.getStatut());
            ps.setInt(6, id);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Demande avec id " + id + " modifiée avec succès");
            } else {
                System.out.println("ID non trouvé : " + id);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la modification de la demande : " + e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }




    @Override
    public List<Demande> recuperer() throws SQLException{
        String sql="SELECT * FROM demande";
        Statement ste= cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Demande> demande = new ArrayList<>();
        while ( rs.next()){
            int id = rs.getInt("id");
            int userId=rs.getInt("user_id");
            int adminId=rs.getInt("admin_id");
            int nombreJours=rs.getInt("nbr_jours");
            String description=rs.getString("description");
            String type=rs.getString("type");
            String lienSupplementaire=rs.getString("lien_supp");
            String statut=rs.getString("statut");
          /*  if (statut == null) {
                statut = "Inconnu";
            }*/
            Date dateSoumission=rs.getDate("date_soumission");

            Demande d = new Demande(id,userId,adminId,nombreJours,description,type,lienSupplementaire,statut,dateSoumission);
            demande.add(d);
        }

        return demande;
    }

    public List<Demande> recupererDemandesParClient(int clientId) throws SQLException {
        String sql = "SELECT * FROM demande WHERE user_id = ?";
        PreparedStatement pstmt = cnx.prepareStatement(sql);
        pstmt.setInt(1, clientId);
        ResultSet rs = pstmt.executeQuery();
        List<Demande> demandes = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            int userId = rs.getInt("user_id");
            int adminId = rs.getInt("admin_id");
            int nombreJours = rs.getInt("nbr_jours");
            String description = rs.getString("description");
            String type = rs.getString("type");
            String lienSupplementaire = rs.getString("lien_supp");
            String statut = rs.getString("statut");
            if (statut == null) {
                statut = "Inconnu";
            }
            Date dateSoumission = rs.getDate("date_soumission");

            Demande d = new Demande(id, userId, adminId, nombreJours, description, type, lienSupplementaire, statut,dateSoumission);
            demandes.add(d);
        }

        return demandes;
    }

    public List<Demande> recupereDemandesParClient() throws SQLException {
        String sql = "SELECT * FROM demande WHERE user_id = ?";
        try {
            // Get logged-in user's ID from SessionManager
            User loggedInUser = SessionManager.getInstance().getLoggedInUser();
            if (loggedInUser == null) {
                throw new IllegalStateException("Aucun utilisateur connecté.");
            }
            int userId = loggedInUser.getId();

            PreparedStatement pstmt = cnx.prepareStatement(sql);
            pstmt.setInt(1, userId); // Dynamic user_id
            ResultSet rs = pstmt.executeQuery();
            List<Demande> demandes = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                int userIdFromDb = rs.getInt("user_id");
                int adminId = rs.getInt("admin_id");
                int nombreJours = rs.getInt("nbr_jours");
                String description = rs.getString("description");
                String type = rs.getString("type");
                String lienSupplementaire = rs.getString("lien_supp");
                String statut = rs.getString("statut");
                if (statut == null) {
                    statut = "Inconnu";
                }
                Date dateSoumission = rs.getDate("date_soumission");

                Demande d = new Demande(id, userIdFromDb, adminId, nombreJours, description, type, lienSupplementaire, statut, dateSoumission);
                demandes.add(d);
            }

            return demandes;
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des demandes: " + e.getMessage());
        }
    }


}
