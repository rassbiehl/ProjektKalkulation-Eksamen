package com.example.projektkalkulationeksamen.exceptions.taskcoworker;

public class TaskCoworkerException extends RuntimeException {
    public TaskCoworkerException (String message) {
        super(message);
    }

    public TaskCoworkerException(String message, Throwable cause) {
        super(message, cause);
    }
}
