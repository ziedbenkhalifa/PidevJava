package tn.cinema.services;

import tn.cinema.entities.Publicite;
import tn.cinema.tools.Mydatabase;

import java.sql.Connection;
import java.util.List;

public abstract class PubliciteService implements IServices<Publicite> {
    Connection cnx;
    public PubliciteService(){
        cnx= Mydatabase.getInstance().getCnx();
    }


    public void ajouter(Publicite publicite) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<Publicite> recuperer() {
        return List.of();
    }
}
