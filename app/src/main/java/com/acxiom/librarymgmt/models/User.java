package com.acxiom.librarymgmt.models;

import com.google.firebase.firestore.PropertyName;

public class User {
    private String userId;
    private String name;
    private String username;
    private String password;
    private boolean isAdmin;
    private boolean isActive;

    public User() {
        // Required for Firestore
    }

    public User(String userId, String name, String username, String password, boolean isAdmin, boolean isActive) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @PropertyName("isAdmin")
    public boolean isAdmin() { return isAdmin; }
    
    @PropertyName("isAdmin")
    public void setAdmin(boolean admin) { isAdmin = admin; }

    @PropertyName("active")
    public boolean isActive() { return isActive; }
    
    @PropertyName("active")
    public void setActive(boolean active) { isActive = active; }
}
