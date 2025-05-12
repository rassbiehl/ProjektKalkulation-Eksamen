package com.example.projektkalkulationeksamen;

import com.example.projektkalkulationeksamen.Service.AuthService;
import com.example.projektkalkulationeksamen.Service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProjektKalkulationEksamenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjektKalkulationEksamenApplication.class, args);
        String rawPassword = "123456";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        System.out.println(rawPassword);
        System.out.println(hashedPassword);
    }

}
