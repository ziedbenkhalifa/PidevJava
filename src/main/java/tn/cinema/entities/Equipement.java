package tn.cinema.entities;

import java.util.Objects;

public class Equipement {


    private int id,salle_id;
    private String nom,type, etat;

    public Equipement (int id, int salle_id, String nom, String type, String etat) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.salle_id = salle_id;
    }
    public Equipement(String nom, String type, int salle_id, String etat)
    {
        this.nom = nom;
        this.type = type;
        this.salle_id = salle_id;
        this.etat = etat;
    }

    public Equipement () {}
    public Equipement(String nom, String type, String salle_id) {
        this.nom = nom;
        this.type = type;
        this.salle_id = Integer.parseInt(salle_id); // Si salle_id est un nombre
        this.etat = "Disponible"; // Ou autre valeur par défaut
    }




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

    public int getSalle_id() {
        return salle_id;
    }
    public void setSalle_id(int salle_id) {
        this.salle_id = salle_id;
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
                ", Id_salle=" + salle_id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Equipement that = (Equipement) o;
        return id == that.id &&salle_id== that.salle_id && Objects.equals(nom, that.nom) && Objects.equals(type, that.type) && Objects.equals(etat, that.etat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salle_id, nom, type, etat);
    }
}
