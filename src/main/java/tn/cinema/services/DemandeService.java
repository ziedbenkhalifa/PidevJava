package tn.cinema.services;

import tn.cinema.entities.Demande;
import tn.cinema.tools.Mydatabase;

<<<<<<< HEAD
import java.sql.Connection;
import java.util.List;
=======
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

>>>>>>> Publicites

public class DemandeService implements IServices<Demande>{
    Connection cnx;
    public DemandeService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
<<<<<<< HEAD
    public void ajouter(Demande demande) {
=======
    public void ajouter(Demande demande) throws SQLException {
        String sql="INSERT INTO demande(int id,user_id,nbr_Jours,description,type,lien_supp) "+"VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps= cnx.prepareStatement(sql);
            ps.setInt(1,3);
            ps.setInt(2,demande.getNombreJours());
            ps.setString(3,demande.getDescription());
            ps.setString(4, demande.getType());
            ps.setString(5, demande.getLienSupplementaire());
            ps.executeUpdate();
            System.out.println("Demande ajoutée");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
>>>>>>> Publicites

    }

    @Override
<<<<<<< HEAD
    public void supprimer(int id) {

    }

    @Override
    public void modifier(int id) {

    }

    @Override
    public List<Demande> recuperer() {
        return List.of();
    }
=======
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
        String sql = "UPDATE demande SET " +
                "nbr_Jours = " + demande.getNombreJours() + ", " +
                "description = '" + demande.getDescription() + "', " +
                "type = '" + demande.getType() + "', " +
                "lien_supp = '" + demande.getLienSupplementaire() + "', " +  // Ajout de la virgule
                "statut = '" + demande.getStatut() + "' " +  // Ajout de la virgule
                "WHERE id = " + id;

        try {
            Statement stmt = cnx.createStatement();
            int rowsUpdated = stmt.executeUpdate(sql);
            if (rowsUpdated > 0) {
                System.out.println("Demande avec id " + id + " modifiée avec succès");
            } else {
                System.out.println("ID non trouvé : " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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



>>>>>>> Publicites
}
