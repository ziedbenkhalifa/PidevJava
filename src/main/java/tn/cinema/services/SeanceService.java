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

        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Séance modifiée avec succès. ID: " + seance.getId());
        } else {
            System.out.println("Échec de la modification. Aucune séance trouvée avec l'ID: " + seance.getId());
        }
    }

    @Override
    public List<Seance> recuperer() throws SQLException {
        sql = "SELECT * FROM seance";
        st = cnx.createStatement();
        rs = st.executeQuery(sql);

        List<Seance> seances = new ArrayList<>();
        CourService courService = new CourService();

        while (rs.next()) {
            int id = rs.getInt("id");
            LocalDate dateSeance = rs.getDate("date_seance").toLocalDate();
            LocalTime duree = rs.getTime("duree").toLocalTime();
            String objectifs = rs.getString("objectifs");
            int idCour = rs.getInt("cour_id");

            // Charger l'objet Cour complet à partir de CourService
            Cour cour = courService.recuperer().stream()
                    .filter(c -> c.getId() == idCour)
                    .findFirst()
                    .orElse(null);

            if (cour == null) {
                System.out.println("Aucun cours trouvé pour cour_id: " + idCour);
                cour = new Cour();
                cour.setId(idCour);
            }

            Seance s = new Seance(id, dateSeance, duree, objectifs, cour);
            seances.add(s);
        }

        return seances;
    }
}