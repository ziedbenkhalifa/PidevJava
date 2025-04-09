package tn.cinema.services;

import tn.cinema.entities.Seance;
import tn.cinema.entities.Cour;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceService implements IServices<Seance> {
    private Connection cnx;
    private String sql;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;

    public SeanceService() {
        cnx = Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Seance seance) throws SQLException {
        sql = "INSERT INTO seance(cour_id, date_seance, duree, objectifs) VALUES (?, ?, ?, ?)";
        ps = cnx.prepareStatement(sql);


        ps.setInt(1, seance.getCour().getId());
        ps.setDate(2, java.sql.Date.valueOf(seance.getDateSeance()));
        ps.setTime(3, Time.valueOf(seance.getDuree()));
        ps.setString(4, seance.getObjectifs());

        ps.executeUpdate();
        System.out.println("Séance ajoutée");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        sql = "DELETE FROM seance WHERE id = ?";
        ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Séance supprimée");
    }

    @Override
    public void modifier(Seance seance) throws SQLException {
        sql = "UPDATE seance SET cour_id = ?, date_seance = ?, duree = ?, objectifs = ? WHERE id = ?";
        ps = cnx.prepareStatement(sql);

        ps.setInt(1, seance.getCour().getId());
        ps.setDate(2, java.sql.Date.valueOf(seance.getDateSeance()));
        ps.setTime(3, Time.valueOf(seance.getDuree()));
        ps.setString(4, seance.getObjectifs());
        ps.setInt(5, seance.getId());

        ps.executeUpdate();
        System.out.println("Séance modifiée");
    }

    @Override
    public List<Seance> recuperer() throws SQLException {
        sql = "SELECT * FROM seance";
        st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        List<Seance> seances = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            LocalDate dateSeance = rs.getDate("date_seance").toLocalDate();
            LocalTime duree = rs.getTime("duree").toLocalTime();
            String objectifs = rs.getString("objectifs");
            int idCour = rs.getInt("cour_id");


            Cour cour = new Cour();
            cour.setId(idCour);


            Seance s = new Seance(id, dateSeance, duree, objectifs, cour);
            seances.add(s);
        }

        return seances;
    }
}
