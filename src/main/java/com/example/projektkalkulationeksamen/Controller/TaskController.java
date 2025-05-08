package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.AccessDeniedException;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Service.TaskService;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.projektkalkulationeksamen.Model.Task;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class TaskController {

    private final TaskService taskService;
    private final SessionValidator sessionValidator;

    @Autowired
    public TaskController (TaskService taskService, SessionValidator sessionValidator) {
        this.taskService = taskService;
        this.sessionValidator = sessionValidator;
    }

    @GetMapping
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
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editTask (HttpSession session, @PathVariable int id, Model model) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can edit tasks");
        }
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "task/taskform";
    }

    @PostMapping("/update")
    public String updateTask (HttpSession session, @ModelAttribute Task task) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can update tasks");
        }

        taskService.updateTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete({id}")
    public String deleteTask (HttpSession session, @PathVariable int id ) {
        if(!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            throw new AccessDeniedException("Only project managers can update tasks");
        }
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
