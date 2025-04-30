package com.example.projektkalkulationeksamen.Model;

public enum Role {
    ADMIN("Admin"),
    PROJECTMANAGER("Projectmanager"),
    EMPLOYEE("Employee");

    private String displayName;

    Role(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
