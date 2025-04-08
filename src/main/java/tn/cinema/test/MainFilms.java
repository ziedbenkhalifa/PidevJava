package tn.cinema.test;

import tn.cinema.entities.Films;
import tn.cinema.entities.Projection;
import tn.cinema.services.FilmsService;
import tn.cinema.services.ProjectionService;

import java.sql.SQLException;
import java.time.LocalDate;

public class MainFilms {
    public static void main(String[] args) {
        FilmsService fs=new FilmsService();
        ProjectionService ps = new ProjectionService();
        Films f=new Films("test2","test2","test","test", LocalDate.of(2001, 1, 1));
        Projection p=new Projection(20,LocalDate.of(2025,12,15), 12.250F);
        try {
            //fs.ajouter(f);
            // ps.ajouter(p);
            // fs.modifier(15,"testUp");
            //ps.modifier(7,60);
            //fs.supprimer(17);
            //ps.supprimer(6);
            //System.out.println(fs.recuperer());
            //System.out.println(ps.recuperer());
            System.out.println(fs.recupererParId(19));
            System.out.println(ps.recupererParId(7));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("helloword");
        }
    }

}