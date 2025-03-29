package tn.cinema.services;

<<<<<<< HEAD
import tn.cinema.entities.Projection;

import java.util.List;

public class ProjectionService implements IServices<Projection>{
    @Override
    public void ajouter(Projection projection) {
=======
import tn.cinema.entities.Films;
import tn.cinema.entities.Projection;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectionService implements IServices<Projection>{

    Connection cnx;
    public ProjectionService() {
        cnx= Mydatabase.getInstance().getCnx();
    }


    @Override
    public void ajouter(Projection projection) throws SQLException {
        String sql = "INSERT INTO projection (capaciter, date_projection, prix) VALUES (?, ?, ?)";
        PreparedStatement ps =cnx.prepareStatement(sql);
        ps.setInt(1, projection.getCapaciter());
        ps.setDate(2, java.sql.Date.valueOf(projection.getDate_projection()));
        ps.setFloat(3, projection.getPrix());
        ps.executeUpdate();
        System.out.println("Projection added");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM projection WHERE id = ?";
        PreparedStatement ps= cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Projection deleted");
>>>>>>> Publicites

    }

    @Override
<<<<<<< HEAD
    public void supprimer(int id) {

    }

    @Override
    public void modifier(int id) {

    }

    @Override
    public List<Projection> recuperer() {
        return List.of();
    }
}
=======
    public void modifier(Projection projection) throws SQLException {

    }

    public void modifier(int id,float prix) throws SQLException {
        String sql = "UPDATE projection SET prix = '" + prix + "' WHERE id = " + id;
        Statement ste = cnx.createStatement();
        ste.executeUpdate(sql);
        System.out.println("Projection modified successfully");
    }

    @Override
    public List<Projection> recuperer() throws SQLException {
        String sql = "SELECT * FROM projection";
        Statement ste= cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Projection> projections = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int capaciter = rs.getInt("Capaciter");
            LocalDate date_projection = rs.getDate("date_projection").toLocalDate();
            float prix = rs.getFloat("prix");
            Projection p = new Projection(id,capaciter,date_projection,prix);
            projections.add(p);
        }
        return projections;
    }

    public Projection recupererParId(int idProjection) throws SQLException {
        String sql = "SELECT * FROM projection WHERE id = " + idProjection;
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);

        if (rs.next()) { // If a projection with the given ID exists
            int id = rs.getInt("id");
            int capaciter = rs.getInt("capaciter");
            LocalDate date_projection = rs.getDate("date_projection").toLocalDate();
            float prix = rs.getFloat("prix");

            return new Projection(id, capaciter, date_projection, prix);
        }

        return null; // If no projection is found
    }


}
>>>>>>> Publicites
