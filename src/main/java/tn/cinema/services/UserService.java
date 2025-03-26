package tn.cinema.services;

import tn.cinema.entities.User;
import tn.cinema.tools.Mydatabase;

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
    public void supprimer(int id) {

    }

    @Override
    public void modifier(int id) {

    }

    @Override
    public List<User> recuperer() {
        return List.of();
    }
}
