package tn.cinema.entities;

import java.util.Objects;

public class Equipement {


    private int id,id_salle;
    private String nom,type, etat;

    public Equipement (int id, int id_salle, String nom, String type, String etat) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.id_salle = id_salle;
    }
    public Equipement (int id_salle, String nom, String type, String etat) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.id_salle = id_salle;
    }
    public Equipement () {}


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getId_salle() {
        return id_salle;
    }
    public void setId_salle(int id_salle) {
        this.id_salle = id_salle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getEtat() {
        return etat;
    }
    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "equipement{" +
                "id=" + id +
                ", Id_salle=" + id_salle +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Equipement that = (Equipement) o;
        return id == that.id && id_salle == that.id_salle && Objects.equals(nom, that.nom) && Objects.equals(type, that.type) && Objects.equals(etat, that.etat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, id_salle, nom, type, etat);
    }
}