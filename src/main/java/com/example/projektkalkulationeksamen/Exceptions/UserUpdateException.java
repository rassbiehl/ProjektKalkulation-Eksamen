package com.example.projektkalkulationeksamen.Exceptions;

public class UserUpdateException extends RuntimeException {
    private final int userId;

    public UserUpdateException(String message, int userId) {
        super(message);
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
