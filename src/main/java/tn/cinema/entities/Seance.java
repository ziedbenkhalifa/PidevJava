package tn.cinema.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Seance {
    private int id;
    private LocalDate dateSeance;
    private LocalTime duree;
    private String objectifs;
    private Cour cour;

    public Seance() {
    }

    public Seance(LocalDate dateSeance, LocalTime duree, String objectifs, Cour cour) {
        this.dateSeance = dateSeance;
        this.duree = duree;
        this.objectifs = objectifs;
        this.cour = cour;
    }

    public Seance(int id, LocalDate dateSeance, LocalTime duree, String objectifs, Cour cour) {
        this.id = id;
        this.dateSeance = dateSeance;
        this.duree = duree;
        this.objectifs = objectifs;
        this.cour = cour;
    }

    public Seance(LocalTime duree, String objectifs, Cour cour) {
        this.duree = duree;
        this.objectifs = objectifs;
        this.cour = cour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public LocalTime getDuree() {
        return duree;
    }

    public void setDuree(LocalTime duree) {
        this.duree = duree;
    }

    public String getObjectifs() {
        return objectifs;
    }

    public void setObjectifs(String objectifs) {
        this.objectifs = objectifs;
    }

    public Cour getCour() {
        return cour;
    }

    public void setCour(Cour cour) {
        this.cour = cour;
    }

    @Override
    public String toString() {
        return "Seance{" +
                "id=" + id +
                ", dateSeance=" + dateSeance +
                ", duree=" + duree +
                ", objectifs='" + objectifs + '\'' +
                ", courId=" + (cour != null ? cour.getId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seance seance = (Seance) o;
        return id == seance.id && Objects.equals(dateSeance, seance.dateSeance) && Objects.equals(duree, seance.duree) && Objects.equals(objectifs, seance.objectifs) && Objects.equals(cour, seance.cour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateSeance, duree, objectifs, cour);
    }
}