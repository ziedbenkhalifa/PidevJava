package tn.cinema.test;

import tn.cinema.entities.User;
import tn.cinema.tools.Mydatabase;
import tn.cinema.services.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args){
        Mydatabase connexion = Mydatabase.getInstance();
        UserService us = new UserService();
        User u = new User(
                "Eden",
                LocalDate.of(1990, 2, 23),  // Year 1990, February (month 2), day 23
                "eden.bensalem@example.com",
                "Admin",
                "MotDePasse123!",
                "photo_profil.jpg",
                "face_token_123456"
        );
    //us.ajouter(u);
    //us.supprimer(36);
        // Appel de la méthode pour modifier l'utilisateur avec id=3
        us.modifier(
                54,
                "Hazard",
                LocalDate.of(1997, 6, 23),
                "hazard.bensalem@example.com",
                "Client",
                "NouveauMotDePasseeee!",
                "new.jpg",
                "58556"
        );

        /*List<User> users = us.recuperer(); // Appel de la méthode recuperer()

        System.out.println("\nListe des utilisateurs :");
        System.out.println("------------------------");
        for (User user : users) {
            System.out.println(user); // Utilise la méthode toString() de User
        }
        System.out.println("------------------------");
        System.out.println("Total utilisateurs : " + users.size());*/
    }
}
