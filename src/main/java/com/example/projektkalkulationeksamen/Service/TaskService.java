package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.DTO.TaskDTO;
import com.example.projektkalkulationeksamen.Exceptions.database.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.database.DeletionException;
import com.example.projektkalkulationeksamen.Exceptions.task.TaskCreationException;
import com.example.projektkalkulationeksamen.Exceptions.notfound.TaskNotFoundException;
import com.example.projektkalkulationeksamen.Exceptions.task.TaskUpdateException;
import com.example.projektkalkulationeksamen.Model.Task;
import com.example.projektkalkulationeksamen.Repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskCoworkerService taskCoworkerService;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskCoworkerService taskCoworkerService) {
        this.taskRepository = taskRepository;
        this.taskCoworkerService = taskCoworkerService;
    }

    public List<Task> getAllTasks() {
        logger.debug("Sends list of all tasks");
        return taskRepository.getAllTasks();
    }

    public Task getTaskById(int id) {
            logger.debug("Sends task with ID: " + id);
            Optional<Task> foundTask = taskRepository.getTaskById(id);

            return foundTask.orElseThrow(() -> {
                logger.warn("Task not found with ID: {}", id);
                    return new TaskNotFoundException("Failed to find task with ID: " + id);
            });
    }

    public Task addTask(Task task) {
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
            throw new DeletionException("Failed to delete task with ID: " + id, e);
        }
    }

    public void updateTask(Task task) {
        logger.debug("Attempting to update task with ID: {}", task.getId());

        getTaskById(task.getId());

        boolean updated = taskRepository.updateTask(task);

        if (!updated) {
            logger.warn("Failed to update task with ID: {}", task.getId());
            throw new TaskUpdateException("Failed to update task with ID: " + task.getId());
        }

        logger.info("Successfully updated task with ID: {}", task.getId());
    }

    public boolean taskExistsById(int id) {
        return taskRepository.getTaskById(id).isPresent();
    }

    public boolean taskExistsByName(String name) {
        return taskRepository.getTaskByName(name).isPresent();
    }

    // DTO Object methods
    public TaskDTO getTaskWithDetails(int id) {
        Task task = getTaskById(id);

        List<Integer> coworkerIds = taskCoworkerService.getAllCoworkersIdsForTask(id);

        return new TaskDTO(
                task.getId(),
                task.getTaskName(),
                task.getTaskDescription(),
                task.getMilestoneId(),
                task.getEstimatedHours(),
                task.getActualHoursUsed(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getStartedDate(),
                task.getDeadline(),
                task.getCompletedAt(),
                coworkerIds
        );
    }

    public List<TaskDTO> getAllTasksWithDetails () {
        List<Task> allTasks = getAllTasks();

        List<TaskDTO> allTasksWithDetails = new ArrayList<>();

        for (Task task : allTasks) {
            allTasksWithDetails.add(getTaskWithDetails(task.getId()));
        }
        return allTasksWithDetails;
    }

    public List<TaskDTO> getTasksByMilestoneIdWithDetails(int milestoneId) {
        List<Task> tasksByMileStoneId = taskRepository.getTasksByMilestoneId(milestoneId);
        List<TaskDTO> tasksByMileStoneIdWithDetails = new ArrayList<>();


        for (Task task : tasksByMileStoneId) {
            tasksByMileStoneIdWithDetails.add(getTaskWithDetails(task.getId()));
        }

        return tasksByMileStoneIdWithDetails;
    }

    public List<TaskDTO> getAllTasksForCoworker(int userId) {

        List<TaskDTO> allTasksWithDetails = getAllTasksWithDetails();

        List<TaskDTO> selectedTasks = new ArrayList<>();

        for (TaskDTO taskDTO : allTasksWithDetails) {
            List<Integer> taskCoworkersIds = taskDTO.getCoworkerIds();

            for (Integer id : taskCoworkersIds) {
                if (id == userId) {
                    selectedTasks.add(taskDTO);
                }
            }
        }

        return selectedTasks;
     }

}



