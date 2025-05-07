package com.example.projektkalkulationeksamen.Exceptions;

import com.example.projektkalkulationeksamen.Repository.TaskCoworkerRepository;

public class TaskCoworkerException extends RuntimeException {
    public TaskCoworkerException (String message) {
        super(message);
    }

    public TaskCoworkerException(String message, Throwable cause) {
        super(message, cause);
    }
}
