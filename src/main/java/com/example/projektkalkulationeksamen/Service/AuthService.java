package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Exceptions.AuthRegisterException;
import com.example.projektkalkulationeksamen.Exceptions.AuthenticationFailedException;
import com.example.projektkalkulationeksamen.Exceptions.UserCreationException;
import com.example.projektkalkulationeksamen.Exceptions.UserNotFoundException;
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

    public User adminRegister(String username, String rawPassword, Role role) {
        logger.debug("Attempting to register new user with username and rawpassword");

        if (userService.userExistsByUsername(username)) {
            throw new AuthRegisterException("Username already taken");
        }

        UserValidator.registrationValidate(username, rawPassword);

        String hashedPassword = passwordEncoder.encode(rawPassword);


        try {
            return userService.addUser(new User(username, hashedPassword,role));

        } catch (UserCreationException e) {
            logger.error("Failed to register user with username: {}", username, e);
            throw new AuthRegisterException("Failed to register user with username: " + username, e);
        }
    }


    public void logout(HttpSession session) {
        session.invalidate();
    }


}
