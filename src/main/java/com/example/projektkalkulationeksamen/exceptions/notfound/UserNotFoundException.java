package com.example.projektkalkulationeksamen.exceptions.notfound;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException (String message){
        super (message);
    }

    public UserNotFoundException (String message, Throwable cause){
        super (message, cause);
    }
}
