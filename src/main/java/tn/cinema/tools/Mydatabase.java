package tn.cinema.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mydatabase {
    public final String URL ="jdbc:mysql://localhost:3306/pidev";
    public final String USER ="root";
    public final String PWD ="";
    private Connection cnx;
    static Mydatabase  myDataBase;
    private Mydatabase (){
        try {
            cnx= DriverManager.getConnection(URL,USER,PWD);
            System.out.println("cnx etabliee !!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Mydatabase  getInstance(){
        if(myDataBase==null)
            myDataBase=new Mydatabase ();
        return myDataBase;

    }

    public Connection getCnx() {
        return cnx;
    }
}


