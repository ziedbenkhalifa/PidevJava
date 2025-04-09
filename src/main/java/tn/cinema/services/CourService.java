package tn.cinema.services;

import tn.cinema.entities.Cour;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourService implements IServices<Cour>{
    private Connection cnx;
    private String sql;
    private PreparedStatement ps;
    private Statement st;

    public CourService() {
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Cour cour) throws SQLException {
        sql = "INSERT INTO cour(type_cour, cout, date_debut, date_fin) " +
                "VALUES (?, ?, ?, ?)";

        ps = cnx.prepareStatement(sql);

        ps.setString(1, cour.getTypeCour());
        ps.setDouble(2, cour.getCout());
        ps.setTimestamp(3, Timestamp.valueOf(cour.getDateDebut()));
        ps.setTimestamp(4, Timestamp.valueOf(cour.getDateFin()));
        ps.executeUpdate();
        System.out.println("Cour ajoutée");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        sql = "DELETE FROM cour WHERE id = ?";
        ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Cour supprimé");
    }

    @Override
    public void modifier(Cour cour) throws SQLException {
        String sql = "UPDATE cour SET type_cour = '" + cour.getTypeCour() +
                "', cout = " + cour.getCout() +
                ", date_debut = '" + Timestamp.valueOf(cour.getDateDebut()) +
                "', date_fin = '" + Timestamp.valueOf(cour.getDateFin()) +
                "' WHERE id = " + cour.getId();

        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
        System.out.println("Cours modifié");
    }


    @Override
    public List<Cour> recuperer() throws SQLException {
        sql="SELECT * FROM cour";

        st=cnx.createStatement();
        ResultSet rs=st.executeQuery(sql);

        List<Cour> cours = new ArrayList<>();

        while (rs.next()){

            int id= rs.getInt("id");
            String TypeCour= rs.getString("type_cour");
            Double Cout= rs.getDouble("cout");
            LocalDateTime DateDebut= rs.getTimestamp("date_debut").toLocalDateTime();
            LocalDateTime DateFin= rs.getTimestamp("date_fin").toLocalDateTime();

            Cour c = new Cour(id, TypeCour, Cout, DateDebut, DateFin);
            cours.add(c);
        }

        return cours;
    }




    public void ajouterParticipation(int userId, int courId) throws SQLException {
        sql = "INSERT INTO participation (user_id, cour_id) VALUES (?, ?)";
        ps = cnx.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, courId);
        ps.executeUpdate();

    }


    public void supprimerParticipation(int userId, int courId) throws SQLException {
        sql = "DELETE FROM participation WHERE user_id = ? AND cour_id = ?";
        ps = cnx.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, courId);
        ps.executeUpdate();
    }


    public List<Integer> recupererParticipations(int userId) throws SQLException {
        List<Integer> coursIds = new ArrayList<>();
        sql = "SELECT cour_id FROM participation WHERE user_id = ?";
        ps = cnx.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            coursIds.add(rs.getInt("cour_id"));
        }

        return coursIds;
    }
}