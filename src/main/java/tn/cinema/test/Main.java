package tn.cinema.test;

import tn.cinema.entities.User;
import tn.cinema.tools.Mydatabase;
import tn.cinema.services.UserService;

import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args){
        Mydatabase connexion = Mydatabase.getInstance();
        UserService us = new UserService();
        User u = new User(
                "Ali Ben Salem",                        // Nom
                new Date(95, 4, 23),                    // Date de naissance (23 mai 1995)
                "ali.bensalem@example.com",             // Email
                "Admin",                                // Rôle
                "MotDePasse123!",                       // Mot de passe
                "photo_profil.jpg",                     // Photo (nom du fichier)
                "face_token_123456"                     // Face Token (exemple)
        );
    //us.ajouter(u);
    //us.supprimer(36);
        // Appel de la méthode pour modifier l'utilisateur avec id=3
        us.modifier(
                42,
                "Hammmmmmouda Ben Salem",
                new Date(95, 4, 23),
                "hammouda.bensalem@example.com",
                "Client",
                "NouveauMotDePasse!",
                "nouvelle_photo.jpg",
                "new_face_token"
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
