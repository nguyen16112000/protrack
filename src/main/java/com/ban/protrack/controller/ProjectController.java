package com.ban.protrack.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ban.protrack.model.Project;
import com.ban.protrack.model.User;
import com.ban.protrack.model.Work;
import com.ban.protrack.payload.response.BODY;
import com.ban.protrack.service.implementation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectServiceImpl projectService;

    private final UserServiceImpl userService;

    private final NotificationServiceImpl notiService;

    private final FilesStorageServiceImpl filesStorageService;

    private final WorkServiceImpl workService;

    @GetMapping(value = {"", "/"})
    public ResponseEntity<BODY> getProjects() {
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("projects", projectService.list(0, 30)))
                        .message("Projects retrieved")
                        .status(OK)
                        .build()
        );
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<BODY> getProjectsOfUser(@PathVariable("username") String username){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("projects", projectService.getProjectsOfUser(username)))
                        .message("Projects retrieved")
                        .status(OK)
                        .build()
        );
    }

    @PostMapping(value={"", "/"})
    public ResponseEntity<BODY> saveProject(@RequestBody Project project){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("project", projectService.create(project)))
                        .message("Projects created")
                        .status(CREATED)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@projectServiceImpl.isAllowed(authentication.authorities, #id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BODY> getProject(@PathVariable("id") Long id){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("project", projectService.getById(id)))
                        .message("Project retrieved")
                        .status(OK)
                        .build()
        );
    }

