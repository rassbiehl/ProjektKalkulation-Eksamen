package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Service.MilestoneService;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @GetMapping("/milestones/{project_id}")
    public String getAllMilestonesByProjectId(@PathVariable int project_id, HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = (Role) session.getAttribute("user_role");
        if (userId == null || role == null || !role.equals(Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }

        List<Milestone> milestones = milestoneService.getMilestonesByProjectId(project_id);

        model.addAttribute("milestones", milestones);
        return "projectmanager/milestones";
    }

    @GetMapping("addMilestone/{projectId}")
    public String showAddForm(HttpSession session, @PathVariable int projectId, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = (Role) session.getAttribute("user_role");
        if (userId == null || role == null || !role.equals(Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }
        model.addAttribute("milestone", new Milestone());
        model.addAttribute("projectId", projectId);
        model.addAttribute("status", Status.values());

        return "/projectmanager/addMilestone";
    }

    @PostMapping("/save")
    public String addMilestone(HttpSession session, @ModelAttribute Milestone milestone, @RequestParam int projectId) {

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = (Role) session.getAttribute("user_role");
        if (userId == null || role == null || !role.equals(Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }

        milestone.setProjectId(projectId);
        milestoneService.addMilestone(milestone);

        return "redirect:/milestones/" + projectId;
    }

    @PostMapping("/delete/{id}")
    public String deleteMilestone(@PathVariable int id) {

        int projectID = milestoneService.getMilestoneById(id).getProjectId();
        milestoneService.deleteMilestone(id);


        return "redirect:/milestones/" + projectID;

    }

    @GetMapping("/updateForm/{milestoneId}")
    public String showUpdateForm(Model model, @PathVariable int milestoneId, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = (Role) session.getAttribute("user_role");
        if (userId == null || role == null || !role.equals(Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }

        try {
            Milestone milestone = milestoneService.getMilestoneById(milestoneId);
            model.addAttribute("milestoneId", milestone.getId());
            model.addAttribute("name", milestone.getMilestoneName());
            model.addAttribute("description", milestone.getMilestoneDescription());
            model.addAttribute("milestoneStatus", milestone.getStatus());
            model.addAttribute("deadline", milestone.getDeadline());
            model.addAttribute("completedAt", milestone.getCompletedAt());

        } catch (MilestoneNotFoundException e) {
            return "redirect:/loginform";
        }

        return "updateMilestoneForm";
    }

    @PostMapping("/milestone/update")
    public String updateMileStone(HttpSession session, @RequestParam int milestoneId, @RequestParam String name, @RequestParam String description,
                                  @RequestParam Status status, @RequestParam LocalDate deadline, @RequestParam LocalDateTime completedAt) {

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = (Role) session.getAttribute("user_role");
        if (userId == null || role == null || !role.equals(Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }
        Milestone milestone = milestoneService.getMilestoneById(milestoneId);
        milestone.setMilestoneName(name);
        milestone.setMilestoneDescription(description);
        milestone.setStatus(status);
        milestone.setDeadline(deadline);
        milestone.setCompletedAt(completedAt);

        milestoneService.updateMilestone(milestone);

        return "redirect:/milestones/" + milestone.getProjectId();
    }

}
