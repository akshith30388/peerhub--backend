package com.peerhub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewer_name")
    private String reviewer;

    private String initials;
    private int score;
    private int stars;
    private String project;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private String color;

    @Column(name = "recipient_student_id")
    private Long recipientStudentId;

    @Column(name = "reviewer_student_id")
    private Long reviewerStudentId;

    @Column(name = "instructor_id")
    private Long instructorId;

    @Column(name = "course_name")
    private String courseName;

    private String semester;

    public Review() {}

    public Review(String reviewer, String initials, int score, int stars, String project, String comment, String color) {
        this.reviewer = reviewer;
        this.initials = initials;
        this.score = score;
        this.stars = stars;
        this.project = project;
        this.comment = comment;
        this.color = color;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Long getRecipientStudentId() { return recipientStudentId; }
    public void setRecipientStudentId(Long recipientStudentId) { this.recipientStudentId = recipientStudentId; }
    public Long getReviewerStudentId() { return reviewerStudentId; }
    public void setReviewerStudentId(Long reviewerStudentId) { this.reviewerStudentId = reviewerStudentId; }
    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
