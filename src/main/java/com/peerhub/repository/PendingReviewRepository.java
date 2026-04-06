package com.peerhub.repository;

import com.peerhub.model.PendingReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingReviewRepository extends JpaRepository<PendingReview, Long> {
}
