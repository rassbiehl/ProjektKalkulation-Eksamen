package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Service.MilestoneService;
import com.example.projektkalkulationeksamen.Service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MilestoneController {

    private final MilestoneService milestoneService;
    private final ProjectService projectService;

    public MilestoneController(MilestoneService milestoneService, ProjectService projectService) {
        this.milestoneService = milestoneService;
        this.projectService = projectService;
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
        model.addAttribute("project", projectService.getProjectById(project_id));

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
    public String deleteMilestone(@PathVariable int id, @RequestParam int projectId) {

        milestoneService.deleteMilestone(id);


        return "redirect:/milestones/" + projectId;

    }

    @GetMapping("/updateMilestoneForm/{milestoneId}")
    public String showUpdateForm(Model model, @PathVariable int milestoneId, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = (Role) session.getAttribute("user_role");
        if (userId == null || role == null || !role.equals(Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }

        try {
            Milestone milestone = milestoneService.getMilestoneById(milestoneId);
            model.addAttribute("milestone",milestone);
            model.addAttribute("status",Status.values());
        } catch (MilestoneNotFoundException e) {
            return "redirect:/loginform";
        }

        return "projectmanager/updateMilestoneForm";
    }

    @PostMapping("/milestone/update")
    public String updateMileStone(HttpSession session,@RequestParam int milestoneId, @ModelAttribute Milestone milestone) {

        Integer userId = (Integer) session.getAttribute("userId");
        Role role = (Role) session.getAttribute("user_role");
        if (userId == null || role == null || !role.equals(Role.PROJECTMANAGER)) {
            return "redirect:/loginform";
        }
        milestone.setId(milestoneId);
         milestoneService.updateMilestone(milestone);

         int projectId = milestoneService.getMilestoneById(milestoneId).getProjectId();


        return "redirect:/milestones/" + projectId;
    }

}
