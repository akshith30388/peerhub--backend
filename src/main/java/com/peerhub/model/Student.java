package com.peerhub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String initials;
    private String team;
    private int submissions;

    @Column(name = "review_count")
    private int reviews;

    private int score;
    private String color;

    public Student() {}

    public Student(String name, String initials, String team, int submissions, int reviews, int score, String color) {
        this.name = name;
        this.initials = initials;
        this.team = team;
        this.submissions = submissions;
        this.reviews = reviews;
        this.score = score;
        this.color = color;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public int getSubmissions() { return submissions; }
    public void setSubmissions(int submissions) { this.submissions = submissions; }
    public int getReviews() { return reviews; }
    public void setReviews(int reviews) { this.reviews = reviews; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
