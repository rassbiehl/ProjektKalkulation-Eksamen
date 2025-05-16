package com.example.projektkalkulationeksamen.Validator;

import com.example.projektkalkulationeksamen.Exceptions.project.ProjectCreationException;

public class ProjectDataValidator {
    public static void validateName(String name){
        if (name == null || name.isBlank()){
            throw new ProjectCreationException("Name cannot be empty");
        }
    }

    public static void validateDescription(String decription){
        if (decription == null || decription.isBlank()){
            throw new ProjectCreationException("Description cannot be empty");
        }
    }

    public static void validateEstimatedHours(int hours){
        if (hours < 0){
            throw new ProjectCreationException("Estimated hours must be positive digit");
        }
    }

    public static void validateActualHoursUsed(int actualHours){
        if (actualHours < 0) {
            throw new ProjectCreationException("Actual hours used must be positive digit");
        }
    }

    public static void validateAllFields(String name, String description, int hours, int actualHours){
        validateName(name);
        validateDescription(description);
        validateEstimatedHours(hours);
        validateActualHoursUsed(actualHours);
    }


}
