package tn.cinema.entities;


import java.time.LocalDateTime;

public class Notification{

    private int id;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public Notification(int id, String message, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return message + " (Exp: " + expiresAt.toLocalDate() + ")";
    }
}

