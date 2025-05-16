package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.database.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.notfound.NotFoundException;
import com.example.projektkalkulationeksamen.Exceptions.security.AccessDeniedException;
import com.example.projektkalkulationeksamen.Exceptions.security.AuthenticationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationFailedException.class)
    public String handleLoginFailed(AuthenticationFailedException e, RedirectAttributes redirectAttributes) {
        logger.warn("Login failed: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/loginform";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        logger.warn("Access denied: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(DatabaseException.class)
    public String handleDatabaseException(DatabaseException e, Model model) {
        logger.warn("Internal server error during database operation: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "error/500";
    }

    // covers tasks, milestone, projects and users.
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException e, Model model) {
        logger.warn("Resource not found: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }


}