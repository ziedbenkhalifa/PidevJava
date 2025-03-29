package tn.cinema.services;

import tn.cinema.entities.Films;

import java.util.List;

public abstract class FilmsService implements IServices<Films>{

    public void ajouter(Films films) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<Films> recuperer() {
        return List.of();
    }
}
