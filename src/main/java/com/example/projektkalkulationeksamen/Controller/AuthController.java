package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.Exceptions.notfound.UserNotFoundException;
import com.example.projektkalkulationeksamen.Exceptions.user.AuthRegisterException;
import com.example.projektkalkulationeksamen.Exceptions.user.UserUpdateException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final AuthService authService;
    private final SessionValidator sessionValidator;

    @Autowired
    public AuthController(UserService userService, AuthService authService, SessionValidator sessionValidator) {
        this.userService = userService;
        this.authService = authService;
        this.sessionValidator = sessionValidator;
    }

    @GetMapping("/")
    public String redirectToLogin(HttpSession session) {
        if (!sessionValidator.isSessionValid(session)) {
            logger.info("User is not logged in for current session. Redirecting to loginform.html");
            return "redirect:/loginform";
        }

        int userId = (Integer) session.getAttribute("userId");

        Role role = userService.getUserById(userId).getRole();
        logger.info("User already logged in. Redirecting to: {}Startpage", role.toString().toLowerCase());
        return "redirect:/projects/" + role.toString().toLowerCase() + "Startpage";
    }

    @GetMapping("/loginform")
    public String getLoginFormPage() {
        logger.info("Redirecting to loginform");
        return "loginform";
    }

    @PostMapping("/login")
    public String login(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String rawPassword
    ) {

        User user = authService.login(username, rawPassword);

        Role role = user.getRole();

        session.setAttribute("userId", user.getId());
        session.setAttribute("user_role", user.getRole());

        logger.info("User successfully logged in with userID: {}. Redirecting to {} Startpage", user.getId(), role.toString().toLowerCase());

        return "redirect:/projects/" + role.toString().toLowerCase() + "Startpage";
    }

    @GetMapping("/registerform")
    public String showRegisterForm(HttpSession session, Model model) {

        if (!sessionValidator.isSessionValid(session, Role.ADMIN)) {

            logger.info("Access denied: user lacks admin privileges");
            throw new AccessDeniedException("User lacks admin privileges");
        }

        logger.info("loading registerform");

        model.addAttribute("roles", Role.values());
        return "admin/registerform";
    }

    @PostMapping("/register")
    public String adminRegister(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String rawPassword,
            @RequestParam Role role,
            RedirectAttributes redirectAttributes

    ) {

        if (!sessionValidator.isSessionValid(session, Role.ADMIN)) {
            logger.info("Access denied: user lacks admin privileges");
            throw new AccessDeniedException("User lacks admin privileges");
        }

        try {
            logger.debug("attempting to create new user with username: {} and role: {}", username, role.toString());

            authService.adminRegister(username, rawPassword, role);

            User createdUser = userService.getUserByUsername(username); //
            logger.info("Successfully created new user with userID: {} and role: {}",
                    createdUser.getId(), createdUser.getRole());
            return "redirect:/registerform";
        } catch (AuthRegisterException e) {
            logger.info("Failed to register user with username: {}", username, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/registerform";
        }
    }

    @GetMapping("/updateform/{id}")
    public String showUpdateForm(
            HttpSession session,
            Model model,
            @PathVariable int id
    ) {

        if (!sessionValidator.isSessionValid(session, Role.ADMIN)) {
            Integer userId = (Integer) session.getAttribute("userId");
            logger.info("Access denied: user with ID {} lacks admin privileges", userId);
            throw new AccessDeniedException("User lacks admin privileges");
        }

            User user = userService.getUserById(id); // kan smide notfoundexception

            model.addAttribute("userId", user.getId());
            model.addAttribute("roles", Role.values());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("selectedRole", user.getRole());

            logger.info("loading registerform.html");

        return "admin/updateform";
    }

    @PostMapping("/updateuser")
    public String adminUpdate(HttpSession session,
                              @RequestParam String username,
                              @RequestParam(required = false) String rawPassword,
                              @RequestParam Role role,
                              @RequestParam int id,
                              RedirectAttributes redirectAttributes) {

        if (!sessionValidator.isSessionValid(session, Role.ADMIN)) {
            Integer userId = (Integer) session.getAttribute("userId");
            logger.info("Access denied: user with ID {} lacks admin privileges", userId);
            throw new AccessDeniedException("User lacks admin privileges");
        }

        try {
            logger.debug("attempting to update existing user with username: {} and role: {}", username, role.toString());

            authService.adminUpdate(id, username, rawPassword, role);

            logger.info("Succesfully updated user with ID: {}", id);

            return "redirect:/users";
        } catch (UserUpdateException e) {
            logger.error("Failed to update user with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/updateform/" + id;
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        logger.info("User successfully logged out of session");
        return "redirect:/loginform";
    }

}
