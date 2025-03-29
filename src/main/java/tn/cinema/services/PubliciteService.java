package tn.cinema.services;

import tn.cinema.entities.Publicite;
import tn.cinema.tools.Mydatabase;

import java.sql.Connection;
import java.util.List;

public class PubliciteService implements IServices<Publicite> {
    Connection cnx;
    public PubliciteService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Publicite publicite) {

    }

    @Override
    public void supprimer(int id) {

    }

    @Override
    public void modifier(Publicite publicite) {

    }

    @Override
    public List<Publicite> recuperer() {
        return List.of();
    }
}
