package com.peerhub.util;

import com.peerhub.model.*;
import com.peerhub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;
    private final ReviewRepository reviewRepo;
    private final PendingReviewRepository pendingRepo;
    private final StudentRepository studentRepo;
    private final AssignmentRepository assignmentRepo;
    private final SettingRepository settingRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepo, ProjectRepository projectRepo, ReviewRepository reviewRepo,
                      PendingReviewRepository pendingRepo, StudentRepository studentRepo,
                      AssignmentRepository assignmentRepo, SettingRepository settingRepo,
                      PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
        this.reviewRepo = reviewRepo;
        this.pendingRepo = pendingRepo;
        this.studentRepo = studentRepo;
        this.assignmentRepo = assignmentRepo;
        this.settingRepo = settingRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) {
            System.out.println("Database already seeded — skipping.");
            return;
        }

        System.out.println("Seeding database with demo data...");

        // ── Users ──
        userRepo.save(new User("alex@university.edu", passwordEncoder.encode("student123"), "student", "Alex Martinez", "AM"));
        userRepo.save(new User("priya@university.edu", passwordEncoder.encode("student123"), "student", "Priya Sharma", "PS"));
        userRepo.save(new User("prof.rivera@university.edu", passwordEncoder.encode("teach123"), "instructor", "Prof. Rivera", "PR"));

        // ── Projects ──
        projectRepo.save(new Project("E-Commerce UI Redesign", 4, "Feb 24", 72, 3, "progress", "Modern storefront redesign built with React & Spring Boot."));
        projectRepo.save(new Project("Machine Learning Pipeline", 3, "Mar 02", 45, 1, "pending", "End-to-end ML pipeline with data preprocessing & model eval."));
        projectRepo.save(new Project("REST API Documentation", 5, "Feb 20", 92, 5, "done", "Swagger/OpenAPI docs for a production Spring Boot backend."));
        projectRepo.save(new Project("Mobile App Prototype", 2, "Mar 10", 28, 0, "pending", "React Native prototype for a student task management app."));

        // ── Reviews Received ──
        reviewRepo.save(new Review("Priya S.", "PS", 92, 5, "E-Commerce UI Redesign",
                "Excellent documentation and code readability. The state management pattern is well thought out. Great use of component composition throughout the project.", "#2655A6"));
        reviewRepo.save(new Review("Jordan K.", "JK", 88, 4, "E-Commerce UI Redesign",
                "Solid architecture choices. The API layer is clean. Consider adding error boundary handling in the React components for better resilience.", "#2A7A45"));
        reviewRepo.save(new Review("Sam C.", "SC", 74, 3, "REST API Documentation",
                "Good effort overall. Needs better test coverage and the database queries could be optimised further to reduce response times.", "#D97706"));

        // ── Pending Reviews ──
        pendingRepo.save(new PendingReview("ML Pipeline \u2014 Team Sigma", "Due Feb 21"));
        pendingRepo.save(new PendingReview("Mobile App \u2014 Team Gamma", "Due Feb 26"));

        // ── Students ──
        studentRepo.save(new Student("Maya Patel", "MP", "Team Sigma", 2, 2, 95, "#7C3AED"));
        studentRepo.save(new Student("Priya Sharma", "PS", "Team Beta", 2, 3, 91, "#2655A6"));
        studentRepo.save(new Student("Alex Martinez", "AM", "Team Alpha", 3, 2, 88, "#E8622A"));
        studentRepo.save(new Student("Sam Chen", "SC", "Team Delta", 1, 2, 82, "#2A7A45"));
        studentRepo.save(new Student("Jordan Kim", "JK", "Team Alpha", 3, 1, 74, "#D97706"));

        // ── Assignments ──
        assignmentRepo.save(new Assignment("Alex M.", "Priya S.", "ML Pipeline", "Feb 21", "pending"));
        assignmentRepo.save(new Assignment("Jordan K.", "Sam C.", "REST API", "Feb 18", "done"));
        assignmentRepo.save(new Assignment("Sam C.", "Alex M.", "E-Commerce UI", "Feb 24", "progress"));
        assignmentRepo.save(new Assignment("Priya S.", "Maya P.", "Mobile App", "Feb 26", "pending"));
        assignmentRepo.save(new Assignment("Maya P.", "Jordan K.", "REST API", "Feb 20", "done"));

        // ── Settings ──
        settingRepo.save(new Setting("courseName", "FSAD-PS26 \u2014 Full Stack Application Development"));
        settingRepo.save(new Setting("semester", "Spring 2025"));
        settingRepo.save(new Setting("reviewDeadlineBuffer", "3 days after submission"));
        settingRepo.save(new Setting("anonymousReviews", "Enabled"));
        settingRepo.save(new Setting("maxReviewsPerStudent", "3"));
        settingRepo.save(new Setting("gradingVisibility", "Instructor only until deadline"));

        System.out.println("Database seeded successfully!");
    }
}
