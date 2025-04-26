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
        String sql = "INSERT INTO salle(id_salle, nom_salle, statut, disponibilite, type_salle, emplacement, nombre_de_place) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);

        // Assurez-vous que l'ordre des paramètres correspond à l'ordre des colonnes dans la requête SQL
        ps.setInt(1, salle.getId_salle()); // id_salle
        ps.setString(2, salle.getNom_salle()); // nom_salle
        ps.setString(3, salle.getStatut()); // statut
        ps.setString(4, salle.getDisponibilite()); // disponibilite
        ps.setString(5, salle.getType_salle()); // type_salle
        ps.setString(6, salle.getEmplacement()); // emplacement
        ps.setInt(7, salle.getNombre_de_place()); // nombre_de_place

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
    public List<Salle> rechercherParNom(String nomRecherche) throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle WHERE LOWER(nom_salle) LIKE ?";
        PreparedStatement ps = cnx.prepareStatement(sql); // cnx est déjà existante
        ps.setString(1, "%" + nomRecherche.toLowerCase() + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Salle s = new Salle(
                    rs.getInt("id_salle"),
                    rs.getInt("nombre_de_place"),
                    rs.getString("nom_salle"),
                    rs.getString("type_salle"),
                    rs.getString("disponibilite"),
                    rs.getString("statut"),
                    rs.getString("emplacement")
            );
            salles.add(s);
        }

        return salles;
    }




    public void modifier(Salle salle) throws SQLException {
        String sql = "UPDATE salle SET nom_salle = ?, disponibilite = ?, emplacement = ?, statut = ?, type_salle = ?, nombre_de_place = ? WHERE id_salle = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, salle.getNom_salle());
            ps.setString(2, salle.getDisponibilite());
            ps.setString(3, salle.getEmplacement());
            ps.setString(4, salle.getStatut());
            ps.setString(5, salle.getType_salle());
            ps.setInt(6, salle.getNombre_de_place());
            ps.setInt(7, salle.getId_salle());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Salle modifiée !");
            } else {
                System.out.println("Aucune salle trouvée avec cet ID !");
            }
        }
    }
    public List<Salle> getSallesParEtage(String etage) throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle WHERE emplacement = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, etage);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Salle s = new Salle(
                    rs.getInt("id_salle"),
                    rs.getInt("nombre_de_place"),
                    rs.getString("nom_salle"),
                    rs.getString("type_salle"),
                    rs.getString("disponibilite"),
                    rs.getString("statut"),
                    rs.getString("emplacement")
            );
            salles.add(s);
        }

        return salles;
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
            s.setDisponibilite(rs.getString("disponibilite"));
            s.setType_salle(rs.getString("type_salle"));
            s.setStatut(rs.getString("statut"));


            s.setEmplacement(rs.getString("emplacement"));
            s.setNombre_de_place(rs.getInt("nombre_de_place"));
            salles.add(s);
        }
        return salles;
    }
}
