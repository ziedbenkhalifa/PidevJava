package tn.cinema.services;

import tn.cinema.entities.User;
import tn.cinema.tools.Mydatabase;

<<<<<<< HEAD
import java.sql.Connection;
import java.util.List;

public class UserService implements IServices<User>{
    Connection cnx;
    public UserService(){
        cnx= Mydatabase.getInstance().getCnx();
=======
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IServices<User> {
    Connection cnx;

    public UserService() {
        cnx = Mydatabase.getInstance().getCnx();
        if (cnx == null) {
            System.out.println("Database connection failed!");
        }
>>>>>>> Publicites
    }

    @Override
    public void ajouter(User u) {
<<<<<<< HEAD

=======
        String query = "INSERT INTO user (nom, date_de_naissance, email, role, mot_de_passe, photo, face_token) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, u.getNom());
            pst.setDate(2, new java.sql.Date(u.getDateDeNaissance().getTime()));
            pst.setString(3, u.getEmail());
            pst.setString(4, u.getRole());
            pst.setString(5, u.getMotDePasse());
            pst.setString(6, u.getPhoto());
            pst.setString(7, u.getFaceToken());
            pst.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
>>>>>>> Publicites
    }

    @Override
    public void supprimer(int id) {
<<<<<<< HEAD

    }

    @Override
    public void modifier(int id) {

    }

    @Override
    public List<User> recuperer() {
        return List.of();
    }
}
=======
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Utilisateur supprimé avec succès.");
            } else {
                System.out.println("L'utilisateur avec l'ID " + id + " n'existe pas.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifier(User user) {

    }

    public void modifier(int id, String nom, Date dateDeNaissance, String email, String role, String motDePasse, String photo, String faceToken) {
        String query = "UPDATE user SET nom = ?, date_de_naissance = ?, email = ?, role = ?, mot_de_passe = ?, photo = ?, face_token = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, nom);
            pst.setDate(2, new java.sql.Date(dateDeNaissance.getTime()));
            pst.setString(3, email);
            pst.setString(4, role);
            pst.setString(5, motDePasse);
            pst.setString(6, photo);
            pst.setString(7, faceToken);
            pst.setInt(8, id);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }


    @Override
    public List<User> recuperer() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDate("date_de_naissance"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("mot_de_passe"),
                        rs.getString("photo"),
                        rs.getString("face_token")
                );
                users.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }
        return users;
    }
}
>>>>>>> Publicites
