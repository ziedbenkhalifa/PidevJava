/*package tn.cinema.services;

import tn.cinema.entities.Films;
import tn.cinema.tools.Mydatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilmsService implements IServices<Films>{
    Connection cnx;

    public FilmsService() {
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Films films) throws SQLException {
        String sql = "INSERT INTO films (nom_film, realisateur, genre, img, date_production) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps=cnx.prepareStatement(sql);
        ps.setString(1, films.getNom_film());
        ps.setString(2, films.getRealisateur());
        ps.setString(3, films.getGenre());
        ps.setString(4, films.getImg());
        ps.setDate(5, java.sql.Date.valueOf(films.getDate_production()));
        ps.executeUpdate();
        System.out.println("Film added");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM films WHERE id = ?";
        PreparedStatement ps=cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Film deleted");
    }

    @Override
    public void modifier(Films films) throws SQLException {

    }

    public void modifier(int id,String nom_film) throws SQLException {
        String sql = "UPDATE films SET nom_film = '" + nom_film + "' WHERE id = " + id;
        Statement ste = cnx.createStatement();
        ste.executeUpdate(sql);
        System.out.println("Film modified successfully");
    }

    @Override
    public List<Films> recuperer() throws SQLException {
        String sql = "SELECT * FROM films";
        Statement ste= cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Films> films = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String nom_film = rs.getString("nom_film");
            String realisateur = rs.getString("realisateur");
            String genre = rs.getString("genre");
            String img = rs.getString("img");
            LocalDate date_production = rs.getDate("date_production").toLocalDate();
            Films f = new Films(id,nom_film,realisateur,genre,img,date_production);
            films.add(f);
        }
        return films;
    }

    public Films recupererParId(int idFilm) throws SQLException {
        String sql = "SELECT * FROM films WHERE id = " + idFilm;
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);

        if (rs.next()) { // If a film with the given ID exists
            int id = rs.getInt("id");
            String nom_film = rs.getString("nom_film");
            String realisateur = rs.getString("realisateur");
            String genre = rs.getString("genre");
            String img = rs.getString("img");
            LocalDate date_production = rs.getDate("date_production").toLocalDate();

            return new Films(id, nom_film, realisateur, genre, img, date_production);
        }

        return null; // If no film is found
    }

}*/