package tn.cinema.services;

import tn.cinema.entities.Seance;
import tn.cinema.entities.Cour;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeanceService implements IServices<Seance> {
    private Connection cnx;
    private String sql;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;

    public SeanceService() {
        cnx = Mydatabase.getInstance().getCnx();
        System.out.println("Connexion à la base pour SeanceService : " + (cnx != null ? "OK" : "Échec"));
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
        System.out.println("Séance ajoutée : " + seance);
    }

    @Override
    public void supprimer(int id) throws SQLException {
        sql = "DELETE FROM seance WHERE id = ?";
        ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Séance supprimée, ID: " + id);
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
        List<Cour> allCours = courService.recuperer(); // Récupérer tous les cours une seule fois

        System.out.println("Début de la récupération des séances...");
        while (rs.next()) {
            int id = rs.getInt("id");
            LocalDate dateSeance = rs.getDate("date_seance") != null ? rs.getDate("date_seance").toLocalDate() : null;
            LocalTime duree = rs.getTime("duree") != null ? rs.getTime("duree").toLocalTime() : null;
            String objectifs = rs.getString("objectifs");
            int idCour = rs.getInt("cour_id");

            // Charger l'objet Cour correspondant
            Cour cour = allCours.stream()
                    .filter(c -> c.getId() == idCour)
                    .findFirst()
                    .orElse(null);

            if (cour == null) {
                System.out.println("⚠️ Aucun cours trouvé pour cour_id: " + idCour);
                cour = new Cour();
                cour.setId(idCour);
            } else {
                System.out.println("Cours trouvé pour cour_id: " + idCour + " -> " + cour.getTypeCour());
            }

            Seance s = new Seance(id, dateSeance, duree, objectifs, cour);
            seances.add(s);
            System.out.println("Séance récupérée : " + s);
        }
        System.out.println("Nombre total de séances récupérées : " + seances.size());
        return seances;
    }

    public Map<Integer, Integer> recupererToutesParticipations() throws SQLException {
        Map<Integer, Integer> participationsCount = new HashMap<>();
        sql = "SELECT seance_id, COUNT(*) as count FROM participation_seance GROUP BY seance_id";
        ps = cnx.prepareStatement(sql);
        rs = ps.executeQuery();
        System.out.println("Début de la récupération des participations aux séances...");
        int rowCount = 0;
        while (rs.next()) {
            int seanceId = rs.getInt("seance_id");
            int count = rs.getInt("count");
            System.out.println("Séance ID: " + seanceId + ", Participants: " + count);
            participationsCount.put(seanceId, count);
            rowCount++;
        }
        if (rowCount == 0) {
            System.out.println("Aucune participation trouvée dans la table participation_seance.");
        } else {
            System.out.println("Total participations trouvées : " + participationsCount.size() + " séances différentes");
        }
        return participationsCount;
    }
}