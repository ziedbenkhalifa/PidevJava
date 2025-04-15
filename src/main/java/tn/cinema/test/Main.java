package tn.cinema.test;

import tn.cinema.entities.Equipement;
import tn.cinema.entities.Salle;
import tn.cinema.services.EquipementService;
import tn.cinema.services.SalleService;
import tn.cinema.tools.Mydatabase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Mydatabase connexion = Mydatabase.getInstance();

        SalleService salleService = new SalleService();
        EquipementService equipementService = new EquipementService();

        // Test ajout d'équipement
        try {
            Equipement equipementAajouter = new Equipement();
            equipementAajouter.setNom("Équipement 1");
            equipementAajouter.setType("Type 1");
            equipementAajouter.setEtat("Neuf");
            equipementAajouter.setSalle_id(1); // Assure-toi que la salle avec cet ID existe

            equipementService.ajouter(equipementAajouter); // Appel de la méthode ajouter avec un objet valide

            System.out.println("Liste des équipements après ajout:");
            equipementService.recuperer().forEach(System.out::println);

        } catch (SQLException E) {
            System.out.println("Erreur SQL lors de l'ajout d'équipement : " + E.getMessage());
        }

        // Test modification d'équipement
        try {
            Equipement equipementActuel = new Equipement();
            equipementActuel.setId(1); // Assure-toi que l'ID est un ID existant dans la base
            equipementActuel.setNom("Nom modifié");
            equipementActuel.setType("Type modifié");
            equipementActuel.setEtat("État modifié");
            equipementActuel.setSalle_id(2); // Assure-toi que la salle avec cet ID existe

            equipementService.modifier(equipementActuel); // Passer l'objet valide à la méthode modifier

            System.out.println("Liste des équipements après modification:");
            equipementService.recuperer().forEach(System.out::println);

        } catch (SQLException E) {
            System.out.println("Erreur SQL lors de la modification d'équipement : " + E.getMessage());
        }

        // Test suppression d'équipement (exemple commenté, à décommenter pour tester)
        try {
            equipementService.supprimer(1); // Suppression de l'équipement ayant l'ID 1
            System.out.println("Liste des équipements après suppression:");
            equipementService.recuperer().forEach(System.out::println);
        } catch (SQLException E) {
            System.out.println("Erreur SQL lors de la suppression d'équipement : " + E.getMessage());
        }

        // Test récupération des salles
        try {
             salleService.ajouter(new Salle()); // Exemple d'ajout de salle
            // salleService.supprimer(27); // Exemple de suppression de salle
            System.out.println("Liste des salles:");
            salleService.recuperer().forEach(System.out::println);
        } catch (SQLException E) {
            System.out.println("Erreur SQL lors de la gestion des salles : " + E.getMessage());
        }
    }
}
