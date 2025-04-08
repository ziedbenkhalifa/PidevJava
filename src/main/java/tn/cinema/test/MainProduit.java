package tn.cinema.test;

import tn.cinema.tools.Mydatabase;
import tn.cinema.services.ProduitService;
import tn.cinema.entities.Produit;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;
import java.time.LocalDateTime;
import java.util.List;

public class MainProduit {
    public static void main(String[] args){

        // Test Produit CRUD
        ProduitService produitService = new ProduitService();

        // Test de l'ajout d'un produit

        Produit produit2 = new Produit("Produit 2", 20.0, "Categorie 2", "Description produit 1", "image1.jpg", LocalDateTime.now());

        produitService.ajouter(produit2);

        // Test de la récupération de tous les produits
        List<Produit> produits = produitService.recuperer();
        System.out.println("Liste des produits récupérés :");
        for (Produit produit : produits) {
            System.out.println(produit);
        }

        // Test de la modification d'un produit (on suppose que l'ID du produit à modifier est 1)
        Produit produitModifie = new Produit(1, "Produit Modifié", 25.0, "Categorie Modifiée", "Description modifiée", "image_modifie.jpg", LocalDateTime.now());
        produitService.modifier(produitModifie);

        // Test de la récupération après modification
        produits = produitService.recuperer();
        System.out.println("\nListe des produits après modification :");
        for (Produit produit : produits) {
            System.out.println(produit);
        }

        // Test de la suppression d'un produit (on suppose que l'ID du produit à supprimer est 1)
        produitService.supprimer(9);

        // Test de la récupération après suppression
        produits = produitService.recuperer();
        System.out.println("\nListe des produits après suppression :");
        for (Produit produit : produits) {
            System.out.println(produit);
        }

        // Test Commande CRUD
        CommandeService commandeService = new CommandeService();

        // Test de l'ajout d'une commande
        Commande commande1 = new Commande(1, 100.0, "en cours");
        commandeService.ajouter(commande1);

        // Test de la récupération de toutes les commandes
        List<Commande> commandes = commandeService.recuperer();
        System.out.println("\nListe des commandes récupérées :");
        for (Commande commande : commandes) {
            System.out.println(commande);
        }

        // Test de la modification d'une commande (on suppose que l'ID de la commande à modifier est 1)
        Commande commandeModifiee = new Commande(1, 200.0, "livrée");
        commandeModifiee.setId(1);  // Assurez-vous que l'ID correspond à une commande existante
        commandeService.modifier(commandeModifiee);

        // Test de la récupération après modification
        commandes = commandeService.recuperer();
        System.out.println("\nListe des commandes après modification :");
        for (Commande commande : commandes) {
            System.out.println(commande);
        }

        // Test de la suppression d'une commande (on suppose que l'ID de la commande à supprimer est 1)
        commandeService.supprimer(1);  // Remplacez l'ID par celui que vous voulez supprimer

        // Test de la récupération après suppression
        commandes = commandeService.recuperer();
        System.out.println("\nListe des commandes après suppression :");
        for (Commande commande : commandes) {
            System.out.println(commande);
        }

        // Associer produit et commande (id fictifs)
        commandeService.ajouterProduitACommande(2, 10);
        commandeService.ajouterProduitACommande(2, 11);
    }
}