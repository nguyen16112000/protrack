package com.ban.protrack.service;


import com.ban.protrack.model.User;
import com.ban.protrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepo;

    public Optional<User> login(User user){
        return userRepo.findByUsername(user.getUsername());
    }

    public String logout(User user){
        return null;
    }
}
