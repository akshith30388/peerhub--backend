package com.peerhub.controller;

import com.peerhub.dto.AssignmentRequest;
import com.peerhub.model.Assignment;
import com.peerhub.repository.AssignmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;

    public AssignmentController(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @GetMapping
    public List<Assignment> getAll() {
        return assignmentRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> create(@RequestBody AssignmentRequest request) {
        if (request.getReviewer() == null || request.getReviewing() == null
                || request.getProject() == null || request.getDue() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }

        Assignment assignment = new Assignment(
                request.getReviewer(), request.getReviewing(),
                request.getProject(), request.getDue(), "pending"
        );

        Assignment saved = assignmentRepository.save(assignment);
        return ResponseEntity.status(201).body(saved);
    }
}
