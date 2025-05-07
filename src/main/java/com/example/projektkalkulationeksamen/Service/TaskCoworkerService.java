package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.TaskCoworkerException;
import com.example.projektkalkulationeksamen.Repository.TaskCoworkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskCoworkerService {
    private static final Logger logger = LoggerFactory.getLogger(TaskCoworkerService.class);

    private final TaskCoworkerRepository taskCoworkerRepository;

    @Autowired
    public TaskCoworkerService(TaskCoworkerRepository taskCoworkerRepository) {
        this.taskCoworkerRepository = taskCoworkerRepository;
    }


    public void addCoworkerToTask (int taskId, int userId) {
        logger.debug("Attempting to add coworker with ID: {} to task with ID: {}", userId, taskId);

        try {
            if (taskCoworkerRepository.exists(taskId, userId)) {
                throw new TaskCoworkerException("Coworker is already assigned to task");
            }
            boolean result = taskCoworkerRepository.addCoworkerToTask(taskId, userId);

            if (!result) {
                logger.warn("Failed to add coworker with ID: {} to task with ID: {}", userId, taskId);
                throw new TaskCoworkerException("Failed to add coworker to task");
            }
            logger.info("Successfully added coworker with ID: {} to task with ID: {}", userId, taskId);
        } catch (DatabaseException e) {
            logger.error("Failed to add coworker with ID: {} to task with ID: {}", userId, taskId);
            throw new TaskCoworkerException("Failed to add coworker to task", e);
        }
    }


    public void removeCoworkerFromTask (int taskId, int userId) {
        logger.debug("Attempting to delete coworker with ID: {} from task with ID: {}", userId, taskId);

        try {
            boolean deleted = taskCoworkerRepository.removeCoworkerFromTask(taskId, userId);

            if (!deleted) {
                logger.warn("Failed to delete coworker with ID: {} from task with ID: {}", userId, taskId);
                throw new TaskCoworkerException("Failed to delete coworker from task");
            }
            logger.info("Succesfully deleted coworker with ID: {} from task with ID: {}", userId, taskId);
        } catch (DatabaseException e) {
            logger.error("Failed to delete coworker with ID: {} from task with ID: {}", userId, taskId, e);
            throw new TaskCoworkerException("Failed to delete coworker from task", e);
        }
    }

    public List<Integer> getAllCoworkersIdsForTask (int taskId) {
        return taskCoworkerRepository.getAllCoworkersIdsForTask(taskId);
    }

    public List<Integer> getAllTaskIdsForCoworker (int userId) {
        return taskCoworkerRepository.getAllTaskIdsForCoworker(userId);
    }
}
