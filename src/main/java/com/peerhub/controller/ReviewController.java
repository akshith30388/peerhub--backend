package com.peerhub.controller;

import com.peerhub.dto.ReviewRequest;
import com.peerhub.model.PendingReview;
import com.peerhub.model.Review;
import com.peerhub.repository.PendingReviewRepository;
import com.peerhub.repository.ReviewRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final PendingReviewRepository pendingReviewRepository;

    public ReviewController(ReviewRepository reviewRepository, PendingReviewRepository pendingReviewRepository) {
        this.reviewRepository = reviewRepository;
        this.pendingReviewRepository = pendingReviewRepository;
    }

    @GetMapping
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @GetMapping("/pending")
    public List<PendingReview> getPending() {
        return pendingReviewRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest request, Authentication auth) {
        try {
            Claims claims = (Claims) auth.getPrincipal();

            Review review = new Review();
            review.setReviewer(claims.get("name", String.class));
            review.setInitials(claims.get("initials", String.class));
            review.setScore(request.getScore());
            review.setStars(Math.round((float) request.getScore() / 20));
            review.setProject(request.getProject());
            review.setComment(request.getComment());
            review.setColor("#E8622A");

            Review saved = reviewRepository.save(review);

            // Remove matching pending review
            String projectFirst = request.getProject().split(" ")[0];
            pendingReviewRepository.findAll().stream()
                    .filter(p -> p.getTitle().contains(projectFirst))
                    .findFirst()
                    .ifPresent(pendingReviewRepository::delete);

            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to submit review"));
        }
    }
}
