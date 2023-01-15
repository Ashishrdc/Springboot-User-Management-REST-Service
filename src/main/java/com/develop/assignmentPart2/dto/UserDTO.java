package com.develop.assignmentPart2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class UserDTO {
    // Will be used to transfer data
    private int id;

    @NotEmpty
    @Size(min=4, message = "Username must be between 4-12 characters!")
    private String username;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags=Pattern.Flag.CASE_INSENSITIVE, message = "Email is invalid!")
    private String email;

    @NotEmpty(message = "Password should not be empty!")
    @Size(min=6, max=12, message = "Password must be between 4-12 characters!")
    private String password;

    private String photo;

    // GETTERS AND SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
