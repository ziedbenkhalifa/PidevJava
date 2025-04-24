package tn.cinema.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Commande {
    private int id;
    private int userId;
    private LocalDateTime dateCommande;
    private double montantPaye;
    private String etat;

    public Commande() {
    }

    public Commande(int userId, double montantPaye, String etat) {
        this.userId = userId;
        this.dateCommande = LocalDateTime.now();
        this.montantPaye = montantPaye;
        this.etat = etat;
    }

    public Commande(int id, int userId, LocalDateTime dateCommande, double montantPaye, String etat) {
        this.id = id;
        this.userId = userId;
        this.dateCommande = dateCommande;
        this.montantPaye = montantPaye;
        this.etat = etat;
    }

    public Commande(double montantPaye, String etat) {
        this.dateCommande = LocalDateTime.now();
        this.montantPaye = montantPaye;
        this.etat = etat;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(double montantPaye) {
        this.montantPaye = montantPaye;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", userId=" + userId +
                ", dateCommande=" + dateCommande +
                ", montantPaye=" + montantPaye +
                ", etat='" + etat + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commande commande = (Commande) o;
        return id == commande.id && userId == commande.userId && Double.compare(montantPaye, commande.montantPaye) == 0 && Objects.equals(dateCommande, commande.dateCommande) && Objects.equals(etat, commande.etat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, dateCommande, montantPaye, etat);
    }
}