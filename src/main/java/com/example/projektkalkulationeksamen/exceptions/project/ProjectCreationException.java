package com.example.projektkalkulationeksamen.exceptions.project;

public class ProjectCreationException extends RuntimeException {

    public ProjectCreationException (String message) {
        super(message);
    }

    public ProjectCreationException (String message, Throwable cause) {
        super(message, cause);
    }
}
