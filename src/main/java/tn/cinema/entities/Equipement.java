package tn.cinema.entities;

import java.util.Objects;

public class Equipement {


<<<<<<< HEAD
    private int id,salle_id;
    private String nom,type, etat;

    public Equipement (int id, int salle_id, String nom, String type, String etat) {
=======
    private int id,id_salle;
    private String nom,type, etat;

    public Equipement (int id, int id_salle, String nom, String type, String etat) {
>>>>>>> Publicites
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
<<<<<<< HEAD
        this.salle_id = salle_id;
    }
    public Equipement (int salle_id, String nom, String type, String etat) {
=======
        this.id_salle = id_salle;
    }
    public Equipement (int id_salle, String nom, String type, String etat) {
>>>>>>> Publicites
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
<<<<<<< HEAD
        this.salle_id = salle_id;
=======
        this.id_salle = id_salle;
>>>>>>> Publicites
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

<<<<<<< HEAD
    public int getSalle_id() {
        return salle_id;
    }
    public void setSalle_id(int salle_id) {
        this.salle_id = salle_id;
=======
    public int getId_salle() {
        return id_salle;
    }
    public void setId_salle(int id_salle) {
        this.id_salle = id_salle;
>>>>>>> Publicites
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
<<<<<<< HEAD
                ", salle_id=" + salle_id +
=======
                ", Id_salle=" + id_salle +
>>>>>>> Publicites
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Equipement that = (Equipement) o;
<<<<<<< HEAD
        return id == that.id && salle_id == that.salle_id && Objects.equals(nom, that.nom) && Objects.equals(type, that.type) && Objects.equals(etat, that.etat);
=======
        return id == that.id && id_salle == that.id_salle && Objects.equals(nom, that.nom) && Objects.equals(type, that.type) && Objects.equals(etat, that.etat);
>>>>>>> Publicites
    }

    @Override
    public int hashCode() {
<<<<<<< HEAD
        return Objects.hash(id, salle_id, nom, type, etat);
    }
}
=======
        return Objects.hash(id, id_salle, nom, type, etat);
    }
}
>>>>>>> Publicites
