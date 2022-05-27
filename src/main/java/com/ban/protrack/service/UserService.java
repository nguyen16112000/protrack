package com.ban.protrack.service;

import com.ban.protrack.model.User;
import com.ban.protrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    public Iterable<User> findAll() {
        return userRepo.findAll();
    }

    public Optional<User> findById(Integer id) {
        return userRepo.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public void remove(Integer id) {
        userRepo.deleteById(id);
    }




}
