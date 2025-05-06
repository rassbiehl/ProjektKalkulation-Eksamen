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
    public String handleLoginFailed(
            AuthenticationFailedException authenticationFailedException,
            RedirectAttributes redirectAttributes
    ) {
        logger.warn("Authentication failed: {}. Redirecting to loginform.", authenticationFailedException.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", authenticationFailedException.getMessage());
        return "redirect:/loginform";
    }


    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException accessDeniedException, Model model) {
        model.addAttribute("errorMessage", accessDeniedException.getMessage());

        return "error/403";
    }

    @ExceptionHandler(AuthRegisterException.class)
    public String handleRegisterFailed(
            AuthRegisterException authRegisterException,
            RedirectAttributes redirectAttributes
    ) {
        logger.warn("Authentication failed: {} Redirecting to registerform", authRegisterException.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", authRegisterException.getMessage());
        return "redirect:/registerform";
    }

    @ExceptionHandler(UserUpdateException.class)
    public String handleUpdateFailed(
            UserUpdateException userUpdateException,
            RedirectAttributes redirectAttributes
    ) {
        logger.warn("Update failed: {} Redirecting to updateform/{}", userUpdateException.getMessage(), userUpdateException.getUserId());
        redirectAttributes.addFlashAttribute("errorMessage", userUpdateException.getMessage());
        return "redirect:/updateform/" + userUpdateException.getUserId();
    }
}
