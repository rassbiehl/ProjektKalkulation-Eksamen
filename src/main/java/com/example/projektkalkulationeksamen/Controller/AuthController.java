package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.Service.AuthService;
import com.example.projektkalkulationeksamen.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/")
    public String redirectToLogin(HttpSession session){
        if (session.getAttribute("userId") == null){
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        int userId = (Integer) session.getAttribute("userId");

        Role role = userService.getUserById(userId).getRole();
        logger.info("User already logged in. Redirecting to: " + role.toString().toLowerCase() + "Startpage");
        return "redirect:/" + role.toString().toLowerCase() + "Startpage";
    }

    @GetMapping("/loginform")
    public String getLoginFormPage() {
        return "loginform";
    }

    @PostMapping("/login")
    public String login (HttpSession session, @RequestParam String username, @RequestParam String rawPassword) {

        authService.login(username, rawPassword);

        User user = userService.getUserByUsername(username);

        Role role = user.getRole();

        session.setAttribute("userId", user.getId());

        logger.info("User successfully logged in with userID: {}. Redirecting to {} Startpage", user.getId(), role.toString().toLowerCase());

        return "redirect:/" + role.toString().toLowerCase() + "Startpage";
    }

    @GetMapping("/registerform")
    public String showRegisterForm() {
        return "registerform";
    }

    @PostMapping("/register")
    public String adminRegister (@RequestParam String username, @RequestParam String rawPassword, @RequestParam Role role) {

        logger.debug("attempting to create new user with username: {} and role: {}", username, role.toString());

        authService.adminRegister(username, rawPassword, role);

        logger.info("Successfully created new user with userID: {} and role: {}", userService.getUserByUsername(username).getId(), userService.getUserByUsername(username).getRole().toString());

        return "redirect:/registerform";
    }












}