//    TODO
//    @PutMapping("/{id}")
    public ResponseEntity<BODY> updateProject(@PathVariable("id") Long id, @RequestBody Project project){
//        if (!Objects.equals(projectname, project.getProjectname()))
//            return ResponseEntity.badRequest().body("Either 'id' or 'name' must be set");
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("project", projectService.getById(id)))
                        .message("Project updated")
                        .status(CREATED)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BODY> deleteProject(@PathVariable("id") Long id){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("project", projectService.deleteById(id)))
                        .message("Project deleted")
                        .status(OK)
                        .build()
        );
    }

    @GetMapping(value = {"/{project_id}/work", "/{project_id}/work"})
    public ResponseEntity<BODY> getWorksofProject(@PathVariable("project_id") Long project_id){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("work", projectService.getWorksofProject(project_id)))
                        .message("Work retrieved")
                        .status(OK)
                        .build()
        );
    }

    @GetMapping("/{project_id}/work/{work_id}")
    public ResponseEntity<BODY> getWorkofProject(@PathVariable("project_id") Long project_id, @PathVariable("work_id") String work_id){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("work", projectService.getWorkofProject(project_id, work_id)))
                        .message("Work retrieved")
                        .status(OK)
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<BODY> addProjectAndWorks(HttpServletRequest request) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String username = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                String secret = "secret";
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                username = decodedJWT.getSubject();
            }
            catch (Exception ex) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                        .timeStamp(now())
                        .message("Missing token")
                        .status(BAD_REQUEST)
                        .build()
                );
            }
        else {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Missing token")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        JSONObject jsonObject = new JSONObject(sb.toString());
        final ObjectMapper mapper = new ObjectMapper();

        List<Work> works = new ArrayList<>();
        Map<String, Object> workMap = new HashMap<>();
        try {
            workMap = mapper.readValue(jsonObject.get("work").toString(), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
//            LOG.error("Exception occurred while trying to parse String to Map.", e);
        }

        Map<String, Object> workOrderMap = new HashMap<>();
        try {
            workOrderMap = mapper.readValue(jsonObject.get("work_order").toString(), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
//            LOG.error("Exception occurred while trying to parse String to Map.", e);
        }

        String project_name = (jsonObject.has("name")) ? jsonObject.get("name").toString() : "No name project";
        LocalDate start_date = (jsonObject.has("start_date")) ? LocalDate.parse((CharSequence) jsonObject.get("start_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();

        Long project_id = projectService.addWorksToProject(username, project_name, start_date, workMap, workOrderMap);
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("id", project_id))
                        .message("Project created")
                        .status(OK)
                        .build()
        );
    }

    @PutMapping("/evaluate")
    public ResponseEntity<BODY> evaluate( @RequestBody Map<String, Object> request){
        Long project_id = Long.valueOf(request.get("id").toString());
        LocalDate start_date = LocalDate.parse((CharSequence) request.get("start_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        projectService.evaluateWorkTime(project_id, start_date);
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .message("Evaluate success")
                        .status(OK)
                        .build()
        );
    }

    @PostMapping("/{project_id}/invite/{username}")
    public ResponseEntity<BODY> inviteUserToProject(@PathVariable("project_id") Long project_id, @PathVariable("username") String username, HttpServletRequest request){
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
        if (!Objects.equals(projectService.getUserRole(sender, project_id), "ROLE_ADMIN"))
            return ResponseEntity.status(FORBIDDEN).body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Unauthorized")
                            .status(FORBIDDEN)
                            .build()
            );
        if (!notiService.createInvitation(project_id, sender, username)) {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Username not found")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .message("Invited")
                        .status(OK)
                        .build()
        );
    }

    @GetMapping("/{project_id}/work/{work_id}/proof")
    public ResponseEntity<?> getProof(@PathVariable("project_id") Long project_id, @PathVariable("work_id") String work_id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String username = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                String secret = "secret";
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                username = decodedJWT.getSubject();
            }
            catch (Exception ex) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Missing token")
                                .status(BAD_REQUEST)
                                .build()
                );
            }
        else {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Missing token")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        try {
            String worker = workService.getWorker(work_id);
            String sender_role = projectService.getUserRole(username, project_id);
            if (!(Objects.equals(sender_role, "ROLE_ADMIN") || Objects.equals(worker, username))) {
                return ResponseEntity.status(UNAUTHORIZED).body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Unauthorized.")
                                .status(UNAUTHORIZED)
                                .build()
                );
            }
            String fileName;
            if (Objects.equals(sender_role, "ROLE_ADMIN"))
                fileName = projectService.getProofURL(project_id, work_id, true);
            else
                fileName = projectService.getProofURL(project_id, work_id, username);;
            if (fileName == null) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Invalid IDs")
                                .status(BAD_REQUEST)
                                .build()
                );
            }
            else if (fileName.equals("")) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Work has no proof")
                                .status(BAD_REQUEST)
                                .build()
                );
            }
            Resource file = filesStorageService.load(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(Base64.getEncoder().encodeToString(file.getInputStream().readAllBytes()));
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message(ex.getMessage())
                            .status(BAD_REQUEST)
                            .build()
            );
        }

    }

    @PostMapping("/{project_id}/work/{work_id}/proof")
    public ResponseEntity<BODY> setProof(@PathVariable("project_id") Long project_id, @PathVariable("work_id") String work_id, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String username = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                String secret = "secret";
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                username = decodedJWT.getSubject();
            }
            catch (Exception ex) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Missing token")
                                .status(BAD_REQUEST)
                                .build()
                );
            }
        else {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Missing token")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.contains("."))
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Could not upload the file: " + file.getOriginalFilename() + "!")
                                .status(OK)
                                .build()
                );
            String proof = projectService.getProofURL(project_id, work_id, username);
            if (proof == null) {
                return ResponseEntity.status(UNAUTHORIZED).body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Unauthorized.")
                                .status(UNAUTHORIZED)
                                .build()
                );
            }
            else if (!proof.equals("")) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Please wait for admin project to approve work.")
                                .status(BAD_REQUEST)
                                .build()
                );
            }
            String save_name = filesStorageService.save(file, username);
            projectService.setProofURL(project_id, work_id, save_name, username);
            return ResponseEntity.status(HttpStatus.OK).body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("File uploaded")
                            .status(OK)
                            .build()
            );
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    BODY.builder()
                            .timeStamp(now())
                            .message(ex.getMessage())
                            .status(OK)
                            .build()
            );
        }
    }

    @PostMapping("/{project_id}/work/{work_id}/approve")
    public ResponseEntity<BODY> approveWork(@PathVariable("project_id") Long project_id, @PathVariable("work_id") String work_id, @RequestParam("status") Integer status, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String username = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                String secret = "secret";
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                username = decodedJWT.getSubject();
            }
            catch (Exception ex) {
                return ResponseEntity.badRequest().body(
                        BODY.builder()
                                .timeStamp(now())
                                .message("Missing token")
                                .status(BAD_REQUEST)
                                .build()
                );
            }
        else {
            return ResponseEntity.badRequest().body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Missing token")
                            .status(BAD_REQUEST)
                            .build()
            );
        }
        User sender = userService.getByUsername(username);
        if (!Objects.equals(sender.getId(), projectService.getProjectAdmin(project_id))) {
            return ResponseEntity.status(UNAUTHORIZED).body(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Unauthorized.")
                            .status(UNAUTHORIZED)
                            .build()
            );
        }
        projectService.approveWork(project_id, work_id, status > 0);
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("f_date", LocalDate.now()))
                        .message("Successful")
                        .status(OK)
                        .build()
        );
    }

    @GetMapping("/{project_id}/work/{work_id}/start")
    public ResponseEntity<BODY> isStartable(@PathVariable("project_id") Long project_id, @PathVariable("work_id") String work_id) {
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("startable", workService.isStartable(project_id, work_id)))
                        .message("Successful")
                        .status(OK)
                        .build()
        );
    }

