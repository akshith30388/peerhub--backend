package com.peerhub.dto;

public class AssignmentRequest {
    private String reviewer;
    private String reviewing;
    private String project;
    private String due;

    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    public String getReviewing() { return reviewing; }
    public void setReviewing(String reviewing) { this.reviewing = reviewing; }
    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    public String getDue() { return due; }
    public void setDue(String due) { this.due = due; }
}
