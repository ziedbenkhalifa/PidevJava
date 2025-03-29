package tn.cinema.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Films {
    private int id;
    private String nom_film,realisateur,genre,img;
    private LocalDate date_production;

    public Films() {
    }

    public Films(int id, String nom_film, String realisateur, String genre, String img, LocalDate date_production) {
        this.id = id;
        this.nom_film = nom_film;
        this.realisateur = realisateur;
        this.genre = genre;
        this.img = img;
        this.date_production = date_production;
    }

    public Films(String nom_film, String realisateur, String genre, String img, LocalDate date_production) {
        this.nom_film = nom_film;
        this.realisateur = realisateur;
        this.genre = genre;
        this.img = img;
        this.date_production = date_production;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_film() {
        return nom_film;
    }

    public void setNom_film(String nom_film) {
        this.nom_film = nom_film;
    }

    public String getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(String realisateur) {
        this.realisateur = realisateur;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public LocalDate getDate_production() {
        return date_production;
    }

    public void setDate_production(LocalDate date_production) {
        this.date_production = date_production;
    }

    @Override
    public String toString() {
        return "Films{" +
                "id=" + id +
                ", nom_film='" + nom_film + '\'' +
                ", realisateur='" + realisateur + '\'' +
                ", genre='" + genre + '\'' +
                ", img='" + img + '\'' +
                ", date_production=" + date_production +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Films films = (Films) o;
        return id == films.id && Objects.equals(nom_film, films.nom_film) && Objects.equals(realisateur, films.realisateur) && Objects.equals(genre, films.genre) && Objects.equals(img, films.img) && Objects.equals(date_production, films.date_production);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom_film, realisateur, genre, img, date_production);
    }
}