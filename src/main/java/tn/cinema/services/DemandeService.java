package tn.cinema.services;

import tn.cinema.entities.Demande;
import tn.cinema.tools.Mydatabase;

import java.sql.Connection;
import java.util.List;

public abstract class DemandeService implements IServices<Demande>{
    Connection cnx;
    public DemandeService(){
        cnx= Mydatabase.getInstance().getCnx();
    }


    public void ajouter(Demande demande) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<Demande> recuperer() {
        return List.of();
    }
}
