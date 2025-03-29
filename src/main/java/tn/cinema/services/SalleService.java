package tn.cinema.services;

import tn.cinema.entities.Salle;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleService implements IServices<Salle> {

    private Connection cnx;

    public SalleService() {
        cnx = Mydatabase.getInstance().getCnx();
    }

    public void ajouter(Salle salle) throws SQLException {
        String sql = "INSERT INTO salle(id_salle, nombre_de_place, nom_salle, statut, disponibilite, type_salle, emplacement) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, salle.getId_salle());
        ps.setInt(2, salle.getNombre_de_place());
        ps.setString(3, salle.getNom_salle());
        ps.setString(4, salle.getStatut());
        ps.setString(5, salle.getDisponibilite());
        ps.setString(6, salle.getType_salle());
        ps.setString(7, salle.getEmplacement());
        ps.executeUpdate();
        System.out.println("Salle ajoutée avec succès !");
    }



    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM salle WHERE id_salle = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Salle supprimée !");
            } else {
                System.out.println("Aucune salle trouvée avec cet ID !");
            }
        }
    }

    public void modifier(Salle salle) throws SQLException {
        String sql = "UPDATE salle SET nom_salle = ? WHERE id_salle = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, salle.getNom_salle()); // Récupérer le nom depuis l'objet Salle
            ps.setInt(2, salle.getId_salle()); // Récupérer l'ID de la salle
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Salle modifiée !");
            } else {
                System.out.println("Aucune salle trouvée avec cet ID !");
            }
        }
    }


    public List<Salle> recuperer() throws SQLException {
        String sql = "SELECT * FROM salle";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Salle> salles = new ArrayList<>();
        while (rs.next()) {
            Salle s = new Salle();
            s.setId_salle(rs.getInt("id_salle"));
            s.setNom_salle(rs.getString("nom_salle"));
            s.setType_salle(rs.getString("type_salle"));
            s.setStatut(rs.getString("statut"));
            s.setNombre_de_place(rs.getInt("nombre_de_place"));
            s.setDisponibilite(rs.getString("disponibilite"));
            s.setEmplacement(rs.getString("emplacement"));
            salles.add(s);
        }
        return salles;
    }
}
