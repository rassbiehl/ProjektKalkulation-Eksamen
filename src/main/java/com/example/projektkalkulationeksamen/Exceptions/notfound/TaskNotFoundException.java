package com.example.projektkalkulationeksamen.Exceptions.notfound;

public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException(String message) {
        super(message);
    }
    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
}
}
