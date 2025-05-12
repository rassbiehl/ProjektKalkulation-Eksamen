package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.DTO.MilestoneDTO;
import com.example.projektkalkulationeksamen.DTO.ProjectDTO;
import com.example.projektkalkulationeksamen.Exceptions.AccessDeniedException;
import com.example.projektkalkulationeksamen.Model.Project;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Service.ProjectService;
import com.example.projektkalkulationeksamen.Service.UserService;
import com.example.projektkalkulationeksamen.Validator.ProjectDataValidator;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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

        Integer userId = (Integer) session.getAttribute("userId");

        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        Role requiredRole;
        try {
            requiredRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role: {} in path", role);
            throw new AccessDeniedException("Invalid role");
        }

        if (!sessionValidator.isSessionValid(session, requiredRole)) {
            logger.info("Access denied: user with ID {} lacks {} privileges", userId, requiredRole);
            throw new AccessDeniedException("User lacks " + requiredRole + " privileges");
        }

        List<ProjectDTO> projectsByManager = projectService.getAllProjectDTOsByProjectManagerId(userId);

        if (!projectsByManager.isEmpty()) {
            model.addAttribute("myProjects", projectsByManager);
        }


        List<ProjectDTO> projectsByEmployee = projectService.getAllProjectsByEmployeeId(userId);

        if (!projectsByEmployee.isEmpty()) {
            model.addAttribute("assignedProjects", projectsByEmployee);
        }

        List<ProjectDTO> ongoingProjects = projectService.getAllOngoingProjects();

        if (!ongoingProjects.isEmpty()) {
            model.addAttribute("ongoingProjects", ongoingProjects);
        }

        List<ProjectDTO> finishedProjects = projectService.getAllFinishedProjectsWithDetails();

        if (!finishedProjects.isEmpty()) {
            model.addAttribute("finishedProjects", finishedProjects);
        }
        model.addAttribute("allProjects", projectService.getAllProjectsWithDetails());

        return role.toLowerCase() + "/startpage";
    }


    @GetMapping("/project/{id}")
    public String getProjectPage(@PathVariable int id, HttpSession session, Model model) {

        logger.info("Attempting to load project page with project ID: {}", id);

        if (!sessionValidator.isSessionValid(session)) {
            logger.warn("Invalid session. Redirecting to login.");
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");

        ProjectDTO project = projectService.getProjectWithDetails(id);


        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && project.getProjectManagerId() == userId;
        logger.debug("User ID {} has role {}. Is owner: {}", userId, role, isOwner);

        model.addAttribute("project", project);
        model.addAttribute("userRole", role.toString().toLowerCase());
        model.addAttribute("projectManager", userService.getUserById(project.getProjectManagerId()));
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("projectMilestones", project.getMilestones());

        List<MilestoneDTO> ongoing = projectService.getOngoingMileStonesFromProject(id);
        if (!ongoing.isEmpty()) {
            logger.debug("Found {} ongoing milestones.", ongoing.size());
            model.addAttribute("ongoingMilestones", ongoing);
        }

        List<MilestoneDTO> finished = projectService.getFinishedMileStonesFromProject(id);
        if (!finished.isEmpty()) {
            logger.debug("Found {} finished milestones.", finished.size());
            model.addAttribute("finishedMilestones", finished);
        }

        logger.info("Returning projectpage.html");
        return "projectpage";
    }

    @GetMapping("/addproject")
    public String getAddProjectPage (HttpSession session, Model model) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        model.addAttribute("project", new Project());


        return "projectmanager/addProject";
    }

    @PostMapping("/saveproject")
    public String saveProject (HttpSession session, @ModelAttribute Project project) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }
        Integer userId = (Integer) session.getAttribute("userId");
        project.setProjectManagerId(userId);

        projectService.addProject(project);


        return "redirect:/projectmanagerStartpage";
    }

}