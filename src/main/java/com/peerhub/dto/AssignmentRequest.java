package com.peerhub.dto;

public class AssignmentRequest {
    private Long reviewerStudentId;
    private Long reviewingStudentId;
    private Long projectId;
    private String reviewer;
    private String reviewing;
    private String project;
    private String due;
    private String status;
    private String courseName;
    private String semester;

    public Long getReviewerStudentId() { return reviewerStudentId; }
    public void setReviewerStudentId(Long reviewerStudentId) { this.reviewerStudentId = reviewerStudentId; }
    public Long getReviewingStudentId() { return reviewingStudentId; }
    public void setReviewingStudentId(Long reviewingStudentId) { this.reviewingStudentId = reviewingStudentId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

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
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
