package com.example.projektkalkulationeksamen.Exceptions.database;

public class DeletionException extends DatabaseException {
    public DeletionException (String message) {
        super(message);
    }

    public DeletionException (String message, Throwable cause) {
        super(message, cause);
    }
}
