package tn.cinema.test;

import tn.cinema.entities.Cour;
import tn.cinema.entities.Seance;
import tn.cinema.services.CourService;
import tn.cinema.services.SeanceService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MainCour {
    public static void main(String[] args) throws SQLException {
       CourService cs = new CourService();
        SeanceService ss = new SeanceService();

        //LocalDateTime dateDebut = LocalDateTime.now();
        //LocalDateTime dateFin= LocalDateTime.of(2025, 3, 30, 16, 30);
        LocalDate dateSeance = LocalDate.now();
        LocalTime duree = LocalTime.of(2, 30); // Durée de 2 heures et 30 minutes

       //Cour c = new Cour("cinema", 100, dateDebut, dateFin);
       //Cour c = new Cour(38, "cinema", 250.000,LocalDateTime.now(), LocalDateTime.of(2025, 4, 30, 16, 30));
        Seance s = new Seance(19,dateSeance, duree,"Histoire du Cinéma",33);
       //try {
           //cs.ajouter(c);
           //cs.supprimer(34);
              //for (Cour cour : cs.recuperer()) {
                //   System.out.println(cour);
           //cs.modifier(c);
          //     } catch (SQLException e) {
        //System.out.println(e.getMessage());
      // }

        try {
           // ss.ajouter(s);
            //ss.supprimer(17);
            ss.modifier(s);

            //for (Seance seance: ss.recuperer()){ ;
            //System.out.println(seance);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //int userId = 1;

        //try {

         //   List<Integer> participations = cs.recupererParticipations(userId);

          //  if (participations.isEmpty()) {
           //     System.out.println("Aucune participation trouvée");
           // } else {
           //     System.out.println("Participations de l'utilisateur : " + participations);
        //    }
      //  } catch (SQLException e) {
         //   System.out.println(e.getMessage());
       // }


    }}
