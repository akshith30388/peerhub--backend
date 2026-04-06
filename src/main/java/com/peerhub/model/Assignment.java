package com.peerhub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewer;
    private String reviewing;
    private String project;

    @Column(name = "due_date")
    private String due;

    @Column(nullable = false, length = 20)
    private String status;

    public Assignment() {}

    public Assignment(String reviewer, String reviewing, String project, String due, String status) {
        this.reviewer = reviewer;
        this.reviewing = reviewing;
        this.project = project;
        this.due = due;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    public String getReviewing() { return reviewing; }
    public void setReviewing(String reviewing) { this.reviewing = reviewing; }
    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    public String getDue() { return due; }
    public void setDue(String due) { this.due = due; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
