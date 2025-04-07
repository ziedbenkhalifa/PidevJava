package tn.cinema.entities;


import java.util.Objects;

public class Salle {
    private int id_salle,nombre_de_place;
    private String  nom_salle, disponibilite, type_salle, statut, emplacement;
    public Salle() {}

    public Salle(int id_salle,int nombre_de_place, String nom_salle, String disponibilite, String type_salle, String statut, String emplacement) {
        this.id_salle = id_salle;
        this.nombre_de_place = nombre_de_place;
        this.nom_salle = nom_salle;
        this.disponibilite = disponibilite;
        this.type_salle = type_salle;
        this.statut = statut;
        this.emplacement = emplacement;
    }
    public Salle(int nombre_de_place, String nom_salle, String disponibilite, String type_salle, String statut, String emplacement) {
        this.id_salle = id_salle;
        this.nombre_de_place = nombre_de_place;
        this.nom_salle = nom_salle;
        this.disponibilite = disponibilite;
        this.type_salle = type_salle;
        this.statut = statut;
        this.emplacement = emplacement;
    }


    public int getId_salle() {
        return id_salle;
    }

    public void setId_salle(int id_salle) {
        this.id_salle = id_salle;
    }

    public int getNombre_de_place() {
        return nombre_de_place;
    }

    public void setNombre_de_place(int nombre_de_place) {
        this.nombre_de_place = nombre_de_place;
    }

    public String getNom_salle() {
        return nom_salle;
    }

    public void setNom_salle(String nom_salle) {
        this.nom_salle = nom_salle;
    }

    public String getType_salle() {
        return type_salle;
    }

    public void setType_salle(String type_salle) {
        this.type_salle = type_salle;
    }

    public String getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(String disponibilite) {
        this.disponibilite = disponibilite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    @Override
    public String toString() {
        return "Salle{" +
                "salle_id=" + id_salle +
                ", nombre_de_place=" + nombre_de_place +
                ", nom_salle='" + nom_salle + '\'' +
                ", disponibilite='" + disponibilite + '\'' +
                ", type_salle='" + type_salle + '\'' +
                ", statut='" + statut + '\'' +
                ", emplacement='" + emplacement + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Salle salle = (Salle) o;
        return id_salle == salle.id_salle && nombre_de_place == salle.nombre_de_place && Objects.equals(nom_salle, salle.nom_salle) && Objects.equals(disponibilite, salle.disponibilite) && Objects.equals(type_salle, salle.type_salle) && Objects.equals(statut, salle.statut) && Objects.equals(emplacement, salle.emplacement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_salle, nombre_de_place, nom_salle, disponibilite, type_salle, statut, emplacement);
    }
}