package com.ban.protrack.controller;

import com.ban.protrack.model.Project;
import com.ban.protrack.model.Work;
import com.ban.protrack.payload.response.BODY;
import com.ban.protrack.service.implementation.ProjectServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectServiceImpl projectService;

    @GetMapping(value = {"", "/"})
    public ResponseEntity<BODY> getProjects() throws InterruptedException {
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("projects", projectService.list(0, 30)))
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

    //TODO
    @PutMapping("/{id}")
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
                        .data(Map.of("work", projectService.getWorkofProject(project_id, project_id + "_" + work_id)))
                        .message("Work retrieved")
                        .status(OK)
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<BODY> addProjectAndWorks(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        Long project_id = projectService.addWorksToProject(workMap, workOrderMap);
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .data(Map.of("id", project_id))
                        .message("Project created")
                        .status(OK)
                        .build()
        );
    }

    @PutMapping("/{project_id}/evaluate")
    public ResponseEntity<BODY> evaluate(@PathVariable("project_id") Long project_id){
        projectService.evaluateWorkTime(project_id);
        return ResponseEntity.ok(
                BODY.builder()
                        .timeStamp(now())
                        .message("Evaluate success")
                        .status(OK)
                        .build()
        );
    }
}
