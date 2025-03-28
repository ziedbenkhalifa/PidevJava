package tn.cinema.services;

import tn.cinema.entities.Demande;
import tn.cinema.tools.Mydatabase;

import java.sql.Connection;
import java.util.List;

public class DemandeService implements IServices<Demande>{
    Connection cnx;
    public DemandeService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Demande demande) {

    }

    @Override
    public void supprimer(int id) {

    }

    @Override
    public void modifier(Demande demande) {

    }

    @Override
    public List<Demande> recuperer() {
        return List.of();
    }
}
