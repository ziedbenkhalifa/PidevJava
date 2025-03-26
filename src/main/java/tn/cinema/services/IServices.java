package tn.cinema.services;

import java.util.List;

public interface IServices<T> {
    void ajouter(T t);
    void supprimer(int id);
    void modifier(int id);
    List<T> recuperer();
}