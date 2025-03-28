package tn.cinema.entities;

import java.util.Date;
import java.util.Objects;

import static java.sql.Types.NULL;

public class Demande {
    private int id;
    private int userId;
    private Integer adminId;
    private int nombreJours;
    private String description;
    private String type;
    private String lienSupplementaire;
    private String statut ;
    private Date dateSoumission;
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

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public int getNombreJours() {
        return nombreJours;
    }

    public void setNombreJours(int nombreJours) {
        this.nombreJours = nombreJours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLienSupplementaire() {
        return lienSupplementaire;
    }

    public void setLienSupplementaire(String lienSupplementaire) {
        this.lienSupplementaire = lienSupplementaire;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Date getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(Date dateSoumission) {
        this.dateSoumission = dateSoumission;
    }
    public Demande(){
        this.dateSoumission = new Date();
    }
    public Demande(int id,int userId,Integer adminId,int nombreJours,String description, String type,String lienSupplementaire,String statut){
        this.id=id;
        this.userId=userId;
        this.adminId=adminId;
        this.nombreJours=nombreJours;
        this.description=description;
        this.type=type;
        this.lienSupplementaire=lienSupplementaire;
        this.statut=statut;
        this.dateSoumission=new Date();

    }
    public Demande(int nombreJours,String description, String type,String lienSupplementaire){
        this.userId=userId;
        this.adminId=adminId;
        this.nombreJours=nombreJours;
        this.description=description;
        this.type=type;
        this.lienSupplementaire=lienSupplementaire;
        this.statut=statut;
        this.dateSoumission=new Date();

    }
    public Demande(int userId,int nombreJours,String description, String type,String lienSupplementaire){
        this.userId=userId;
        this.adminId=NULL;
        this.nombreJours=nombreJours;
        this.description=description;
        this.type=type;
        this.lienSupplementaire=lienSupplementaire;
        this.statut=statut;
        this.dateSoumission=new Date();

    }


    @Override
    public String toString() {
        return "Demande{" +
                "id=" + id +
                ", userId=" + userId +
                ", adminId=" + adminId +
                ", nombreJours=" + nombreJours +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", lienSupplementaire='" + lienSupplementaire + '\'' +
                ", statut='" + statut + '\'' +
                ", dateSoumission=" + dateSoumission +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Demande demande = (Demande) o;
        return id == demande.id && userId == demande.userId && nombreJours == demande.nombreJours && Objects.equals(adminId, demande.adminId) && Objects.equals(description, demande.description) && Objects.equals(type, demande.type) && Objects.equals(lienSupplementaire, demande.lienSupplementaire) && Objects.equals(statut, demande.statut) && Objects.equals(dateSoumission, demande.dateSoumission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, adminId, nombreJours, description, type, lienSupplementaire, statut, dateSoumission);
    }
}
