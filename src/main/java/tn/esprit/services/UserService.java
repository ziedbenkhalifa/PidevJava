package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.tools.Mydatabase;

import java.sql.Connection;
import java.util.List;

public class UserService implements IServices<User>{
    Connection cnx;
    public UserService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(User u) {

    }

    @Override
    public void supprimer(User u) {

    }

    @Override
    public void modifier(int id) {

    }

    @Override
    public List<User> recuperer() {
        return List.of();
    }
}
