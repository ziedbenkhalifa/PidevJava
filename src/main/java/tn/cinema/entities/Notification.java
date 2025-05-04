package tn.cinema.entities;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // Constructeur
    public Notification(int id, String message, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", message='" + message + "', createdAt=" + createdAt + ", expiresAt=" + expiresAt + "}";
    }
}
