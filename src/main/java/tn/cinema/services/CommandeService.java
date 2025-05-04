package tn.cinema.services;

import tn.cinema.entities.Commande;
import tn.cinema.tools.Mydatabase;
import tn.cinema.utils.SessionManager;
import tn.cinema.entities.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.stream.*;
import java.text.SimpleDateFormat;
import java.io.FileNotFoundException;
import java.io.File;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class CommandeService implements IServices<Commande> {
    private Connection connection;

    public CommandeService() {
        connection = Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Commande commande) {
        // Récupérer l'utilisateur connecté depuis la session
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            System.err.println("❌ Aucun utilisateur connecté. Impossible d'ajouter une commande.");
            return;
        }

        int userId = loggedInUser.getId(); // On récupère l'ID depuis la session

        String query = "INSERT INTO commande (user_id, datecommande, montantpaye, etat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
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
            // Remplir la requête préparée avec les données de la commande
            stmt.setInt(1, commande.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
            stmt.setDouble(3, commande.getMontantPaye());
            stmt.setString(4, commande.getEtat());
            stmt.setInt(5, commande.getId());

            // Exécuter la mise à jour
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Commande mise à jour avec succès !");

                // Si l'état de la commande est modifié à "livrée", envoyer un email
                if ("livrée".equalsIgnoreCase(commande.getEtat())) {
                    // Récupérer l'utilisateur connecté
                    User loggedInUser = SessionManager.getInstance().getLoggedInUser();

                    // Vérifier si l'utilisateur est connecté avant d'envoyer l'email
                    if (loggedInUser != null) {
                        String recipientEmail = loggedInUser.getEmail();
                        String subject = "Confirmation de commande livrée";
                        String messageBody = "Bonjour, votre commande #" + commande.getId() + " a été livrée. Merci de votre achat !";

                        // Envoyer l'email de confirmation
                        boolean emailEnvoye = envoyerEmailConfirmation(recipientEmail, subject, messageBody);

                        // Vérifier si l'email a été envoyé avec succès
                        if (emailEnvoye) {
                            System.out.println("✅ Email de confirmation envoyé !");
                        } else {
                            System.out.println("⚠️ L'envoi de l'email a échoué.");
                        }
                    } else {
                        System.out.println("⚠️ L'utilisateur n'est pas connecté, email non envoyé.");
                    }
                }
            } else {
                System.out.println("⚠️ Aucun produit trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la commande : " + e.getMessage());
        }
    }

    // Méthode pour envoyer l'email de confirmation
    public static boolean envoyerEmailConfirmation(String recipientEmail, String subject, String messageBody) {
        try {
            // Configuration de l'email (utilisez un serveur SMTP comme Gmail)
            String host = "smtp.gmail.com";
            String fromEmail = "yassinewertani09@gmail.com"; // Remplacez par votre email
            String password = "ynpxxfamthgesaki"; // Utilisez un mot de passe d'application

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            // Création de la session email
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            // Créer le message email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(messageBody);

            // Envoyer l'email
            Transport.send(message);
            return true;  // Email envoyé avec succès
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
            return false;  // Envoi échoué
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
    public List<Commande> recupererCommandesUtilisateurConnecte() {
        List<Commande> commandes = new ArrayList<>();
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();

        if (loggedInUser == null) {
            System.err.println("❌ Aucun utilisateur connecté. Impossible de récupérer les commandes.");
            return commandes;
        }

        String query = "SELECT * FROM commande WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, loggedInUser.getId());
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des commandes de l'utilisateur connecté : " + e.getMessage());
        }

        return commandes;
    }

    public void exporterCommandeVersPDF(Commande commande, String filePath) throws DocumentException, FileNotFoundException {
        // Créer un document PDF
        Document document = new Document();
        PdfWriter.getInstance(document, new java.io.FileOutputStream(new File(filePath)));

        // Ouvrir le document pour ajouter des éléments
        document.open();

        // Ajouter un titre
        document.add(new Phrase("Commande Détails\n"));

        // Créer un tableau PDF avec 5 colonnes : ID, User ID, Etat, Montant Payé, Date
        PdfPTable table = new PdfPTable(5);
        table.addCell("ID");
        table.addCell("User ID");
        table.addCell("Etat");
        table.addCell("Montant Payé");
        table.addCell("Date");

        // Ajouter les données de la commande dans le tableau
        table.addCell(String.valueOf(commande.getId()));
        table.addCell(String.valueOf(commande.getUserId()));
        table.addCell(commande.getEtat());
        table.addCell(String.valueOf(commande.getMontantPaye()));
        table.addCell(commande.getDateCommande().toString());

        // Ajouter le tableau au document
        document.add(table);

        // Fermer le document
        document.close();
    }
}