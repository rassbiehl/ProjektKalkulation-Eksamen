package com.example.projektkalkulationeksamen.validator;

import com.example.projektkalkulationeksamen.exceptions.user.UserCreationException;
import com.example.projektkalkulationeksamen.exceptions.user.UserUpdateException;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    public static User validateAndPrepareUpdate(User currentUser, String newUsername, String rawPassword, Role newRole, UserService userService) {
        String finalUsername = currentUser.getUsername();
        String finalPassword = currentUser.getPasswordHash();

        if (!currentUser.getUsername().equals(newUsername)) {
            if (userService.userExistsByUsername(newUsername)) {
                throw new UserUpdateException("Username already taken");
            }
            validateUsername(newUsername);
            finalUsername = newUsername;
        }

        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            validatePassword(rawPassword);
            finalPassword = new BCryptPasswordEncoder().encode(rawPassword);
        }

        if (finalUsername.equals(currentUser.getUsername())
                && finalPassword.equals(currentUser.getPasswordHash())
                && currentUser.getRole().equals(newRole)) {
            throw new UserUpdateException("No changes made. Nothing to update.");
        }

        return new User(currentUser.getId(), finalUsername, finalPassword, newRole);
    }

}
