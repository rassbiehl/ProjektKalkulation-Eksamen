package com.example.projektkalkulationeksamen.service;

import com.example.projektkalkulationeksamen.exceptions.database.DatabaseException;
import com.example.projektkalkulationeksamen.exceptions.taskcoworker.TaskCoworkerException;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.repository.TaskCoworkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskCoworkerService {
    private static final Logger logger = LoggerFactory.getLogger(TaskCoworkerService.class);

    private final TaskCoworkerRepository taskCoworkerRepository;
    private final UserService userService;

    @Autowired
    public TaskCoworkerService(TaskCoworkerRepository taskCoworkerRepository, UserService userService) {
        this.taskCoworkerRepository = taskCoworkerRepository;
        this.userService = userService;
    }


    @Transactional
    public void addCoworkersToTask(int taskId, List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            logger.debug("No coworkers provided to add for task ID: {}", taskId);
            return;
        }

        for (int userId : userIds) {
            try {
                logger.debug("Attempting to add coworker with ID: {} to task with ID: {}", userId, taskId);

                if (taskCoworkerRepository.exists(taskId, userId)) {
                    logger.info("Coworker with ID: {} is already assigned to task ID: {}", userId, taskId);
                    continue;
                }

                boolean result = taskCoworkerRepository.addCoworkerToTask(taskId, userId);

                if (!result) {
                    logger.warn("Failed to add coworker with ID: {} to task with ID: {}", userId, taskId);
                    throw new TaskCoworkerException("Failed to add coworker to task");
                }

                logger.info("Successfully added coworker with ID: {} to task with ID: {}", userId, taskId);

            } catch (DatabaseException e) {
                logger.error("Database error while adding coworker with ID: {} to task ID: {}", userId, taskId, e);
                throw new TaskCoworkerException("Failed to add coworker to task", e);
            }
        }
    }



    @Transactional
    public void removeCoworkersFromTask(int taskId, List<Integer> userIds) {
        logger.debug("Attempting to remove {} coworkers from task with ID: {}", userIds.size(), taskId);

        for (int userId : userIds) {
            try {
                boolean deleted = taskCoworkerRepository.removeCoworkerFromTask(taskId, userId);

                if (!deleted) {
                    logger.warn("Failed to delete coworker with ID: {} from task with ID: {}", userId, taskId);
                    throw new TaskCoworkerException("Failed to delete coworker from task");
                }

                logger.info("Successfully deleted coworker with ID: {} from task with ID: {}", userId, taskId);
            } catch (DatabaseException e) {
                logger.error("DB error while removing coworker ID {} from task ID {}", userId, taskId, e);
                throw new TaskCoworkerException("Failed to delete coworker from task", e);
            }
        }
    }

    public List<Integer> getAllCoworkersIdsFromTask(int taskId) {
        logger.debug("Retrieving all coworkers ID's from task with ID: {}", taskId);
        return taskCoworkerRepository.getAllCoworkersIdsForTask(taskId);
    }

    public List<User> getAllCoworkersForTask(int taskId) {
        logger.debug("Retrieving all coworkers from task with ID: {}", taskId);
        List<Integer> allTaskCoworkersIds = getAllCoworkersIdsFromTask(taskId);
        List<User> allTaskCoworkers = new ArrayList<>();

        for (Integer userId : allTaskCoworkersIds) {
            allTaskCoworkers.add(userService.getUserById(userId));
        }
        return allTaskCoworkers;
    }

    public boolean isEmployee(int userId, int taskId){
        logger.debug("Checking if user ID {} is assigned to task ID {}", userId, taskId);
        List<Integer> allCoworkers = getAllCoworkersIdsFromTask(taskId);
        for (Integer id : allCoworkers){
            if (id.equals(userId)){
                logger.debug("User ID {} is assigned to task ID {}", userId, taskId);
                return true;
            }
        }
        logger.debug("User ID {} is NOT assigned to task ID {}", userId, taskId);
        return false;
    }
}
