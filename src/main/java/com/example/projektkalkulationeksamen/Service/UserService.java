package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.UserCreationException;
import com.example.projektkalkulationeksamen.Exceptions.UserNotFoundException;
import com.example.projektkalkulationeksamen.Exceptions.UserUpdateException;
import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        logger.debug("Sends list of all users");
        return userRepository.getAllUsers();
    }

    public User getUserById(int id) {
        try {
            logger.debug("Sends user with ID: {}", id);

            Optional<User> foundUser = userRepository.getUserById(id);

            return foundUser.orElseThrow(() -> new UserNotFoundException("Failed to find user with ID: " + id));
        } catch (DatabaseException e) {
            throw new UserNotFoundException("Failed to find user with ID: " + id, e);
        }
    }

    public User getUserByUsername(String username) {
        try {
            logger.debug("Sends user with username: {}", username);
            Optional<User> foundUser = userRepository.getUserByUsername(username);

            return foundUser.orElseThrow(() -> new UserNotFoundException("Failed to find user by username: " + username));

        } catch (DatabaseException e) {
            logger.error("Failed to retrieve user from Database with username " + username, e);
            throw new UserNotFoundException("Failed to find user with username: " + username);
        }
    }

    public User addUser(User user) {
        try {
            return userRepository.addUser(user);
        } catch (DatabaseException e) {
            logger.error("Failed to add user to database with username: {}", user.getUsername());
            throw new UserCreationException("Failed to create user", e);
        }
    }

    public void deleteUser(int id) {
        logger.debug("Attempting to delete user with id {}", id);

        try {
            boolean deleted = userRepository.deleteUser(id);

            if (!deleted) {
                logger.warn("Failed to delete user with ID {}", id);
                throw new UserNotFoundException("Failed to delete user with ID " + id);
            }
            logger.info("Successfully deleted user with ID {}", id);
        } catch (DatabaseException e) {
            logger.error("Failed to delete user with ID: {}", id);
            throw new UserNotFoundException("Failed to delete user with ID " + id, e);
        }

    }

    public void updateUser(User user) {
        logger.debug("Attempting to update user with ID: {}", user.getId());

        try {
            boolean updated = userRepository.updateUser(user);

            if (!updated) {
                logger.warn("No user found to update with ID {}", user.getId());
                throw new UserNotFoundException("No user found to update with ID: " + user.getId());
            }

            logger.info("Successfully updated user with ID {}", user.getId());

        } catch (DatabaseException e) {
            logger.error("Database error while updating user with ID {}", user.getId(), e);
            throw new UserUpdateException("Database error while updating user with ID: " + user.getId(), user.getId());
        }
    }


    public boolean userExistsByUsername(String username) {
        return userRepository.getUserByUsername(username).isPresent();
    }

    public boolean userExistsById(int id) {
        return userRepository.getUserById(id).isPresent();
    }


}

