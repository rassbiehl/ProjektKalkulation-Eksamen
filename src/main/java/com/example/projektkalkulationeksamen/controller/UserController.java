package com.example.projektkalkulationeksamen.controller;

import com.example.projektkalkulationeksamen.exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.service.UserService;
import com.example.projektkalkulationeksamen.validation.SessionValidation;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final SessionValidation sessionValidation;

    @Autowired
    public UserController(UserService userService, SessionValidation sessionValidation) {
        this.userService = userService;
        this.sessionValidation = sessionValidation;
    }

    @GetMapping
    public String getUserPage(
            Model model,
            HttpSession session
    ) {
        if (!sessionValidation.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");

        Role role = userService.getUserById(userId).getRole();

        model.addAttribute("allUsers", userService.getAllUsers());

        String viewName = role.toString().toLowerCase() + "/userpage";
        logger.info("User with ID {} and role {} accessing {}", userId, role, viewName);
        return viewName;

    }


    @GetMapping("/delete/{id}")
    public String deleteUser(
            HttpSession session,
            @PathVariable int id
    ) {
        if (!sessionValidation.isSessionValid(session, Role.ADMIN)) {
            throw new AccessDeniedException("Only admins can delete users");
        }
        // prevents the admin of deleting itself.
        if (id == (int) session.getAttribute("userId")) {
            throw new AccessDeniedException("You cannot delete your own account");
        }
        userService.deleteUser(id);

        Integer adminId = (Integer) session.getAttribute("userId");
        logger.info("Admin with ID {} deleted user with ID {}", adminId, id);
        return "redirect:/users";
    }

}
