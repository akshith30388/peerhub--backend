package com.peerhub.repository;

import com.peerhub.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	List<Assignment> findByReviewerStudentIdOrderByIdDesc(Long reviewerStudentId);
	List<Assignment> findByReviewerStudentIdAndStatusOrderByIdDesc(Long reviewerStudentId, String status);
	List<Assignment> findByInstructorIdOrderByIdDesc(Long instructorId);
}
