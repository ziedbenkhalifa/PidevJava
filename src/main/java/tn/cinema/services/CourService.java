package tn.cinema.services;

import tn.cinema.entities.Cour;

import java.util.List;

public abstract class CourService implements IServices<Cour>{

    public void ajouter(Cour cour) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }

    @Override
    public List<Cour> recuperer() {
        return List.of();
    }
}
