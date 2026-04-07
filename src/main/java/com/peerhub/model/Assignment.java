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

    @Column(name = "reviewer_student_id")
    private Long reviewerStudentId;

    @Column(name = "reviewing_student_id")
    private Long reviewingStudentId;

    @Column(name = "instructor_id")
    private Long instructorId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "course_name")
    private String courseName;

    private String semester;

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
    public Long getReviewerStudentId() { return reviewerStudentId; }
    public void setReviewerStudentId(Long reviewerStudentId) { this.reviewerStudentId = reviewerStudentId; }
    public Long getReviewingStudentId() { return reviewingStudentId; }
    public void setReviewingStudentId(Long reviewingStudentId) { this.reviewingStudentId = reviewingStudentId; }
    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
