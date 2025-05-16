package com.example.projektkalkulationeksamen.Exceptions.notfound;

public class ProjectNotFoundException extends NotFoundException {
    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
