package com.example.projektkalkulationeksamen.exceptions.user;

public class UserCreationException extends RuntimeException {
    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
