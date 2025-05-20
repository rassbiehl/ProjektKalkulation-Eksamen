package com.example.projektkalkulationeksamen.exceptions.task;

public class TaskUpdateException  extends RuntimeException {
    public TaskUpdateException (String message) {
        super(message);
    }
    public TaskUpdateException (String message, Throwable cause) {
        super(message, cause);
    }
}
