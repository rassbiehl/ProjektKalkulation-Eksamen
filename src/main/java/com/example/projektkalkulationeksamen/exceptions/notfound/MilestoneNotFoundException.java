package com.example.projektkalkulationeksamen.exceptions.notfound;

public class MilestoneNotFoundException extends NotFoundException {
    public MilestoneNotFoundException(String message) {
        super(message);
    }

    public MilestoneNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
