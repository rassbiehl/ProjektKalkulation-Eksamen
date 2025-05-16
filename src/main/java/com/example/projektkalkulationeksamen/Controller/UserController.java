package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.notfound.UserNotFoundException;
import com.example.projektkalkulationeksamen.Exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.Service.AuthService;
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
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, SessionValidator sessionValidator, AuthService authService) {
        this.userService = userService;
        this.sessionValidator = sessionValidator;
        this.authService = authService;
    }

    @GetMapping
    public String getUserPage(
            Model model,
            HttpSession session
    ) {
        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        Integer userId = (Integer) session.getAttribute("userId");

        User user = userService.getUserById(userId); // NotfoundException bliver fanget globalt.
        Role role = user.getRole();

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
        if (!sessionValidator.isSessionValid(session, Role.ADMIN)) {
            throw new AccessDeniedException("Only admins can delete users");
        }

        userService.deleteUser(id); // DeletionException bliver fanget globalt.

        logger.info("User was successfully deleted with ID: {}", id);
        return "redirect:/users";
    }

}
