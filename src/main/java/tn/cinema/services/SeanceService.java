package tn.cinema.services;

import tn.cinema.entities.Seance;

import java.util.List;

public abstract class SeanceService implements IServices<Seance>{

    public void ajouter(Seance seance) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<Seance> recuperer() {
        return List.of();
    }
}
