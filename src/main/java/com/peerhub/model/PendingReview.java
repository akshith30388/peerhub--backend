package com.peerhub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pending_reviews")
public class PendingReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "due_info")
    private String due;

    public PendingReview() {}

    public PendingReview(String title, String due) {
        this.title = title;
        this.due = due;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDue() { return due; }
    public void setDue(String due) { this.due = due; }
}
