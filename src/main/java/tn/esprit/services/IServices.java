package tn.esprit.services;

import tn.esprit.entities.User;
import java.util.List;

public interface IServices<T> {
    void ajouter(T t);
    void supprimer(T t);
    void modifier(int id);
    List<T> recuperer();
}