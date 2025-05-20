package com.example.projektkalkulationeksamen.controller;

import com.example.projektkalkulationeksamen.dto.MilestoneDTO;
import com.example.projektkalkulationeksamen.dto.ProjectDTO;
import com.example.projektkalkulationeksamen.exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.exceptions.task.TaskCreationException;
import com.example.projektkalkulationeksamen.exceptions.task.TaskUpdateException;
import com.example.projektkalkulationeksamen.model.*;
import com.example.projektkalkulationeksamen.service.*;
import com.example.projektkalkulationeksamen.validation.AccessValidation;
import com.example.projektkalkulationeksamen.validation.SessionValidation;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;
    private final SessionValidation sessionValidation;
    private final UserService userService;
    private final ProjectService projectService;
    private final MilestoneService milestoneService;
    private final TaskCoworkerService taskCoworkerService;

    @Autowired
    public TaskController(TaskService taskService, SessionValidation sessionValidation, UserService userService, ProjectService projectService, MilestoneService milestoneService, TaskCoworkerService taskCoworkerService) {
        this.taskService = taskService;
        this.sessionValidation = sessionValidation;
        this.userService = userService;
        this.projectService = projectService;
        this.milestoneService = milestoneService;
        this.taskCoworkerService = taskCoworkerService;
    }

    @GetMapping("")
    public String showAllTasks(
            HttpSession session,
            Model model
    ) {
        if (!sessionValidation.isSessionValid(session)) {
            return "redirect:/loginform";
        }
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task/tasklist";
    }


    @GetMapping("/update/{id}")
    public String showUpdateForm(
            HttpSession session,
            @PathVariable int id,
            Model model
    ) {
        if (!sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can edit tasks");
        }

        Task task = taskService.getTaskById(id);
        MilestoneDTO milestone = milestoneService.getMilestoneWithDetails(task.getMilestoneId());
        ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());
        Integer userId = (Integer) session.getAttribute("userId");
        Role userRole = userService.getUserById(userId).getRole();

        boolean isOwner = AccessValidation.isOwner(userId, userRole, project);
        if (!isOwner) {
            throw new AccessDeniedException("You must be the project owner to edit tasks.");
        }

        model.addAttribute("task", task);
        model.addAttribute("status", Status.values());
        model.addAttribute("employees", userService.getAllEmployees());
        model.addAttribute("assignedEmployees", taskCoworkerService.getAllCoworkersIdsFromTask(id));

        return "projectmanager/updateTask";
    }


    @PostMapping("/update")
    public String updateTask(
            HttpSession session,
            @ModelAttribute Task task,
            @RequestParam(required = false) List<Integer> userIds,
            RedirectAttributes redirectAttributes

    ) {
        if (!sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can update tasks");
        }

        Task currentTask = taskService.getTaskById(task.getId());
        MilestoneDTO milestone = milestoneService.getMilestoneWithDetails(task.getMilestoneId());
        ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());

        Integer userId = (Integer) session.getAttribute("userId");
        Role userRole = userService.getUserById(userId).getRole();

        boolean isOwner = AccessValidation.isOwner(userId, userRole, project);
        if (!isOwner) {
            throw new AccessDeniedException("You must be the project owner to update tasks.");
        }
        task.setId(currentTask.getId());

        try {
            taskService.updateTask(task);
            taskCoworkerService.removeCoworkersFromTask(task.getId(), taskCoworkerService.getAllCoworkersIdsFromTask(task.getId()));
            taskCoworkerService.addCoworkersToTask(task.getId(), userIds);

            logger.info("Task with ID: {} was successfully updated by user {}", task.getId(), userId);
        } catch (TaskUpdateException e) {
            logger.error("Could not update task with ID: {}. Reason: {}", task.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tasks/update/" + task.getId();
        }

        return "redirect:/tasks/view/" + task.getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(
            HttpSession session,
            @PathVariable int id
    ) {
        if (!sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can delete tasks");
        }

        Task task = taskService.getTaskById(id);
        MilestoneDTO milestone = milestoneService.getMilestoneWithDetails(task.getMilestoneId());
        ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());

        Integer userId = (Integer) session.getAttribute("userId");
        Role userRole = userService.getUserById(userId).getRole();

        boolean isOwner = AccessValidation.isOwner(userId, userRole, project);
        if (!isOwner) {
            throw new AccessDeniedException("You must be the project owner to delete tasks.");
        }

        int milestoneId = task.getMilestoneId();
        taskService.deleteTask(id);
        logger.info("Successfully deleted task with ID: {} Redirecting to milestone page", id);

        return "redirect:/milestones/view/" + milestoneId;
    }

    @GetMapping("/create/{milestoneId}")
    public String showCreateTaskForm(
            @PathVariable int milestoneId,
            HttpSession session,
            Model model
    ) {
        if (!sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can create tasks");
        }

        Milestone milestone = milestoneService.getMilestoneById(milestoneId);
        Integer userId = (Integer) session.getAttribute("userId");
        ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());
        Role userRole = userService.getUserById(userId).getRole();
        boolean isOwner = AccessValidation.isOwner(userId, userRole, project);

        if (isOwner) {
            Task task = new Task();
            task.setMilestoneId(milestoneId);
            model.addAttribute("task", task);
            model.addAttribute("milestoneId", milestoneId);
            model.addAttribute("employees", userService.getAllEmployees());
            logger.info("Returning add task form");
            return "projectmanager/addTask";
        } else {
            logger.warn("Failed to get add Task page because of missing owner id in session");
            throw new AccessDeniedException("Access denied: User does not own the project");
        }
    }

    @PostMapping("/create")
    public String createTask(
            HttpSession session,
            @ModelAttribute Task task,
            @RequestParam(required = false) List<Integer> userIds,
            RedirectAttributes redirectAttributes
    ) {
        if (!sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can create tasks");
        }

        int milestoneId = task.getMilestoneId();

        try {
            Milestone milestone = milestoneService.getMilestoneById(milestoneId);
            Integer userId = (Integer) session.getAttribute("userId");
            ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());
            Role userRole = userService.getUserById(userId).getRole();
            boolean isOwner = AccessValidation.isOwner(userId, userRole, project);

            if (!isOwner) {
                throw new AccessDeniedException("Access denied: User does not own the project");
            }

            Task newTask = taskService.addTask(task);
            taskCoworkerService.addCoworkersToTask(newTask.getId(), userIds);

            return "redirect:/milestones/view/" + milestoneId;

        } catch (TaskCreationException e) {
            logger.warn("Task creation failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tasks/create/" + milestoneId;
        }
    }


    @GetMapping("/view/{id}")
    public String viewTask(
            @PathVariable int id,
            Model model,
            HttpSession session
    ) {
        if (!sessionValidation.isSessionValid(session)) {
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Task task = taskService.getTaskById(id);


        MilestoneDTO milestone = milestoneService.getMilestoneWithDetails(task.getMilestoneId());
        ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());

        Role userRole = userService.getUserById(userId).getRole();
        boolean isOwner = AccessValidation.isOwner(userId, userRole, project);
        boolean isEmployee = userRole == Role.EMPLOYEE && taskCoworkerService.isEmployee(userId, task.getId());


        model.addAttribute("task", task);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("employees", taskCoworkerService.getAllCoworkersForTask(id));
        model.addAttribute("isEmployee", isEmployee);

        return "taskpage";
    }

    @GetMapping("/setHours/{id}")
    public String showSetHours(
            HttpSession session,
            @PathVariable int id,
            Model model
    ) {
        if (!sessionValidation.isSessionValid(session)) {
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Task task = taskService.getTaskById(id);
        Role userRole = userService.getUserById(userId).getRole();
        boolean isEmployee = userRole == Role.EMPLOYEE && taskCoworkerService.isEmployee(userId, task.getId());

        if (isEmployee) {
            model.addAttribute("task", task);
            model.addAttribute("taskId", id);
            model.addAttribute("HoursInput", 0);
            logger.info("User with ID: {} successfully added hours to task with ID: {}", userId, id);
            return "setHours";
        } else {
            logger.warn("Failed to get add-hours page because of missing employee ID: {}", userId);
            throw new AccessDeniedException("Access denied: User with ID: " + userId + " is not a part of the project");
        }
    }

    @PostMapping("/set-hours")
    public String submitHours(
            HttpSession session,
            @RequestParam int taskId,
            @RequestParam int hours
    ) {

        if (!sessionValidation.isSessionValid(session)) {
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Task task = taskService.getTaskById(taskId);
        Role userRole = userService.getUserById(userId).getRole();
        boolean isEmployee = userRole == Role.EMPLOYEE && taskCoworkerService.isEmployee(userId, task.getId());

        if (isEmployee) {
        int updatedHours = task.getActualHoursUsed() + hours;
        task.setActualHoursUsed(updatedHours);

        int milestoneId = task.getMilestoneId();

        taskService.setHours(updatedHours, taskId);

        return "redirect:/tasks/view/" + taskId;
        } else {
            logger.warn("Access denied: User ID {} attempted to add hours to task ID {} but is not assigned", userId, taskId);
            throw new AccessDeniedException("Access denied: User with ID: " + userId + " is not a part of the project");
        }
    }
}
