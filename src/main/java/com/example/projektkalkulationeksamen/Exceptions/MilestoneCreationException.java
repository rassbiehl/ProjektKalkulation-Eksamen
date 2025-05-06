package com.example.projektkalkulationeksamen.Exceptions;

public class MilestoneCreationException extends RuntimeException {
    public MilestoneCreationException(String message) {
        super(message);
    }
    public MilestoneCreationException(String message, Throwable cause){
        super(message, cause);

    }
}
