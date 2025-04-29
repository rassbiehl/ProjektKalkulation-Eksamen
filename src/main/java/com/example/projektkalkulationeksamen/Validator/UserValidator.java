package com.example.projektkalkulationeksamen.Validator;

import com.example.projektkalkulationeksamen.Exceptions.UserCreationException;

public class UserValidator {

    public static void validateUsername(String username){
        if (username == null || username.isBlank()){
            throw new UserCreationException("Username cannot be empty");
        }
    }
    public static void validatePassword(String password){
if (password == null || password.length() < 6){
    throw new UserCreationException("Password must be atleast 6 characters");
}
    }

    public static void registrationValidate(String username, String password){
        validateUsername(username);
        validatePassword(password);
    }
}
