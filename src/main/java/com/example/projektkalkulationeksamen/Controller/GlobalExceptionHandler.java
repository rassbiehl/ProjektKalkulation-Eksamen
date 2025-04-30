package com.example.projektkalkulationeksamen.Controller;

import com.example.projektkalkulationeksamen.Exceptions.AuthenticationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    logger.warn("Authentication failed: {}", authenticationFailedException.getMessage());
    redirectAttributes.addFlashAttribute("errorMessage", authenticationFailedException.getMessage());
    return "redirect:/loginform";
}

}
