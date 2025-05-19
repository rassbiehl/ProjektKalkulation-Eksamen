package com.example.projektkalkulationeksamen.validator;

import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionValidator {

    private UserService userService;

    @Autowired
    public SessionValidator(UserService userService) {
        this.userService = userService;
    }

    // bruges ved session og rolle validering
    public boolean isSessionValid(HttpSession session, Role requiredRole) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || !userService.userExistsById(userId)) return false;

        Role role = userService.getUserById(userId).getRole();
        return role == requiredRole;
    }

    // bruges ved simpel session validering
    public boolean isSessionValid(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        return userId != null && userService.userExistsById(userId);
    }
}