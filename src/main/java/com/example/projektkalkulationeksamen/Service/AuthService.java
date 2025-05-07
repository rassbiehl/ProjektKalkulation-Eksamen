package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Exceptions.*;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.Validator.UserValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public void login(String username, String rawPassword) {
        logger.debug("Attempting to sign user in with username: {} ", username);
        try {

            User user = userService.getUserByUsername(username);


            if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                logger.error("Failed to login attempt for username: {}", username);
                throw new AuthenticationFailedException("Invalid username or password");
            }


        } catch (UserNotFoundException e) {
logger.error("Failed login attempt: no user found with username: {}", username, e);
            throw new AuthenticationFailedException("Invalid username or password", e);
        }
    }

    public void adminRegister(String username, String rawPassword, Role role) {
        logger.debug("Attempting to register new user with username: {}", username);

        if (userService.userExistsByUsername(username)) {
            throw new AuthRegisterException("Username already taken");
        }
        try {
        UserValidator.registrationValidate(username, rawPassword);

        String hashedPassword = passwordEncoder.encode(rawPassword);

            userService.addUser(new User(username, hashedPassword,role));

        } catch (UserCreationException e) {
            logger.error("Failed to register user with username: {}", username, e);
            throw new AuthRegisterException(e.getMessage(), e);
        }
    }

    public void adminUpdate(int id, String username, String rawPassword, Role role) {
        logger.debug("Attempting to update existing user with ID: {}", id);

        try {
            User currentUser = userService.getUserById(id);

            // Username
            String newUsername;
            if (!currentUser.getUsername().equals(username) &&
                    userService.userExistsByUsername(username)) {
                throw new AuthRegisterException("Username already taken");
            } else if (username != null && !username.trim().isEmpty()) {
                UserValidator.validateUsername(username);
                newUsername = username;
            } else {
                newUsername = currentUser.getUsername();
            }

            // Password
            String hashedPassword;
            if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                UserValidator.validatePassword(rawPassword);
                hashedPassword = passwordEncoder.encode(rawPassword);
            } else {
                hashedPassword = currentUser.getPasswordHash();
            }

            // i tilfælde af at både password og username er uændret.
            if (newUsername.equals(currentUser.getUsername())
                    && hashedPassword.equals(currentUser.getPasswordHash())
                    && role == currentUser.getRole()) {
                logger.info("No changes detected for user ID {}. Skipping update.", id);
                throw new UserUpdateException("No changes made. Nothing to update.", id);
            }

            userService.updateUser(new User(id, newUsername, hashedPassword, role));

        } catch (UserNotFoundException | UserCreationException e) {
            throw new UserUpdateException(e.getMessage(), id);
        }
    }


    public void logout(HttpSession session) {
        session.invalidate();
    }


}
