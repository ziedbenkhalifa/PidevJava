package tn.cinema.services;

import tn.cinema.entities.User;
import tn.cinema.tools.Mydatabase;

import java.sql.Connection;
import java.util.List;

public abstract class UserService implements IServices<User>{
    Connection cnx;
    public UserService(){
        cnx= Mydatabase.getInstance().getCnx();
    }


    public void ajouter(User u) {

    }


    public void supprimer(int id) {

    }


    public void modifier(int id) {

    }


    public List<User> recuperer() {
        return List.of();
    }
}
