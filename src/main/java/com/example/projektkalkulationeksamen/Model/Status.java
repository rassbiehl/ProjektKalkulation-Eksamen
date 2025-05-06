package com.example.projektkalkulationeksamen.Model;

public enum Status {
    COMPLETED("Completed"),
    IN_PROGRESS("In progress"),
    NOT_STARTED("Not started");

    private String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
