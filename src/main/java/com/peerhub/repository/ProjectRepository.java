package com.peerhub.repository;

import com.peerhub.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	List<Project> findByOwnerStudentIdOrderByIdDesc(Long ownerStudentId);
	List<Project> findByInstructorIdOrderByIdDesc(Long instructorId);
	List<Project> findByInstructorIdAndCourseNameIgnoreCaseOrderByIdDesc(Long instructorId, String courseName);
}
