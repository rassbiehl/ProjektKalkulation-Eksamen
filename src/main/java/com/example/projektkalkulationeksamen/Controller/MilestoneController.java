package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.AccessDeniedException;
import com.example.projektkalkulationeksamen.Exceptions.MilestoneCreationException;
import com.example.projektkalkulationeksamen.Exceptions.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Service.MilestoneService;
import com.example.projektkalkulationeksamen.Service.ProjectService;
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

@Controller
public class MilestoneController {
    private final static Logger logger = LoggerFactory.getLogger(MilestoneController.class);
    private final MilestoneService milestoneService;
    private final ProjectService projectService;
    private final SessionValidator sessionValidator;

    @Autowired
    public MilestoneController(MilestoneService milestoneService, ProjectService projectService, SessionValidator sessionValidator) {
        this.milestoneService = milestoneService;
        this.projectService = projectService;
        this.sessionValidator = sessionValidator;
    }

    /*
    @GetMapping("/milestones/{project_id}")
    public String getAllMilestonesByProjectId(@PathVariable int project_id, HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }

        List<Milestone> milestones = milestoneService.getMilestonesByProjectId(project_id);
        model.addAttribute("milestones", milestones);
        model.addAttribute("project", projectService.getProjectById(project_id));

        return "projectmanager/milestones";
    } */

    @GetMapping("addMilestone/{projectId}")
    public String showAddForm(HttpSession session, @PathVariable int projectId, Model model) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }
        model.addAttribute("milestone", new Milestone());
        model.addAttribute("projectId", projectId);
        model.addAttribute("status", Status.values());

        return "/projectmanager/addMilestone";
    }

    @PostMapping("/savemilestone")
    public String addMilestone(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute Milestone milestone, @RequestParam int projectId) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        milestone.setProjectId(projectId);

        try {
            milestoneService.addMilestone(milestone);
        } catch (MilestoneCreationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/addMilestone/" + projectId;
        }

        return "redirect:/milestones/" + projectId;
    }

    @PostMapping("/delete/{id}")
    public String deleteMilestone(@PathVariable int id, @RequestParam int projectId) {

        milestoneService.deleteMilestone(id);


        return "redirect:/milestones/" + projectId;

    }

    @GetMapping("/updateMilestoneForm/{milestoneId}")
    public String showUpdateForm(Model model, @PathVariable int milestoneId, HttpSession session) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }

        try {
            Milestone milestone = milestoneService.getMilestoneById(milestoneId);
            model.addAttribute("milestone", milestone);
            model.addAttribute("status", Status.values());
        } catch (MilestoneNotFoundException e) {
            return "redirect:/loginform";
        }

        return "projectmanager/updateMilestoneForm";
    }

    @PostMapping("/milestone/update")
    public String updateMileStone(HttpSession session, @RequestParam int milestoneId, @ModelAttribute Milestone milestone) {

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            logger.warn("Access denied: User is not a project manager");
            throw new AccessDeniedException("Only project managers can add projects");
        }
        milestone.setId(milestoneId);
        milestoneService.updateMilestone(milestone);

        int projectId = milestoneService.getMilestoneById(milestoneId).getProjectId();


        return "redirect:/milestones/" + projectId;
    }

}
