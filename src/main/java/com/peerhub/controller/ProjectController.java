package com.peerhub.controller;

import com.peerhub.model.Project;
import com.peerhub.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(p -> ResponseEntity.ok((Object) p))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Project not found")));
    }
}
