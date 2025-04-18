package tn.cinema.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Projection {
    private int id,capaciter;
    private LocalDate date_projection;
    private float prix;
    private Films film;


    public Projection() {
    }

    public Projection(int id, int capaciter, LocalDate date_projection, float prix) {
        this.id = id;
        this.capaciter = capaciter;
        this.date_projection = date_projection;
        this.prix = prix;
    }

    public Projection(int capaciter, LocalDate date_projection, float prix) {
        this.capaciter = capaciter;
        this.date_projection = date_projection;
        this.prix = prix;
    }

    public Films getFilm() {
        return film;
    }

    public void setFilm(Films film) {
        this.film = film;
    }

    public int getId() {
        return id;
    }

    public int getCapaciter() {
        return capaciter;
    }

    public LocalDate getDate_projection() {
        return date_projection;
    }

    public float getPrix() {
        return prix;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCapaciter(int capaciter) {
        this.capaciter = capaciter;
    }

    public void setDate_projection(LocalDate date_projection) {
        this.date_projection = date_projection;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Projection{" +
                "id=" + id +
                ", capaciter=" + capaciter +
                ", date_projection=" + date_projection +
                ", prix=" + prix +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projection that = (Projection) o;
        return id == that.id && capaciter == that.capaciter && Float.compare(prix, that.prix) == 0 && Objects.equals(date_projection, that.date_projection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, capaciter, date_projection, prix);
    }
}