package tn.cinema.entities;
<<<<<<< HEAD

=======
>>>>>>> Publicites
import java.time.LocalDateTime;
import java.util.Objects;

public class Cour {
    private int id;
    private String typeCour;
    private double cout;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;


    public Cour() {
    }


    public Cour(String typeCour, double cout, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.typeCour = typeCour;
        this.cout = cout;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }


    public Cour(int id, String typeCour, double cout, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.id = id;
        this.typeCour = typeCour;
        this.cout = cout;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

<<<<<<< HEAD
    // Getters et Setters
=======

>>>>>>> Publicites
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeCour() {
        return typeCour;
    }

    public void setTypeCour(String typeCour) {
        this.typeCour = typeCour;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    @Override
    public String toString() {
        return "Cour{" +
                "id=" + id +
                ", typeCour='" + typeCour + '\'' +
                ", cout=" + cout +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cour cour = (Cour) o;
        return id == cour.id && Double.compare(cout, cour.cout) == 0 && Objects.equals(typeCour, cour.typeCour) && Objects.equals(dateDebut, cour.dateDebut) && Objects.equals(dateFin, cour.dateFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeCour, cout, dateDebut, dateFin);
    }
}