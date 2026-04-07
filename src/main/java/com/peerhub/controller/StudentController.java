package com.peerhub.controller;

import com.peerhub.dto.AssignStudentRequest;
import com.peerhub.model.User;
import com.peerhub.repository.AssignmentRepository;
import com.peerhub.repository.ReviewRepository;
import com.peerhub.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final ReviewRepository reviewRepository;

    public StudentController(UserRepository userRepository,
                             AssignmentRepository assignmentRepository,
                             ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<Map<String, Object>> getAll(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number instructorIdNum = (Number) claims.get("id");
        Long instructorId = instructorIdNum.longValue();

        return userRepository.findByRoleAndInstructorIdOrderByNameAsc("student", instructorId).stream()
                .map(student -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", student.getId());
                    row.put("name", student.getName());
                    row.put("initials", student.getInitials());
                    row.put("team", student.getCourseName() == null ? "Unassigned" : student.getCourseName());
                    row.put("submissions", assignmentRepository.findByReviewerStudentIdOrderByIdDesc(student.getId()).size());
                    int reviewsGiven = assignmentRepository.findByReviewerStudentIdAndStatusOrderByIdDesc(student.getId(), "done").size();
                    row.put("reviews", reviewsGiven);
                    List<Integer> scores = reviewRepository.findByRecipientStudentIdOrderByIdDesc(student.getId()).stream().map(r -> r.getScore()).collect(Collectors.toList());
                    int avg = scores.isEmpty() ? 0 : (int) Math.round(scores.stream().mapToInt(v -> v).average().orElse(0));
                    row.put("score", avg);
                    row.put("color", "#2655A6");
                    return row;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<Map<String, Object>> getAvailableStudents() {
        return userRepository.findByRoleAndInstructorIdIsNullOrderByNameAsc("student").stream()
                .map(student -> Map.<String, Object>of(
                        "id", student.getId(),
                        "name", student.getName(),
                        "email", student.getEmail(),
                        "initials", student.getInitials()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/{studentId}/assign")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> assignStudent(@PathVariable Long studentId,
                                           @RequestBody AssignStudentRequest request,
                                           Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number instructorIdNum = (Number) claims.get("id");
        Long instructorId = instructorIdNum.longValue();
        String instructorName = claims.get("name", String.class);

        User student = userRepository.findById(studentId).orElse(null);
        if (student == null || !"student".equalsIgnoreCase(student.getRole())) {
            return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
        }
        if (student.getInstructorId() != null && !instructorId.equals(student.getInstructorId())) {
            return ResponseEntity.status(409).body(Map.of("error", "Student is already assigned to another instructor"));
        }

        String courseName = request.getCourseName() == null ? "" : request.getCourseName().trim();
        String semester = request.getSemester() == null ? "" : request.getSemester().trim();
        if (courseName.isBlank() || semester.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Course name and semester are required"));
        }

        student.setInstructorId(instructorId);
        student.setInstructorName(instructorName);
        student.setCourseName(courseName);
        student.setSemester(semester);
        userRepository.save(student);

        return ResponseEntity.ok(Map.of(
                "id", student.getId(),
                "name", student.getName(),
                "instructorName", student.getInstructorName(),
                "courseName", student.getCourseName(),
                "semester", student.getSemester()
        ));
    }

    @GetMapping("/me-profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentProfile(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number studentIdNum = (Number) claims.get("id");

        return userRepository.findById(studentIdNum.longValue())
                .map(student -> ResponseEntity.ok(Map.of(
                        "id", student.getId(),
                        "name", student.getName(),
                        "email", student.getEmail(),
                        "instructorName", student.getInstructorName() == null ? "Not assigned" : student.getInstructorName(),
                        "courseName", student.getCourseName() == null ? "Not assigned" : student.getCourseName(),
                        "semester", student.getSemester() == null ? "Not assigned" : student.getSemester()
                )))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Student not found")));
    }
}
