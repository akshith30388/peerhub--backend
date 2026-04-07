package com.peerhub.controller;

import com.peerhub.dto.AssignmentRequest;
import com.peerhub.model.Assignment;
import com.peerhub.model.Project;
import com.peerhub.model.User;
import com.peerhub.repository.AssignmentRepository;
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
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public AssignmentController(AssignmentRepository assignmentRepository,
                                UserRepository userRepository,
                                ProjectRepository projectRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Assignment> getAll(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number idNum = (Number) claims.get("id");
        String role = claims.get("role", String.class);

        if ("student".equalsIgnoreCase(role)) {
            return assignmentRepository.findByReviewerStudentIdOrderByIdDesc(idNum.longValue());
        }
        return assignmentRepository.findByInstructorIdOrderByIdDesc(idNum.longValue());
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> create(@RequestBody AssignmentRequest request, Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number instructorIdNum = (Number) claims.get("id");
        Long instructorId = instructorIdNum.longValue();

        if (request.getReviewerStudentId() == null || request.getReviewingStudentId() == null || request.getDue() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Reviewer, review target and due date are required"));
        }
        if (request.getReviewerStudentId().equals(request.getReviewingStudentId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Reviewer and target student must be different"));
        }

        User reviewer = userRepository.findById(request.getReviewerStudentId()).orElse(null);
        User reviewing = userRepository.findById(request.getReviewingStudentId()).orElse(null);
        if (reviewer == null || reviewing == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Selected students were not found"));
        }

        if (!"student".equalsIgnoreCase(reviewer.getRole()) || !"student".equalsIgnoreCase(reviewing.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Assignments can only be created for students"));
        }

        if (!instructorId.equals(reviewer.getInstructorId()) || !instructorId.equals(reviewing.getInstructorId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only assign your own students"));
        }

        Project project;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId()).orElse(null);
            if (project == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Selected project was not found"));
            }
            if (project.getOwnerStudentId() == null || !reviewing.getId().equals(project.getOwnerStudentId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Selected project must belong to the student being reviewed"));
            }

            // Backfill legacy projects that existed before instructor/course metadata was enforced.
            boolean changed = false;
            if (project.getInstructorId() == null) {
                project.setInstructorId(instructorId);
                changed = true;
            } else if (!instructorId.equals(project.getInstructorId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Selected project belongs to a different instructor"));
            }

            String selectedCourse = nonBlank(request.getCourseName(), reviewing.getCourseName());
            if (project.getCourseName() == null || project.getCourseName().isBlank()) {
                project.setCourseName(selectedCourse);
                changed = true;
            } else if (selectedCourse != null && !selectedCourse.isBlank() && !selectedCourse.equalsIgnoreCase(project.getCourseName())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Selected project is not in the chosen course"));
            }

            if (project.getSemester() == null || project.getSemester().isBlank()) {
                project.setSemester(nonBlank(request.getSemester(), reviewing.getSemester()));
                changed = true;
            }

            if (changed) {
                project = projectRepository.save(project);
            }
        } else {
            if (request.getProject() == null || request.getProject().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Project is required"));
            }
            project = new Project();
            project.setName(request.getProject().trim());
            project.setDescription("");
            project.setMembers(1);
            project.setDue(request.getDue());
            project.setProgress(0);
            project.setReviews(0);
            project.setStatus("pending");
            project.setOwnerStudentId(reviewing.getId());
            project.setInstructorId(instructorId);
            project.setCourseName(nonBlank(request.getCourseName(), reviewing.getCourseName()));
            project.setSemester(nonBlank(request.getSemester(), reviewing.getSemester()));
            project = projectRepository.save(project);
        }

        Assignment assignment = new Assignment(
                reviewer.getName(), reviewing.getName(),
                project.getName(), request.getDue(), "pending"
        );
        assignment.setReviewerStudentId(reviewer.getId());
        assignment.setReviewingStudentId(reviewing.getId());
        assignment.setInstructorId(instructorId);
        assignment.setProjectId(project.getId());
        assignment.setCourseName(nonBlank(project.getCourseName(), nonBlank(request.getCourseName(), reviewing.getCourseName())));
        assignment.setSemester(nonBlank(project.getSemester(), nonBlank(request.getSemester(), reviewing.getSemester())));

        Assignment saved = assignmentRepository.save(assignment);
        return ResponseEntity.status(201).body(saved);
    }

    private String nonBlank(String first, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return fallback;
    }
}
