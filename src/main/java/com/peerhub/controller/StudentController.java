package com.peerhub.controller;

import com.peerhub.model.Student;
import com.peerhub.repository.StudentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<Student> getAll() {
        return studentRepository.findAll();
    }
}
