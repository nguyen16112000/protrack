package com.ban.protrack.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ban.protrack.model.User;
import com.ban.protrack.payload.response.BODY;
import com.ban.protrack.service.implementation.NotificationServiceImpl;
import com.ban.protrack.service.implementation.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final NotificationServiceImpl notificationService;

    @GetMapping(value = {"", "/"})
    public ResponseEntity<BODY> getUsers() {
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
    public ResponseEntity<BODY> saveUser(@RequestParam Map<String, String> request){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .message(userService.register(request.get("username"), request.get("password"), request.get("email"), request.get("phone")))
                        .status(CREATED)
                        .build()
        );
    }

    @GetMapping("/{username}")
    @PreAuthorize("#username == authentication.name or hasAuthority('ROLE_ADMIN')")
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

//    TODO
//    @PutMapping("/{username}")
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
                        .status(OK)
                        .build()
        );
    }

    @RequestMapping("/check_token")
    public void checkToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                String secret = "secret";
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String resMessage = "The Token has expired";
                Map<String, String> res = new HashMap<>();
                res.put("message", "Valid Token until " + decodedJWT.getExpiresAt());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), res);
            }
            catch (Exception ex) {
                response.setStatus(OK.value());
                Map<String, String> error = new HashMap<>();
                error.put("message", ex.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        else {
            throw new RuntimeException("Check token is missing");
        }
    }

    @RequestMapping("/refresh_token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                String secret = "secret";
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getByUsername(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", List.of(decodedJWT.getClaims().get("roles").asArray(String.class)))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }
            catch (Exception ex) {
                response.setHeader("error", ex.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", ex.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @PutMapping("/password")
    public ResponseEntity<BODY> changePassword(@RequestParam Map<String, String> request){
        userService.updatePassword(request);
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .message("Updated successful")
                        .status(OK)
                        .build()
        );
    }

    @GetMapping("/{username}/unread")
    public ResponseEntity<BODY> getUnreadNotification(@PathVariable("username") String username){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("notifications", notificationService.getUnreadNotificationsOfUser(username)))
                        .message("Notifications retrieved")
                        .status(OK)
                        .build()
        );
    }

    @GetMapping("/{username}/all")
    public ResponseEntity<BODY> getNotification(@PathVariable("username") String username){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("user", notificationService.getNotificationsOfUser(username)))
                        .message("Notifications retrieved")
                        .status(OK)
                        .build()
        );
    }

    @PostMapping("/{username}/read/{id}")
    public ResponseEntity<BODY> markAsRead(@PathVariable("username") String username, @PathVariable("id") Long id, @RequestParam Integer status, HttpServletRequest request){
        //validate sender token with username
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String sender = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                String secret = "secret";
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                sender = decodedJWT.getSubject();
            }
            catch (Exception ex) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Invalid token")
                                .status(BAD_REQUEST)
                                .build()
                );
            }
        else {
            return ResponseEntity.status(FORBIDDEN).body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Unauthorized")
                            .status(FORBIDDEN)
                            .build()
            );
        }
        if (!Objects.equals(sender, username)) {
            return ResponseEntity.status(FORBIDDEN).body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Unauthorized")
                            .status(FORBIDDEN)
                            .build()
            );
        }
        if (status > 0)
            status = 1;
        else
            status = -1;
        Integer res = notificationService.readNotification(id, username, status);
        if (res == 0) {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Notification is read already")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        else if (res == -1) {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Username not found")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        else if (res == -2) {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("User is project member already")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .message("Notification read")
                        .status(OK)
                        .build()
        );
    }

    @PutMapping("/{username}/read/all")
    public ResponseEntity<BODY> markAllAsRead(@PathVariable("username") String username){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("user", notificationService.readAllNotification(username)))
                        .message("Notifications read")
                        .status(OK)
                        .build()
        );
    }
}
