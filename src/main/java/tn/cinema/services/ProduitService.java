package tn.cinema.services;

import tn.cinema.entities.Produit;

import java.util.List;

public abstract class ProduitService implements IServices<Produit>{

    public void ajouter(Produit produit) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<Produit> recuperer() {
        return List.of();
    }
}
