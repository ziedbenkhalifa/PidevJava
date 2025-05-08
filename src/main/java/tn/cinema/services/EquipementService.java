package tn.cinema.services;

import tn.cinema.entities.Equipement;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipementService implements IServices<Equipement> {

    private Connection getConnection() {
        // Toujours récupérer la connexion fraîchement depuis Mydatabase
        return Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Equipement equipement) throws SQLException {
        String sql = "INSERT INTO equipement(nom, type, etat, salle_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, equipement.getNom());
            ps.setString(2, equipement.getType());
            ps.setString(3, equipement.getEtat());
            ps.setInt(4, equipement.getSalle_id());
            ps.executeUpdate();
            System.out.println("Équipement ajouté");
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM equipement WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Équipement supprimé avec succès !");
            } else {
                System.out.println("Aucun équipement trouvé avec cet ID !");
            }
        }
    }

    @Override
    public void modifier(Equipement equipement) throws SQLException {
        String sql = "UPDATE equipement SET nom = ?, type = ?, salle_id = ?, etat = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, equipement.getNom());
            ps.setString(2, equipement.getType());
            ps.setInt(3, equipement.getSalle_id());
            ps.setString(4, equipement.getEtat());
            ps.setInt(5, equipement.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Équipement modifié avec succès !");
            } else {
                System.out.println("Aucun équipement trouvé avec cet ID !");
            }
        }
    }

    @Override
    public List<Equipement> recuperer() throws SQLException {
        String sql = "SELECT * FROM equipement";
        List<Equipement> equipements = new ArrayList<>();

        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Equipement e = new Equipement();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setType(rs.getString("type"));
                e.setEtat(rs.getString("etat"));
                e.setSalle_id(rs.getInt("salle_id"));
                equipements.add(e);
            }
        }
        return equipements;
    }

    public List<Equipement> rechercherParNom(String nomRecherche) throws SQLException {
        List<Equipement> resultats = new ArrayList<>();
        String req = "SELECT * FROM equipement WHERE nom LIKE ?";

        try (PreparedStatement ps = getConnection().prepareStatement(req)) {
            ps.setString(1, "%" + nomRecherche + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Equipement e = new Equipement();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setType(rs.getString("type"));
                e.setEtat(rs.getString("etat"));
                e.setSalle_id(rs.getInt("salle_id"));
                resultats.add(e);
            }
        }

        return resultats;
    }
}