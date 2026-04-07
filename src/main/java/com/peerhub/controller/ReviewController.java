package com.peerhub.controller;

import com.peerhub.dto.ReviewRequest;
import com.peerhub.model.Assignment;
import com.peerhub.model.PendingReview;
import com.peerhub.model.Project;
import com.peerhub.model.Review;
import com.peerhub.repository.AssignmentRepository;
import com.peerhub.repository.ProjectRepository;
import com.peerhub.repository.ReviewRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final AssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            AssignmentRepository assignmentRepository,
                            ProjectRepository projectRepository) {
        this.reviewRepository = reviewRepository;
        this.assignmentRepository = assignmentRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Review> getAll(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number idNum = (Number) claims.get("id");
        String role = claims.get("role", String.class);

        if ("student".equalsIgnoreCase(role)) {
            return reviewRepository.findByRecipientStudentIdOrderByIdDesc(idNum.longValue());
        }
        return reviewRepository.findByInstructorIdOrderByIdDesc(idNum.longValue());
    }

    @GetMapping("/pending")
    public List<PendingReview> getPending(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number idNum = (Number) claims.get("id");
        String role = claims.get("role", String.class);

        List<Assignment> assignments;
        if ("student".equalsIgnoreCase(role)) {
            assignments = assignmentRepository.findByReviewerStudentIdAndStatusOrderByIdDesc(idNum.longValue(), "pending");
        } else {
            assignments = assignmentRepository.findByInstructorIdOrderByIdDesc(idNum.longValue()).stream()
                    .filter(a -> "pending".equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());
        }

        return assignments.stream().map(a -> {
            PendingReview pending = new PendingReview();
            pending.setId(a.getId());
            pending.setTitle(a.getProject() + " - " + a.getReviewing());
            pending.setDue("Due " + a.getDue());
            return pending;
        }).collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest request, Authentication auth) {
        try {
            Claims claims = (Claims) auth.getPrincipal();
            Number reviewerIdNum = (Number) claims.get("id");

            if (request.getAssignmentId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Assignment is required"));
            }

            Assignment assignment = assignmentRepository.findById(request.getAssignmentId()).orElse(null);
            if (assignment == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Assignment not found"));
            }
            if (assignment.getReviewerStudentId() == null || reviewerIdNum.longValue() != assignment.getReviewerStudentId()) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only submit your assigned reviews"));
            }
            if (!"pending".equalsIgnoreCase(assignment.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "This assignment is already completed"));
            }

            Review review = new Review();
            review.setReviewer(claims.get("name", String.class));
            review.setInitials(claims.get("initials", String.class));
            review.setScore(request.getScore());
            review.setStars(Math.round((float) request.getScore() / 20));
            review.setProject(assignment.getProject());
            review.setComment(request.getComment());
            review.setColor("#E8622A");
            review.setReviewerStudentId(assignment.getReviewerStudentId());
            review.setRecipientStudentId(assignment.getReviewingStudentId());
            review.setInstructorId(assignment.getInstructorId());
            review.setCourseName(assignment.getCourseName());
            review.setSemester(assignment.getSemester());

            Review saved = reviewRepository.save(review);

            assignment.setStatus("done");
            assignmentRepository.save(assignment);

            if (assignment.getProjectId() != null) {
                Project project = projectRepository.findById(assignment.getProjectId()).orElse(null);
                if (project != null) {
                    project.setReviews(project.getReviews() + 1);
                    int updatedProgress = Math.min(100, project.getProgress() + 20);
                    project.setProgress(updatedProgress);
                    if (updatedProgress >= 100) {
                        project.setStatus("done");
                    } else if (updatedProgress > 0 && "pending".equalsIgnoreCase(project.getStatus())) {
                        project.setStatus("progress");
                    }
                    projectRepository.save(project);
                }
            }

            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to submit review"));
        }
    }
}
