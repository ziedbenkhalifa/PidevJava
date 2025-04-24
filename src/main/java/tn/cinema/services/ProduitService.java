package tn.cinema.services;

import tn.cinema.entities.Produit;
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
            stmt.setTimestamp(6, Timestamp.valueOf(produit.getDate()));

            stmt.executeUpdate();
            System.out.println("✅ Produit ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
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
            stmt.setTimestamp(6, Timestamp.valueOf(produit.getDate()));
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
    }

    @Override
    public List<Produit> recuperer() {
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
                        rs.getTimestamp("date").toLocalDateTime()
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return produits;
    }
    public Produit recupererParId(int id) {
        String query = "SELECT * FROM produit WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produit(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getDouble("prix"),
                            rs.getString("categorie"),
                            rs.getString("description"),
                            rs.getString("image"),
                            rs.getTimestamp("date").toLocalDateTime()
                    );
                } else {
                    System.out.println("⚠️ Aucun produit trouvé avec l'ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du produit : " + e.getMessage());
        }
        return null;
    }
    /*public List<Produit> rechercherParNom(String keyword) {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit WHERE nom LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getDouble("prix"),
                            rs.getString("categorie"),
                            rs.getString("description"),
                            rs.getString("image"),
                            rs.getTimestamp("date").toLocalDateTime()
                    );
                    produits.add(produit);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche : " + e.getMessage());
        }
        return produits;
    }*/
}