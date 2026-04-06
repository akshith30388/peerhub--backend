package com.peerhub.dto;

import java.util.Map;

public class ReviewRequest {
    private String project;
    private int score;
    private String comment;
    private Map<String, Integer> ratings;

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Map<String, Integer> getRatings() { return ratings; }
    public void setRatings(Map<String, Integer> ratings) { this.ratings = ratings; }
}
