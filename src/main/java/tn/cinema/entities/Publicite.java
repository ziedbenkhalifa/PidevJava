package tn.cinema.entities;

import java.sql.Date;
import java.util.Objects;

public class Publicite {
    private int id;
    private int demandeId;
    private Date dateDebut;
    private Date dateFin;
    private String support;
    private double montant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDemandeId() {
        return demandeId;
    }

    public void setDemandeId(int demandeId) {
        this.demandeId = demandeId;
    }

    public java.sql.Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public java.sql.Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public float getMontant() {
        return (float) montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
    public Publicite(){

    }
    public Publicite(int id,int demandeId,Date dateDebut,Date dateFin,String support,double montant){
        this.id=id;
        this.demandeId=demandeId;
        this.dateDebut=dateDebut;
        this.dateFin=dateFin;
        this.support=support;
        this.montant=montant;
    }
    public Publicite(int demandeId,Date dateDebut,Date dateFin,String support,double montant){
        this.demandeId=demandeId;
        this.dateDebut=dateDebut;
        this.dateFin=dateFin;
        this.support=support;
        this.montant=montant;
    }
    public Publicite(Date dateDebut,Date dateFin,String support,double montant){
        this.id=id;
        this.demandeId=demandeId;
        this.dateDebut=dateDebut;
        this.dateFin=dateFin;
        this.support=support;
        this.montant=montant;
    }


    @Override
    public String toString() {
        return "Publicite{" +
                "id=" + id +
                ", demandeId=" + demandeId +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", support='" + support + '\'' +
                ", montant=" + montant +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Publicite publicite = (Publicite) o;
        return id == publicite.id && demandeId == publicite.demandeId && Double.compare(montant, publicite.montant) == 0 && Objects.equals(dateDebut, publicite.dateDebut) && Objects.equals(dateFin, publicite.dateFin) && Objects.equals(support, publicite.support);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, demandeId, dateDebut, dateFin, support, montant);
    }
}
