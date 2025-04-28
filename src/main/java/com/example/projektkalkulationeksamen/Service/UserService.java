package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
}
