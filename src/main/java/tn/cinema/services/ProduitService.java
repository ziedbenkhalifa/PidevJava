package tn.cinema.services;

import tn.cinema.entities.Produit;
<<<<<<< HEAD

import java.util.List;

public class ProduitService implements IServices<Produit>{
    @Override
    public void ajouter(Produit produit) {

=======
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IServices<Produit> {
    private Connection connection;

    public ProduitService() {
        connection = Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Produit produit) {
        String query = "INSERT INTO produit (nom, prix, categorie, description, image, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setString(3, produit.getCategorie());
            stmt.setString(4, produit.getDescription());
            stmt.setString(5, produit.getImage());
            stmt.setDate(6, new java.sql.Date(produit.getDate().getTime()));

            stmt.executeUpdate();
            System.out.println("✅ Produit ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
>>>>>>> Publicites
    }

    @Override
    public void supprimer(int id) {
<<<<<<< HEAD

    }

    @Override
    public void modifier(int id) {

=======
        String query = "DELETE FROM produit WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Produit supprimé !");
            } else {
                System.out.println("⚠️ Aucun produit trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Produit produit) {
        String query = "UPDATE produit SET nom=?, prix=?, categorie=?, description=?, image=?, date=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setString(3, produit.getCategorie());
            stmt.setString(4, produit.getDescription());
            stmt.setString(5, produit.getImage());
            stmt.setDate(6, new java.sql.Date(produit.getDate().getTime()));
            stmt.setInt(7, produit.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Produit mis à jour !");
            } else {
                System.out.println("⚠️ Aucun produit trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
>>>>>>> Publicites
    }

    @Override
    public List<Produit> recuperer() {
<<<<<<< HEAD
        return List.of();
    }
}
=======
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getString("categorie"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDate("date")  // Changer le nom de la colonne "date" à "dateAjout"
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return produits;
    }

}
>>>>>>> Publicites
