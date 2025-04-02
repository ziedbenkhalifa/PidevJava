package tn.cinema.test;

import tn.cinema.entities.Equipement;
import tn.cinema.entities.Salle;
import tn.cinema.services.EquipementService;
import tn.cinema.services.SalleService;
import tn.cinema.tools.Mydatabase;

import java.sql.SQLException;

public class MainSalle {
    public static void main(String[] args) {
        Mydatabase connexion = Mydatabase.getInstance();

        SalleService salleService = new SalleService();
        EquipementService equipementService = new EquipementService();

        Salle s = new Salle(1, "Grande Salle!", "oui", "Disponible", "IMAX", "Centre");
        s.setId_salle(27);
        s.setNom_salle("salleee");
        // Equipement e = new Equipement( 22, "cadre", "Vidéo", "Fonctionnel?");
        Equipement e1 = new Equipement( 22, "imprimante", "Vidéo", "Fonctionnel?");
        Equipement e4 = new Equipement( 22, "korsi", "Vidéo", "mediocre");
        e1.setId(50);
        e1.setNom("BHHH");

        try {
            //equipementService.ajouter(e1);
            //equipementService.supprimer(25);
            equipementService.modifier(e1);
            System.out.println(equipementService.recuperer());
        } catch (SQLException E) {
            System.out.println(E.getMessage());
        }

        try {
            //salleService.ajouter(s);
            salleService.modifier(s);
            // salleService.supprimer(27);
            System.out.println(salleService.recuperer());
        } catch (SQLException E) {
            System.out.println(E.getMessage());
        }
    }
}