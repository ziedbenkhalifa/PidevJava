package tn.cinema.services;

<<<<<<< HEAD
import tn.cinema.entities.Publicite;
import tn.cinema.tools.Mydatabase;

import java.sql.Connection;
=======
import tn.cinema.entities.Demande;
import tn.cinema.entities.Publicite;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
//import java.util.Date;
import java.sql.Date;
>>>>>>> Publicites
import java.util.List;

public class PubliciteService implements IServices<Publicite> {
    Connection cnx;
    public PubliciteService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Publicite publicite) {

    }
<<<<<<< HEAD
=======
    public void ajouterpub(Publicite publicite) throws SQLException {
        String sql = "INSERT INTO publicite(demande_id, date_debut, date_fin, support, montant) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, publicite.getDemandeId()); // ID de la demande
            ps.setDate(2, publicite.getDateDebut());
            ps.setDate(3, publicite.getDateFin());
            ps.setString(4, publicite.getSupport());
            ps.setFloat(5, publicite.getMontant());
            ps.executeUpdate();
            System.out.println("Publicité ajoutée avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

>>>>>>> Publicites

    @Override
    public void supprimer(int id) {

    }
<<<<<<< HEAD

    @Override
    public void modifier(int id) {

    }

    @Override
    public List<Publicite> recuperer() {
        return List.of();
    }
}
=======
    public void supprimerpub(int id){
        String sql = "DELETE FROM publicite WHERE id = ?"; // Correction de la requête SQL
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id); // Assignation de l'ID à supprimer

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("publicite supprimée avec succès !");
            } else {
                System.out.println("Aucune publicite trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Publicite publicite) {

    }
    public void modifierpub(int publiciteId, Publicite updatedPublicite) throws SQLException {
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

    public List<Publicite> recupererpub() throws SQLException{
    String sql="SELECT * FROM publicite";
    Statement ste= cnx.createStatement();
    ResultSet rs = ste.executeQuery(sql);
    List<Publicite> publicite = new ArrayList<>();
        while ( rs.next()){
        int id = rs.getInt(1);
        int demandeId=rs.getInt(2);
        Date dateDebut=rs.getDate(3);
        Date dateFin=rs.getDate(4);
        String support=rs.getString(5);
        Float montant=rs.getFloat(6);

        Publicite p = new Publicite(id,demandeId,dateDebut,dateFin,support,montant);
        publicite.add(p);
    }
        return publicite;
    }

    public List<Publicite> recupererPublicitesParClient(int clientId) throws SQLException {
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

}

>>>>>>> Publicites
