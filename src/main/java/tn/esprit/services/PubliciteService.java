package tn.esprit.services;

import tn.esprit.tools.Mydatabase;

import java.sql.Connection;

public class PubliciteService {
    Connection cnx;
    public PubliciteService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

}
