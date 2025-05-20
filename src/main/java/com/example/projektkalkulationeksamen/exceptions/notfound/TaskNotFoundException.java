package com.example.projektkalkulationeksamen.exceptions.notfound;

public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException(String message) {
        super(message);
    }
    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
}
}
