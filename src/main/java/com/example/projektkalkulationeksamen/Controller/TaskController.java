package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.DTO.MilestoneDTO;
import com.example.projektkalkulationeksamen.DTO.ProjectDTO;
import com.example.projektkalkulationeksamen.Exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Service.*;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.projektkalkulationeksamen.Model.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {
private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;
    private final SessionValidator sessionValidator;
    private final UserService userService;
    private final ProjectService projectService;
    private final MilestoneService milestoneService;
    private final TaskCoworkerService taskCoworkerService;

    @Autowired
    public TaskController(TaskService taskService, SessionValidator sessionValidator, UserService userService, ProjectService projectService, MilestoneService milestoneService, TaskCoworkerService taskCoworkerService) {
        this.taskService = taskService;
        this.sessionValidator = sessionValidator;
        this.userService = userService;
        this.projectService = projectService;
        this.milestoneService = milestoneService;
        this.taskCoworkerService = taskCoworkerService;
    }

    @GetMapping("")
    public String showAllTasks (HttpSession session, Model model) {
        if (!sessionValidator.isSessionValid(session)) {
            return "redirect:/loginform";
        }
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task/tasklist";
    }



    @GetMapping("/update/{id}")
    public String showUpateForm (HttpSession session, @PathVariable int id, Model model) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can edit tasks");
        }
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "projectmanager/taskform";
    }

    @PostMapping("/update")
    public String updateTask (HttpSession session, @ModelAttribute Task task) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can update tasks");
        }

        taskService.updateTask(task);
        return "redirect:/tasks/view/" + task.getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteTask (HttpSession session, @PathVariable int id) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can update tasks");
        }

        Task task = taskService.getTaskById(id);
        int milestoneId = task.getMilestoneId();
        taskService.deleteTask(id);
        return "redirect:/milestones/view/" + milestoneId;
    }

     @GetMapping("/create/{milestoneId}")
    public String showCreateTaskForm(@PathVariable int milestoneId, HttpSession session, Model model) {
        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can create tasks");
        }

        Milestone milestone = milestoneService.getMilestoneById(milestoneId);
        Integer userId = (Integer) session.getAttribute("userId");
        ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());
        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && project.getProjectManagerId() == userId;

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
    public String createTask (
            HttpSession session,
            @ModelAttribute Task task,
            @RequestParam (required = false) List<Integer> userIds
    ) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can create tasks");
        }
        Task newTask = taskService.addTask(task);
        taskCoworkerService.addCoworkersToTask(newTask.getId(), userIds);
        return "redirect:/milestones/view/" + task.getMilestoneId();
    }

    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable int id, Model model, HttpSession session) {
        if (!sessionValidator.isSessionValid(session)) {
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Task task = taskService.getTaskById(id);


        MilestoneDTO milestone = milestoneService.getMilestoneWithDetails(task.getMilestoneId());
        ProjectDTO project = projectService.getProjectWithDetails(milestone.getProjectId());

        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && project.getProjectManagerId() == userId;

        model.addAttribute("task", task);
        model.addAttribute("isOwner", isOwner);

        return "taskpage";
    }
}
