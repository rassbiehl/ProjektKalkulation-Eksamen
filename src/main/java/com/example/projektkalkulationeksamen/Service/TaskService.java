package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.DTO.TaskDTO;
import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.TaskCreationException;
import com.example.projektkalkulationeksamen.Exceptions.TaskNotFoundException;
import com.example.projektkalkulationeksamen.Exceptions.UserNotFoundException;
import com.example.projektkalkulationeksamen.Model.Task;
import com.example.projektkalkulationeksamen.Repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final

    @Autowired
    public TaskService (TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        logger.debug("Sends list of all tasks");
        return taskRepository.getAllTasks();
    }

    public Task getTaskById(int id) {
        try {
            logger.debug("Sends task with ID: " + id);

            Optional<Task> foundTask = taskRepository.getTaskById(id);

            return foundTask.orElseThrow(() -> new TaskNotFoundException("Failed to find task with ID: "+ id));
        } catch (DatabaseException e) {
            throw new TaskNotFoundException("Failed to find task with ID: " + id);
        }
    }

    public Task getTaskByName (String taskName) {
        try {
            logger.debug("Sends task with name: " + taskName);
            Optional<Task> foundTask = taskRepository.getTaskByName(taskName);

            return foundTask.orElseThrow(() -> new TaskNotFoundException("Failed to find task by name: " + taskName));

        } catch (DatabaseException e) {
            logger.error("Failed to retrieve task from Database with name " + taskName, e);
            throw new TaskNotFoundException("Failed to find task with name: " + taskName);
        }
    }

    public Task addTask (Task task) {
        try {
            return taskRepository.addTask(task);
        } catch (DatabaseException e) {
            logger.error("Failed to add task to database with ID: {}", task.getId());
            throw new TaskCreationException("Failed to create Task", e);
        }
    }

    public void deleteTask(int id) {
        try {
            logger.debug("Attempting to delete task with id: " + id);

            boolean deleteTask = taskRepository.deleteTask(id);

            if (!deleteTask) {
                logger.warn("Failed to delete task with ID: " + id);
                throw new TaskNotFoundException("Failed to delete task with ID: " + id);
            }
            logger.info("Successfully deleted task with ID: " + id);
        } catch (TaskNotFoundException e) {
            throw new TaskNotFoundException("Failed to delete task with ID: "+ id, e);
        }
    }

    public void updateTask (Task task) {
        logger.debug("Attempting to update task with ID: {}", task.getId());

        getTaskById(task.getId());

        boolean updated = taskRepository.updateTask(task);

        if (!updated) {
            logger.warn("Failed to update task with ID: {}", task.getId());
            throw new TaskNotFoundException("Failed to update task with ID: " + task.getId());
        }

        logger.info("Successfully updated task with ID: {}", task.getId());
    }

    public boolean taskExistsById (int id) {
        return taskRepository.getTaskById(id).isPresent();
    }

    public boolean taskExistsByName (String name) {
        return taskRepository.getTaskByName(name).isPresent();
    }

    // DTO Object methods
/*
TaskDTO getTaskWithDetails(int id)	Task + coworkers

List<TaskDTO> getTasksByMilestoneIdWithDetails(int milestoneId)
 */
    public TaskDTO getTaskWithDetails (int id) {
        Task task =
    }
    }



