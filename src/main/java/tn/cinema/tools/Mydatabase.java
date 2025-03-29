package tn.cinema.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mydatabase {
    public final String URL="jdbc:mysql://localhost:3306/newbase";
    public final String USER="root";
    public final String PWD="";
    private Connection cnx;
    public static Mydatabase mydatabase;
    private Mydatabase(){

        try {
            cnx= DriverManager.getConnection(URL,USER,PWD);
            System.out.println("connexion etablieeee");
        } catch (SQLException e) {
           // throw new RuntimeException(e);
            System.out.println(e.getMessage());
        }
    }
    public static Mydatabase getInstance(){
        if(mydatabase==null)
            mydatabase=new Mydatabase();
        return mydatabase;
    }



    public Connection getCnx() {
        return cnx;
    }

    public void setCnx(Connection cnx) {
        this.cnx = cnx;
    }
}
