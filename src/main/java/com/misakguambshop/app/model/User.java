package com.misakguambshop.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = false)
    private boolean isActive = true;

    private String passwordResetToken;

    private java.sql.Timestamp passwordResetExpiration;

    @Column(name = "reactivation_token")
    private String reactivationToken;

    @Column(name = "is_seller")
    private boolean isSeller = false;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    private String profileImageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    private Set<Role> roles = new HashSet<>();

    public User(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.isActive = true;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public boolean getIsActive() {
        return isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }



    public String getReactivationToken() {
        return reactivationToken;
    }

    public void setReactivationToken(String reactivationToken) {
        this.reactivationToken = reactivationToken;
    }

    public boolean getIsSeller() {
        return isSeller;
    }

    public void setIsSeller(boolean isSeller) {
        this.isSeller = isSeller;
    }

    public boolean isAdmin() {
        return isAdmin || roles.stream().anyMatch(role -> role.getName() == ERole.ADMIN);
    }

    public void setIsAdmin(boolean admin) {
        this.isAdmin = admin;
    }
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}