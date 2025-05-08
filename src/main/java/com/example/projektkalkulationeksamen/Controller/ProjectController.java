package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.AccessDeniedException;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Service.ProjectService;
import com.example.projektkalkulationeksamen.Service.UserService;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProjectController {
    public static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final UserService userService;
    private final SessionValidator sessionValidator;
    private final ProjectService projectService;

    @Autowired
    public ProjectController(UserService userService, SessionValidator sessionValidator, ProjectService projectService) {
        this.userService = userService;
        this.sessionValidator = sessionValidator;
        this.projectService = projectService;
    }

    @GetMapping("/{role}Startpage")
    public String getStartPage(@PathVariable String role, HttpSession session, Model model) {

        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        Role requiredRole;
        try {
            requiredRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role '{}' in path", role);
            throw new AccessDeniedException("Invalid role");
        }

        if (!sessionValidator.isSessionValid(session, requiredRole)) {
            Integer userId = (Integer) session.getAttribute("userId");
            logger.info("Access denied: user with ID {} lacks {} privileges", userId, requiredRole);
            throw new AccessDeniedException("User lacks " + requiredRole + " privileges");
        }
        model.addAttribute("projectId",1);

        model.addAttribute("allProjects", projectService.getAllProjectsWithDetails());

        return role.toLowerCase() + "/startpage";
    }
}