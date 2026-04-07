package com.peerhub.dto;

public class AssignStudentRequest {
    private String courseName;
    private String semester;

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
