package tn.cinema.services;

import tn.cinema.entities.Projection;

import java.util.List;

public abstract class ProjectionService implements IServices<Projection>{

    public void ajouter(Projection projection) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<Projection> recuperer() {
        return List.of();
    }
}
