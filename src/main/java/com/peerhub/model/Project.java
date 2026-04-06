package com.peerhub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int members;

    @Column(name = "due_date")
    private String due;

    private int progress;

    @Column(name = "review_count")
    private int reviews;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    @JsonProperty("desc")
    private String description;

    public Project() {}

    public Project(String name, int members, String due, int progress, int reviews, String status, String description) {
        this.name = name;
        this.members = members;
        this.due = due;
        this.progress = progress;
        this.reviews = reviews;
        this.status = status;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getMembers() { return members; }
    public void setMembers(int members) { this.members = members; }
    public String getDue() { return due; }
    public void setDue(String due) { this.due = due; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public int getReviews() { return reviews; }
    public void setReviews(int reviews) { this.reviews = reviews; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @JsonProperty("desc")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
