package com.example.projektkalkulationeksamen.exceptions.user;

public class UserUpdateException extends RuntimeException {

    public UserUpdateException(String message) {
        super(message);
    }

    public UserUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

}
