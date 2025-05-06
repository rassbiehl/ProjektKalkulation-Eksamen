package com.example.projektkalkulationeksamen.Model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;
    private Role role;


    public User() {
    }

    public User(int id, String username, String passwordHash, LocalDateTime createdAt, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.role = role;
    }

    public User(int id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }


    public User(String username, String passwordHash , Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

