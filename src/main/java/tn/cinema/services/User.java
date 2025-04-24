package tn.cinema.services;

import tn.cinema.tools.Mydatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private Connection cnx;

    public User() {
        cnx = Mydatabase.getInstance().getCnx();
        if (cnx == null) {
            System.out.println("Erreur : La connexion à la base de données est null.");
        } else {
            System.out.println("Connexion à la base de données établie avec succès pour UserService.");
        }
    }

    public boolean userExists(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}