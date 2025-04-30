package com.example.projektkalkulationeksamen.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
@Controller
public class UserController {

    @GetMapping
    public String getUserPage (Model model, HttpSession httpSession) {


        return "userPage";
    }

}
