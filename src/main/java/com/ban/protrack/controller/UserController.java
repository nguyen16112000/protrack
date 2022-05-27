package com.ban.protrack.controller;

import com.ban.protrack.model.User;
import com.ban.protrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public ResponseEntity<Iterable<User>> getAllUser(){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable(name = "username") String username){
        Optional<User> user = userService.findByUsername(username);
        return user.map(user1 -> new ResponseEntity<>(user1, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<User> addUser(@RequestBody User user){
        return new ResponseEntity<>(userService.save(user), HttpStatus.OK);
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable(name = "username") String username, @RequestBody User user){
        System.out.println(user);
        Optional<User> updateUser = userService.findByUsername(username);
        return updateUser.map(user1 -> {
            user.setId(user1.getId());
            return new ResponseEntity<>(userService.save(user), HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<User> deleteUser(@PathVariable(name = "username") String username){
        Optional<User> user = userService.findByUsername(username);
        return user.map(user1 -> {
            userService.remove(user.get().getId());
            return new ResponseEntity<>(user1, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
