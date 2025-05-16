package com.example.projektkalkulationeksamen.Exceptions.user;

public class AuthRegisterException extends RuntimeException {
    public AuthRegisterException (String message) {
        super(message);
    }

    public AuthRegisterException (String message, Throwable cause) {
        super(message, cause);
    }
}
