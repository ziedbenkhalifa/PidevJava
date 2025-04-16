package tn.cinema.services;

import tn.cinema.entities.Commande;
import tn.cinema.tools.Mydatabase;
import tn.cinema.entities.Produit;
import java.sql.*;
import java.time.LocalDateTime;
import tn.cinema.services.ProduitService;
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
            stmt.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
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
            stmt.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
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
                        rs.getTimestamp("datecommande").toLocalDateTime(),
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
    public Commande recupererParId(int id) {
        String query = "SELECT * FROM commande WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Commande(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("datecommande").toLocalDateTime(),
                            rs.getDouble("montantpaye"),
                            rs.getString("etat")
                    );
                } else {
                    System.out.println("⚠️ Aucune commande trouvée avec l'ID : " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return null;
    }
    public void ajouterProduitACommande(int commandeId, int produitId) {
        String sql = "INSERT INTO commande_produit (commande_id, produit_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
            System.out.println("✅ Produit ajouté à la commande !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du produit à la commande : " + e.getMessage());
        }
    }
    // Exemple de méthode dans CommandeService pour récupérer la dernière commande
    public int recupererDerniereCommandeId() {
        String sql = "SELECT id FROM commande ORDER BY id DESC LIMIT 1"; // Récupérer la commande avec l'ID le plus élevé
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la dernière commande : " + e.getMessage());
        }
        return -1;  // Retourner -1 si aucune commande n'est trouvée
    }
    public int getIdCommandeEnCours() {
        String sql = "SELECT id FROM commande WHERE status = 'en cours' LIMIT 1";  // Filtrer par statut
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la commande en cours : " + e.getMessage());
        }
        return -1;  // Retourner -1 si aucune commande en cours n'est trouvée
    }

}
