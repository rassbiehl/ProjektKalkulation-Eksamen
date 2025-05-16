package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.DTO.MilestoneDTO;
import com.example.projektkalkulationeksamen.DTO.ProjectDTO;
import com.example.projektkalkulationeksamen.DTO.TaskDTO;
import com.example.projektkalkulationeksamen.Exceptions.milestone.MilestoneUpdateException;
import com.example.projektkalkulationeksamen.Exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.Exceptions.milestone.MilestoneCreationException;
import com.example.projektkalkulationeksamen.Exceptions.notfound.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Service.MilestoneService;
import com.example.projektkalkulationeksamen.Service.ProjectService;
import com.example.projektkalkulationeksamen.Service.UserService;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/milestones")
@Controller
public class MilestoneController {
    private final static Logger logger = LoggerFactory.getLogger(MilestoneController.class);
    private final MilestoneService milestoneService;
    private final ProjectService projectService;
    private final SessionValidator sessionValidator;
    private final UserService userService;


    @Autowired
    public MilestoneController(MilestoneService milestoneService, ProjectService projectService, SessionValidator sessionValidator, UserService userService) {
        this.milestoneService = milestoneService;
        this.projectService = projectService;
        this.sessionValidator = sessionValidator;
        this.userService = userService;
    }


    @GetMapping("/view/{id}")
    public String getMilestonePage(
            @PathVariable int id,
            HttpSession session,
            Model model
    ) {
        logger.info("Attempting to load milestone page with milestone ID: {}", id);

        if (!sessionValidator.isSessionValid(session)) {
            logger.warn("Invalid session. Redirecting to login.");
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");

        MilestoneDTO milestoneDTO = milestoneService.getMilestoneWithDetails(id);

        ProjectDTO projectDTO = projectService.getProjectWithDetails(milestoneDTO.getProjectId());

        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && projectDTO.getProjectManagerId() == userId;
        logger.debug("User ID {} has role {}. Is owner: {}", userId, role, isOwner);

        model.addAttribute("project", projectDTO);
        model.addAttribute("milestone", milestoneDTO);
        model.addAttribute("projectManager", userService.getUserById(projectDTO.getProjectManagerId()));
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("milestoneTasks", milestoneDTO.getTasks());

        List<TaskDTO> ongoingTasks = milestoneService.getOngoingTasksFromMilestone(id);

        if (!ongoingTasks.isEmpty()) {
            logger.debug("Found {} ongoing tasks.", ongoingTasks.size());
            model.addAttribute("ongoingTasks", ongoingTasks);
        }

        List<TaskDTO> completedTasks = milestoneService.getCompletedTasksFromMilestone(id);

        if (!completedTasks.isEmpty()) {
            logger.debug("Found {} completed tasks.", ongoingTasks.size());
            model.addAttribute("completedTasks", completedTasks);
        }

        logger.info("Returning milestone page");
        return "milestonePage";
    }

    @GetMapping("/add/{projectId}")
    public String showAddForm(
            HttpSession session,
            @PathVariable int projectId,
            Model model
    ) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = userService.getUserById(userId).getRole();
        ProjectDTO projectDTO = projectService.getProjectWithDetails(projectId);

        boolean isOwner = role == Role.PROJECTMANAGER && projectDTO.getProjectManagerId() == userId;

        if (isOwner) {
            model.addAttribute("userRole", role.toString().toLowerCase());

            model.addAttribute("milestone", new Milestone());
            model.addAttribute("projectId", projectId);
            model.addAttribute("status", Status.values());

            return "/projectmanager/addMilestone";
        } else {
            logger.warn("Failed retrieve add milestone form because of missing owner ID: {}", userId);
            throw new AccessDeniedException("Access denied: User with ID: {} " + projectId + " does not own the project");
        }
    }

    @PostMapping("/save")
    public String addMilestone(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @ModelAttribute Milestone milestone,
            @RequestParam int projectId
    ) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = userService.getUserById(userId).getRole();
        ProjectDTO projectDTO = projectService.getProjectWithDetails(projectId);

        boolean isOwner = role == Role.PROJECTMANAGER && projectDTO.getProjectManagerId() == userId;

        if (isOwner) {
            milestone.setProjectId(projectId);

            try {
                milestoneService.addMilestone(milestone);
                logger.info("Successfully created new milestone with name: {}", milestone.getMilestoneName());
                return "redirect:/projects/view/" + projectId;
            } catch (MilestoneCreationException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/milestones/add/" + projectId;
            }

        } else {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteMilestone(
            HttpSession session,
            @PathVariable int id
    ) {
        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can delete milestones");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        MilestoneDTO milestoneDTO = milestoneService.getMilestoneWithDetails(id);
        ProjectDTO projectDTO = projectService.getProjectWithDetails(milestoneDTO.getProjectId());
        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && projectDTO.getProjectManagerId() == userId;

        if (isOwner) {
            milestoneService.deleteMilestone(id);
            logger.info("Succesfully deleted milesetone with ID: {}", id);
        } else {
            logger.warn("Failed to delete milestone with ID: {} because of missing owner id in session", id);
            throw new AccessDeniedException("Access denied: User does not own the project");
        }

        return "redirect:/projects/view/" + projectDTO.getId();

    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(
            Model model,
            @PathVariable int id,
            HttpSession session
    ) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        try {
            Milestone milestone = milestoneService.getMilestoneById(id);
            model.addAttribute("milestone", milestone);
            model.addAttribute("status", Status.values());
        } catch (MilestoneNotFoundException e) {
            return "redirect:/loginform";
        }

        return "projectmanager/updateMilestone";
    }

    @PostMapping("/update")
    public String updateMileStone(
            HttpSession session,
            @ModelAttribute Milestone milestone,
            RedirectAttributes redirectAttributes) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can update projects");
        }

        Integer userId = (Integer) session.getAttribute("userId");
        MilestoneDTO milestoneDTO = milestoneService.getMilestoneWithDetails(milestone.getId());
        ProjectDTO projectDTO = projectService.getProjectWithDetails(milestoneDTO.getProjectId());

        Role role = userService.getUserById(userId).getRole();
        boolean isOwner = role == Role.PROJECTMANAGER && projectDTO.getProjectManagerId() == userId;

        if (!isOwner) {
            logger.warn("User with ID {} is not the owner of project ID {}. Access denied.", userId, milestone.getId());
            throw new AccessDeniedException("Access denied: User does not own the project");
        }

        milestone.setProjectId(milestoneDTO.getProjectId());

        try {
            milestoneService.updateMilestone(milestone);
            logger.info("Milestone with ID {} was successfully updated by user {}", milestone.getId(), userId);
        } catch (MilestoneUpdateException e) {
            logger.error("Could not update milestone with ID {}. Reason: {}", milestone.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/milestones/update/" + milestone.getId();
        }

        return "redirect:/milestones/view/" + milestone.getId();
    }

}
