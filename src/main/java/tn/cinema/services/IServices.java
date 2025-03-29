package tn.cinema.services;

import java.sql.SQLException;
import java.util.List;

public interface IServices<T> {
    void ajouter(T t) throws SQLException;
    void supprimer(int id) throws SQLException;
    void modifier(T t) throws SQLException;
    List<T> recuperer() throws SQLException;
}