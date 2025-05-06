package com.example.projektkalkulationeksamen.Exceptions;

import com.example.projektkalkulationeksamen.Model.Project;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
