package tn.cinema.entities;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class User {
    private int id;
    private String nom;
    private LocalDate dateDeNaissance;
    private String email;
    private String role;
    private String motDePasse;
    private String photo;
    private String faceToken;

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

    public LocalDate getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(LocalDate dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

    public User() {
    }
    public User(String nom,LocalDate dateDeNaissance,String email,String role,String motDePasse,String photo,String faceToken) {
        this.nom=nom;
        this.dateDeNaissance=dateDeNaissance;
        this.email=email;
        this.role=role;
        this.motDePasse=motDePasse;
        this.photo=photo;
        this.faceToken=faceToken;
    }
    public User(int id,String nom,LocalDate dateDeNaissance,String email,String role,String motDePasse,String photo,String faceToken) {
        this.id=id;
        this.nom=nom;
        this.dateDeNaissance=dateDeNaissance;
        this.email=email;
        this.role=role;
        this.motDePasse=motDePasse;
        this.photo=photo;
        this.faceToken=faceToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", dateDeNaissance=" + dateDeNaissance +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", photo='" + photo + '\'' +
                ", faceToken='" + faceToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(nom, user.nom) && Objects.equals(dateDeNaissance, user.dateDeNaissance) && Objects.equals(email, user.email) && Objects.equals(role, user.role) && Objects.equals(motDePasse, user.motDePasse) && Objects.equals(photo, user.photo) && Objects.equals(faceToken, user.faceToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, dateDeNaissance, email, role, motDePasse, photo, faceToken);
    }

}