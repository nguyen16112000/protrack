package com.ban.protrack.controller;

import com.ban.protrack.model.User;
import com.ban.protrack.payload.response.BODY;
import com.ban.protrack.service.implementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping(value = {"", "/"})
    public ResponseEntity<BODY> getUsers() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("users", userService.list(0, 30)))
                        .message("Users retrieved")
                        .status(OK)
                        .build()
        );
    }

    @PostMapping(value={"", "/"})
    public ResponseEntity<BODY> saveUser(@RequestBody User user){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("user", userService.create(user)))
                        .message("Users created")
                        .status(CREATED)
                        .build()
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<BODY> getUser(@PathVariable("username") String username){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("user", userService.getByUsername(username)))
                        .message("User retrieved")
                        .status(CREATED)
                        .build()
        );
    }

    @PutMapping("/{username}")
    public ResponseEntity<BODY> updateUser(@PathVariable("username") String username, @RequestBody User user){
//        if (!Objects.equals(username, user.getUsername()))
//            return ResponseEntity.badRequest().body("Either 'id' or 'name' must be set");
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("user", userService.getByUsername(username)))
                        .message("User retrieved")
                        .status(CREATED)
                        .build()
        );
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<BODY> deleteUser(@PathVariable("username") String username){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("user", userService.deleteByUsername(username)))
                        .message("User deleted")
                        .status(CREATED)
                        .build()
        );
    }
}
