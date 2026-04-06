package com.peerhub.dto;

public class GoogleAuthRequest {
    private String idToken;
    private String role;
    private String mode;
    private String captchaToken;

    public GoogleAuthRequest() {}

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getCaptchaToken() { return captchaToken; }
    public void setCaptchaToken(String captchaToken) { this.captchaToken = captchaToken; }
}