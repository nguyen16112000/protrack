package com.ban.protrack.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ban.protrack.model.User;
import com.ban.protrack.payload.response.BODY;
import com.ban.protrack.service.implementation.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

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

    //TODO
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
                        .status(OK)
                        .build()
        );
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
                        .withClaim("role", String.valueOf(Collections.singleton(user.getRole())))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
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
}
