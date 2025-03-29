package tn.cinema.services;

import tn.cinema.entities.Commande;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements IServices<Commande> {
    private Connection connection;

    public CommandeService() {
        connection = Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Commande commande) {
        String query = "INSERT INTO commande (user_id, datecommande, montantpaye, etat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, commande.getUserId());
            stmt.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            stmt.setDouble(3, commande.getMontantPaye());
            stmt.setString(4, commande.getEtat());

            stmt.executeUpdate();
            System.out.println("✅ Commande ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM commande WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Commande supprimée !");
            } else {
                System.out.println("⚠️ Aucun produit trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Commande commande) {
        String query = "UPDATE commande SET user_id=?, datecommande=?, montantpaye=?, etat=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, commande.getUserId());
            stmt.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            stmt.setDouble(3, commande.getMontantPaye());
            stmt.setString(4, commande.getEtat());
            stmt.setInt(5, commande.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Commande mise à jour !");
            } else {
                System.out.println("⚠️ Aucun produit trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public List<Commande> recuperer() {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDate("datecommande"),
                        rs.getDouble("montantpaye"),
                        rs.getString("etat")
                );
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return commandes;
    }
}
