package com.example.projektkalkulationeksamen.service;

import com.example.projektkalkulationeksamen.exceptions.notfound.UserNotFoundException;
import com.example.projektkalkulationeksamen.exceptions.security.AuthenticationFailedException;
import com.example.projektkalkulationeksamen.exceptions.user.*;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.validator.UserValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String rawPassword) {
        logger.debug("Attempting to sign user in with username: {} ", username);
        try {

            User user = userService.getUserByUsername(username);

            if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                logger.warn("Login failed for username '{}': invalid password", username);
                throw new AuthenticationFailedException("Invalid username or password");
            }

            return user;

        } catch (UserNotFoundException e) {
            logger.error("Failed login attempt: no user found with username: {}", username, e);
            throw new AuthenticationFailedException("Invalid username or password", e);
        }
    }

    public void adminRegister(String username, String rawPassword, Role role) {
        logger.debug("Attempting to register new user with username: {}", username);

        try {
            if (userService.userExistsByUsername(username)) {
                logger.warn("Username already taken: {}", username);
                throw new UserCreationException("Username already taken");
            }

            UserValidator.registrationValidate(username, rawPassword);

            String hashedPassword = passwordEncoder.encode(rawPassword);

            userService.addUser(new User(username, hashedPassword, role));

            logger.info("Successfully registered new user with username: {}", username);

        } catch (UserCreationException e) {
            logger.error("Failed to register user with username: {}", username, e);
            throw new AuthRegisterException(e.getMessage(), e);
        }
    }

    public void adminUpdate(int id, String username, String rawPassword, Role role) {
        logger.debug("Attempting to update existing user with ID: {}", id);

        try {
            User currentUser = userService.getUserById(id);

            User updatedUser = UserValidator.validateAndPrepareUpdate(currentUser, username, rawPassword, role, userService);

            userService.updateUser(updatedUser);
            logger.info("Successfully updated user with ID: {}", id);

        } catch (UserNotFoundException | UserCreationException e) {
            logger.error("Failed to update user with ID: {}", id, e);
            throw new UserUpdateException(e.getMessage(), e);
        }
    }


    public void logout(HttpSession session) {
        logger.info("Invalidating session with id: {}", session.getAttribute("userId"));
        session.invalidate();
    }


}
