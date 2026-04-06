package com.peerhub.dto;

import com.peerhub.model.User;

public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private String name;
    private String initials;

    public UserDTO() {}

    public UserDTO(Long id, String email, String role, String name, String initials) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.initials = initials;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.name = user.getName();
        this.initials = user.getInitials();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getInitials() { return initials; }
}
