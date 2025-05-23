package com.example.projektkalkulationeksamen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ProjektKalkulationEksamenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjektKalkulationEksamenApplication.class, args);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123"));
    }

}
