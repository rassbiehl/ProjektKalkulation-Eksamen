package com.example.projektkalkulationeksamen.exceptions.milestone;

public class MilestoneUpdateException extends RuntimeException {
    public MilestoneUpdateException (String message) {
        super (message);
    }

    public MilestoneUpdateException(String message, Throwable cause) {
        super (message, cause);
    }
}