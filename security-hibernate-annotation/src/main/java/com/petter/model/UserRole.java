package com.petter.model;

import javax.persistence.*;

/**
 * @author hongxf
 * @since 2017-04-17 10:32
 */
@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = {"role", "username"}))
public class UserRole {
    private Integer userRoleId;
    private User user;
    private String role;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "role", nullable = false, length = 45)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
