package tn.cinema.services;

import tn.cinema.entities.Equipement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tn.cinema.tools.Mydatabase;


public class EquipementService implements IServices<Equipement> {

        private Connection cnx;

        public EquipementService() {
            cnx = Mydatabase.getInstance().getCnx();
        }


        public void ajouter(Equipement equipement) throws SQLException {
            String sql = "INSERT INTO equipement(nom, type, etat, id_salle) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, equipement.getNom());
            ps.setString(2, equipement.getType()); // Correction de l'ordre
            ps.setString(3, equipement.getEtat());
            ps.setInt(4, equipement.getId_salle()); // Correction du type
            ps.executeUpdate();
            System.out.println("Équipement ajouté");
        }


    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM equipement WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
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
        String sql = "UPDATE equipement SET nom = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, equipement.getNom());
            ps.setInt(2, equipement.getId());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Équipement modifié avec succès !");
            } else {
                System.out.println("Aucun équipement trouvé avec cet ID !");
            }
        }
    }





    public List<Equipement> recuperer() throws SQLException {
            String sql = "SELECT * FROM equipement";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);
            List<Equipement> equipements = new ArrayList<>();
            while (rs.next()) {
                Equipement e = new Equipement();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setType(rs.getString("type"));
                e.setEtat(rs.getString("etat"));
                e.setId_salle(rs.getInt("id_salle")); // Ajout de l'ID de la salle
                equipements.add(e);
            }
            return equipements;
        }
    }

