package com.peerhub.controller;

import com.peerhub.model.Project;
import com.peerhub.model.User;
import com.peerhub.repository.ProjectRepository;
import com.peerhub.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Project> getAll(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number idNum = (Number) claims.get("id");
        String role = claims.get("role", String.class);

        if ("student".equalsIgnoreCase(role)) {
            return projectRepository.findByOwnerStudentIdOrderByIdDesc(idNum.longValue());
        }
        return projectRepository.findByInstructorIdOrderByIdDesc(idNum.longValue());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> getProjectsForStudent(@PathVariable Long studentId, Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number instructorIdNum = (Number) claims.get("id");
        Long instructorId = instructorIdNum.longValue();

        User student = userRepository.findById(studentId).orElse(null);
        if (student == null || !"student".equalsIgnoreCase(student.getRole())) {
            return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
        }
        if (!instructorId.equals(student.getInstructorId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only access your own students' projects"));
        }

        return ResponseEntity.ok(projectRepository.findByOwnerStudentIdOrderByIdDesc(studentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(p -> ResponseEntity.ok((Object) p))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Project not found")));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> create(@RequestBody Project request, Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number idNum = (Number) claims.get("id");

        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Project name is required"));
        }

        return userRepository.findById(idNum.longValue())
            .<ResponseEntity<?>>map(studentUser -> {
                    Project project = new Project();
                    project.setName(request.getName().trim());
                    project.setMembers(Math.max(1, request.getMembers()));
                    project.setDue(request.getDue());
                    project.setProgress(Math.max(0, request.getProgress()));
                    project.setReviews(0);
                    project.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "pending" : request.getStatus());
                    project.setDescription(request.getDescription() == null ? "" : request.getDescription());
                    project.setOwnerStudentId(studentUser.getId());
                    project.setInstructorId(studentUser.getInstructorId());
                    project.setCourseName(studentUser.getCourseName());
                    project.setSemester(studentUser.getSemester());
                    return ResponseEntity.status(201).body(projectRepository.save(project));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Student not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Project request, Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number studentIdNum = (Number) claims.get("id");
        Long studentId = studentIdNum.longValue();

        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Project not found"));
        }
        if (project.getOwnerStudentId() == null || !studentId.equals(project.getOwnerStudentId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only update your own projects"));
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            project.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getDue() != null) {
            project.setDue(request.getDue());
        }
        if (request.getMembers() > 0) {
            project.setMembers(request.getMembers());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            project.setStatus(request.getStatus());
        }

        return ResponseEntity.ok(projectRepository.save(project));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number studentIdNum = (Number) claims.get("id");
        Long studentId = studentIdNum.longValue();

        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Project not found"));
        }
        if (project.getOwnerStudentId() == null || !studentId.equals(project.getOwnerStudentId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only delete your own projects"));
        }

        projectRepository.delete(project);
        return ResponseEntity.ok(Map.of("message", "Project deleted"));
    }
}
