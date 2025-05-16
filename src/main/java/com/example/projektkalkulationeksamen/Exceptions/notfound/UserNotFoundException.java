package com.example.projektkalkulationeksamen.Exceptions.notfound;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException (String message){
        super (message);
    }

    public UserNotFoundException (String message, Throwable cause){
        super (message, cause);
    }
}
