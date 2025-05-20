package com.example.projektkalkulationeksamen.exceptions.project;

public class ProjectUpdateException extends RuntimeException {

    public ProjectUpdateException(String message) {
        super(message);
    }

    public ProjectUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}