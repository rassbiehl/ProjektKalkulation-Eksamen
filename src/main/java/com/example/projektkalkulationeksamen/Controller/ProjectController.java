package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.AccessDeniedException;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Service.UserService;
import com.example.projektkalkulationeksamen.Validator.SessionValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectController {
    public static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final UserService userService;
    private final SessionValidator sessionValidator;

    @Autowired
    public ProjectController(UserService userService, SessionValidator sessionValidator) {
        this.userService = userService;
        this.sessionValidator = sessionValidator;
    }

    @GetMapping("/adminStartpage")
    public String getAdminStartPage(HttpSession session, Model model) {


        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        if (!sessionValidator.isSessionValid(session, Role.ADMIN)) {
            Integer userId = (Integer) session.getAttribute("userId");
            logger.info("Access denied: user with ID {} lacks admin privileges", userId);
            throw new AccessDeniedException("User lacks admin privileges");
        }


        return "admin/startpage";
    }

    @GetMapping("/projectmanagerStartpage")
    public String getProjectManagerStartPage(HttpSession session, Model model) {

        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        if (!sessionValidator.isSessionValid(session, Role.PROJECTMANAGER)) {
            Integer userId = (Integer) session.getAttribute("userId");
            logger.info("Access denied: user with ID {} lacks project manager privileges", userId);
            throw new AccessDeniedException("User lacks project manager privileges");
        }

        return "projectmanager/startpage";
    }

    @GetMapping("/employeeStartpage")
    public String getEmployeeStartPage(HttpSession session, Model model) {

        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        if (!sessionValidator.isSessionValid(session, Role.EMPLOYEE)) {
            Integer userId = (Integer) session.getAttribute("userId");
            logger.info("Access denied: user with ID {} is not an employee", userId);
            throw new AccessDeniedException("User is not an employee");
        }

        return "employee/startpage";
    }
}
