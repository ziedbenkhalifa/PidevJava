package tn.cinema.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private String categorie;
    private String description;
    private String image;
    private LocalDateTime date;

    // Constructeur
    public Produit() {
    }

    public Produit(String nom, double prix, String categorie, String description, String image, LocalDateTime date) {
        this.nom = nom;
        this.prix = prix;
        this.categorie = categorie;
        this.description = description;
        this.image = image;
        this.date = date;
    }
    public Produit(int id, String nom, double prix, String categorie, String description, String image, LocalDateTime date) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.categorie = categorie;
        this.description = description;
        this.image = image;
        this.date = date;
    }

    // Getters et Setters
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

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    // Méthode toString pour afficher l'objet
    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", categorie='" + categorie + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Produit produit = (Produit) o;
        return id == produit.id && Double.compare(prix, produit.prix) == 0 && Objects.equals(nom, produit.nom) && Objects.equals(categorie, produit.categorie) && Objects.equals(description, produit.description) && Objects.equals(image, produit.image) && Objects.equals(date, produit.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prix, categorie, description, image, date);
    }
}