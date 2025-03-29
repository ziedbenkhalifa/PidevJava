package tn.cinema.services;

import tn.cinema.entities.Commande;

import java.util.List;

public abstract class CommandeService implements IServices<Commande>{
    @Override
    public void ajouter(Commande commande) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<Commande> recuperer() {
        return List.of();
    }
}
