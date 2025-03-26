package tn.esprit.services;

import tn.esprit.tools.Mydatabase;

import java.sql.Connection;

public class DemandeService {
    Connection cnx;
    public DemandeService(){
        cnx= Mydatabase.getInstance().getCnx();
    }

}
