package com.peerhub.repository;

import com.peerhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRoleOrderByNameAsc(String role);
    List<User> findByRoleAndInstructorIdOrderByNameAsc(String role, Long instructorId);
    List<User> findByRoleAndInstructorIdIsNullOrderByNameAsc(String role);
}
