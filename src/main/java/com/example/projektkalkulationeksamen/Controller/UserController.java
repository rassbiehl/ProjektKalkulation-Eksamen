package com.example.projektkalkulationeksamen.Controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final SessionValidator sessionValidator;

    @Autowired
    public UserController(UserService userService, SessionValidator sessionValidator) {
        this.userService = userService;
        this.sessionValidator = sessionValidator;
    }

    @GetMapping
    public String getUserPage(Model model, HttpSession session) {
        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");

        if (!userService.userExistsById(userId)) {
            logger.warn("User ID {} not found in DB. Invalidating session.", userId);
            session.invalidate();
            return "redirect:/loginform";
        }

        Role role = userService.getUserById(userId).getRole();

        model.addAttribute("allUsers", userService.getAllUsers());

        String viewName = role.toString().toLowerCase() + "/userpage";
        logger.info("User with ID {} and role {} accessing {}", userId, role, viewName);

        return viewName;
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        logger.info("User was successfully deleted with ID: {}", id);
        return "redirect:/users";
    }

}
