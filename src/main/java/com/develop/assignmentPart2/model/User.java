package com.develop.assignmentPart2.model;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    //One user can have multiple roles and one role can have multiple user
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name= "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns= {@JoinColumn(name="ROLE_ID", referencedColumnName = "ID")}
    ) //Merging primary key of one table with foreign key of other table
    private List<Role> roles;

    private String photo;
    private String status;
    private String role;
    boolean Enabled;

    // Constructor
    public User(User user) {
        this.photo = user.getPhoto();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.role = user.getRole();
        this.roles = user.getRoles();

    }

    public User(){

    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
