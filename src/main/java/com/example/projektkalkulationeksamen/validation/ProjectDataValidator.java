package com.example.projektkalkulationeksamen.validation;

import com.example.projektkalkulationeksamen.exceptions.project.ProjectCreationException;

public class ProjectDataValidator {

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProjectCreationException("Name cannot be empty");
        }
    }

    public static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new ProjectCreationException("Description cannot be empty");
        }
    }
}
