package com.example.projektkalkulationeksamen.exceptions.user;

public class AuthRegisterException extends RuntimeException {
    public AuthRegisterException (String message) {
        super(message);
    }

    public AuthRegisterException (String message, Throwable cause) {
        super(message, cause);
    }
}
