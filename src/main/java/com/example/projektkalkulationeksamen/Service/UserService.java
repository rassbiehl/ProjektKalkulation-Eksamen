package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.UserCreationException;
import com.example.projektkalkulationeksamen.Exceptions.UserNotFoundException;
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
            logger.debug("Sends user with ID" + id);

            Optional<User> foundUser = userRepository.getUserById(id);

            if (foundUser == null) {
                throw new UserNotFoundException("Could not find user with ID " + id);
            } else {
                return foundUser.get();
            }
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("Could not fint user with ID " + id, e);
        }
    }

    public User getUserByUsername(String username){
        try {
            logger.debug("Sends user with username " + username);
             Optional<User> foundUser = userRepository.getUserByUsername(username);

             return foundUser.orElseThrow(() -> new UserNotFoundException("Failed to retrieve user from Database by username " + username));

        } catch (UserNotFoundException e){
            logger.error("Failed to retrieve user from Database with username " + username, e);
            throw new UserNotFoundException("Could not find user with Username " + username);
        }
    }

    public void addUser(User user){
        try {
            userRepository.addUser(user);
        } catch (DatabaseException e) {
            throw new UserCreationException("Failed to create user", e);
        }
        }

        public void deleteUser(int id){
        try {
            logger.debug("Attempting to delete user with id " + id);

            boolean deleteUser = userRepository.deleteUser(id);

            if (!deleteUser) {
                logger.warn("Failed to delete user with ID " + id);
                throw new UserNotFoundException("Failed to delete user with ID " + id);
            }
            logger.info("Successfully deleted user with ID " + id);
        }catch (UserNotFoundException e){
            throw new UserNotFoundException("Could not find user with ID " + id, e);
        }

        }


        public boolean userExistsByUsername(String username) {
        return userRepository.getUserByUsername(username).isPresent();
        }


    }

