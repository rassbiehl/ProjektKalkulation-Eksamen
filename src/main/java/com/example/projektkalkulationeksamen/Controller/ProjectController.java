package com.example.projektkalkulationeksamen.Controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectController {
    public static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @GetMapping("/adminStartpage")
    public String getAdminDashboardPage (HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            logger.info("User not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";

        }

        return "adminStartpage";
    }
}
