package com.example.projektkalkulationeksamen.Validator;

import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Service.UserService;
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

    public boolean isSessionValid(HttpSession session, Role requiredRole) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return false;

        Role role = userService.getUserById(userId).getRole();
        return role == requiredRole;
    }

    public boolean isSessionValid(HttpSession session) {
        return session.getAttribute("userId") != null;
    }
}