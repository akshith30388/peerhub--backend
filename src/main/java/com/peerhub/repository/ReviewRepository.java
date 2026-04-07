package com.peerhub.repository;

import com.peerhub.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByRecipientStudentIdOrderByIdDesc(Long recipientStudentId);
	List<Review> findByInstructorIdOrderByIdDesc(Long instructorId);
}