//    can xac thuc
    @PostMapping("/{project_id}/work/{work_id}/start")
    public ResponseEntity<BODY> startWork(@PathVariable("project_id") Long project_id, @PathVariable("work_id") String work_id){
        if (workService.startWork(project_id, work_id))
            return ResponseEntity.ok(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Work started")
                            .data(Map.of("s_date", LocalDate.now()))
                            .status(OK)
                            .build()
            );
        else
            return ResponseEntity.ok(
                    BODY.builder()
                            .timeStamp(now())
                            .message("Work can't be started yet")
                            .status(OK)
                            .build()
            );
    }

//    can xac thuc
    @PutMapping("/{project_id}/work/{work_id}")
    public ResponseEntity<BODY> updateWork(@PathVariable("project_id") Long project_id, @PathVariable("work_id") String work_id, @RequestParam Map<String, Object> request){
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("work", workService.update(project_id, work_id, request)))
                        .message("Work updated")
                        .status(OK)
                        .build()
        );
    }





//    @PostMapping("/{project_id}/add/{username}")
//    public ResponseEntity<BODY> addProjectMember (@PathVariable("project_id") Long project_id, @PathVariable("username") String username, HttpServletRequest request){
//        String authorizationHeader = request.getHeader(AUTHORIZATION);
//        String sender = "";
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
//            try {
//                String token = authorizationHeader.substring("Bearer ".length());
//                String secret = "secret";
//                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
//                JWTVerifier verifier = JWT.require(algorithm).build();
//                DecodedJWT decodedJWT = verifier.verify(token);
//                sender = decodedJWT.getSubject();
//            }
//            catch (Exception ex) {
//                return ResponseEntity.badRequest().body(
//                        BODY.builder()
//                                .timeStamp(now())
//                                .message("Invalid token")
//                                .status(BAD_REQUEST)
//                                .build()
//                );
//            }
//        else {
//            return ResponseEntity.status(FORBIDDEN).body(
//                    BODY.builder()
//                            .timeStamp(now())
//                            .message("Unauthorized")
//                            .status(FORBIDDEN)
//                            .build()
//            );
//        }
//        if (!Objects.equals(projectService.getUserRole(sender, project_id), "ROLE_ADMIN"))
//            return ResponseEntity.status(FORBIDDEN).body(
//                    BODY.builder()
//                    .timeStamp(now())
//                    .message("Unauthorized")
//                    .status(FORBIDDEN)
//                    .build()
//            );
//        if (!projectService.addUsertoProject(username, project_id))
//            return ResponseEntity.badRequest().body(
//                    BODY.builder()
//                            .timeStamp(now())
//                            .message("User is project member already")
//                            .status(BAD_REQUEST)
//                            .build()
//            );
//
//        return ResponseEntity.ok(
//                BODY.builder()
//                        .timeStamp(now())
//                        .message("Added successful")
//                        .status(CREATED)
//                        .build()
//        );
//    }
}
