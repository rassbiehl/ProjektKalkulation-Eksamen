package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.*;
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

    @ExceptionHandler(AuthRegisterException.class)
    public String handleRegisterFailed(AuthRegisterException e, RedirectAttributes redirectAttributes) {
        logger.warn("Register failed: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/registerform";
    }

    @ExceptionHandler(UserUpdateException.class)
    public String handleUpdateFailed(UserUpdateException e, RedirectAttributes redirectAttributes) {
        logger.warn("User update failed for user ID {}: {}", e.getUserId(), e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/updateform/" + e.getUserId();
    }
}