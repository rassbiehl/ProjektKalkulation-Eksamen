package com.example.projektkalkulationeksamen.controller;

import com.example.projektkalkulationeksamen.dto.MilestoneDTO;
import com.example.projektkalkulationeksamen.dto.ProjectDTO;
import com.example.projektkalkulationeksamen.exceptions.project.ProjectUpdateException;
import com.example.projektkalkulationeksamen.exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.exceptions.project.ProjectCreationException;
import com.example.projektkalkulationeksamen.model.Project;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.Status;
import com.example.projektkalkulationeksamen.service.ProjectService;
import com.example.projektkalkulationeksamen.service.UserService;
import com.example.projektkalkulationeksamen.validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/projects")
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
    public String getStartPage(
            @PathVariable String role,
            HttpSession session,
            Model model
    ) {

        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");

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


    @GetMapping("/view/{id}")
    public String getProjectPage(
            @PathVariable int id,
            HttpSession session,
            Model model
    ) {
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
        model.addAttribute("estimatedHours",projectService.estimatedHours(id));
        model.addAttribute("actualHoursUsed", projectService.actualHoursUsed(id));

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

    @GetMapping("/add")
    public String getAddProjectPage(
            HttpSession session,
            Model model
    ) {
        logger.debug("Attempting to load add project page");
        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        model.addAttribute("project", new Project());

        Integer userId = (Integer) session.getAttribute("userId");

        Role role = userService.getUserById(userId).getRole();

        model.addAttribute("userRole", role.toString().toLowerCase());

        logger.info("Loading add project page");
        return "projectmanager/addProject";
    }

    @PostMapping("/save")
    public String saveProject(
            HttpSession session,
            @ModelAttribute Project project,
            RedirectAttributes redirectAttributes
    ) {
        logger.debug("Attempting to add new project");

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        project.setProjectManagerId(userId);

        try {
            projectService.addProject(project);
            logger.info("Successfully added project with name: {}", project.getProjectName());
            Role role = userService.getUserById(userId).getRole();
            return "redirect:/projects/" + role.toString().toLowerCase() + "Startpage";

        } catch (ProjectCreationException e) {
            logger.warn("Project creation failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/projects/add";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(
            HttpSession session,
            @PathVariable int id
    ) {
        logger.debug("Attempting to delete project with ID: {}", id);
        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can delete projects");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        ProjectDTO project = projectService.getProjectWithDetails(id);
        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && project.getProjectManagerId() == userId;

        if (isOwner) {
            projectService.deleteProject(id);
            logger.info("Successfully deleted project with ID: {}", id);
        } else {
            logger.warn("Failed to delete project with ID: {} because of missing owner id in session", id);
            throw new AccessDeniedException("Access denied: User does not own the project");
        }

        return "redirect:/projects/" + role.toString().toLowerCase() + "Startpage";
    }

    @GetMapping("/update/{id}")
    public String getUpdateProjectPage(
            HttpSession session,
            @PathVariable int id,
            Model model
    ) {
        logger.debug("Attempting to get update page for project with ID: {}", id);
        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can update projects");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        ProjectDTO project = projectService.getProjectWithDetails(id);
        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && project.getProjectManagerId() == userId;

        if (isOwner) {

            model.addAttribute("projectId", id);
            model.addAttribute("project", project);
            model.addAttribute("status", Status.values());

            return "projectmanager/updateProject";
        } else {
            logger.warn("Failed to get update page for project with ID: {} because of missing owner id in session", id);
            throw new AccessDeniedException("Access denied: User does not own the project");
        }
    }

    @PostMapping("/update")
    public String updateProject(
            HttpSession session,
            @RequestParam int id,
            @RequestParam String projectName,
            @RequestParam String description,
            @RequestParam LocalDateTime deadline,
            @RequestParam LocalDateTime startDate,
            @RequestParam Status status,
            RedirectAttributes redirectAttributes
    ) {
        logger.debug("Trying to update project with ID: {}", id);

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("User is not a project manager. Access denied.");
            throw new AccessDeniedException("Only project managers can update projects");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        ProjectDTO project = projectService.getProjectWithDetails(id);
        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && project.getProjectManagerId() == userId;

        if (isOwner) {
            Project projectToUpdate = projectService.getProjectById(id);

            if (projectName != null) projectToUpdate.setProjectName(projectName);
            if (description != null) projectToUpdate.setDescription(description);
            if (deadline != null) projectToUpdate.setDeadline(deadline);
            if (startDate != null) projectToUpdate.setStartDate(startDate);
            if (status != null) projectToUpdate.setStatus(status);

            try {
                projectService.updateProject(projectToUpdate);
                logger.info("Project with ID {} was successfully updated by user {}", id, userId);
            } catch (ProjectUpdateException e) {
                logger.error("Could not update project with ID {}. Reason: {}", id, e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/projects/update/" + id;
            }
        } else {
            logger.warn("User with ID {} is not the owner of project ID {}. Access denied.", userId, id);
            throw new AccessDeniedException("Access denied: User does not own the project");
        }

        return "redirect:/projects/view/" + id;
    }


}

