package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.DTO.MilestoneDTO;
import com.example.projektkalkulationeksamen.DTO.ProjectDTO;
import com.example.projektkalkulationeksamen.Exceptions.AccessDeniedException;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Service.MilestoneService;
import com.example.projektkalkulationeksamen.Service.ProjectService;
import com.example.projektkalkulationeksamen.Service.TaskService;
import com.example.projektkalkulationeksamen.Service.UserService;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.projektkalkulationeksamen.Model.Task;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final SessionValidator sessionValidator;
    private final UserService userService;
    private final ProjectService projectService;
    private final MilestoneService milestoneService;

    @Autowired
    public TaskController (TaskService taskService, SessionValidator sessionValidator, ProjectService projectService, UserService userService, MilestoneService milestoneService) {
        this.taskService = taskService;
        this.sessionValidator = sessionValidator;
        this.projectService = projectService;
        this.userService = userService;
        this.milestoneService = milestoneService;
    }

    @GetMapping("")
    public String showAllTasks (HttpSession session, Model model) {
        if (!sessionValidator.isSessionValid(session)) {
            return "redirect:/loginform";
        }
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task/tasklist";
    }

    @GetMapping("/create")
    public String showCreateForm (HttpSession session, Model model) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can create tasks");
        }
        model.addAttribute("task", new Task());
        return "task/taskform";

    }

    @PostMapping("/create")
    public String createTask (HttpSession session, @ModelAttribute Task task){
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can create tasks");
        }
        taskService.addTask(task);
        return "redirect:/milestones/view/" + task.getMilestoneId();
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

    @GetMapping("/create/milestone/{milestoneId}")
    public String showCreateTaskForMilestone(@PathVariable int milestoneId, HttpSession session, Model model) {
        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can create tasks");
        }
        Task task = new Task();
        task.setMilestoneId(milestoneId);

        model.addAttribute("task", task);
        return "projectmanager/taskform";
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
