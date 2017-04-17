package com.petter.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author hongxf
 * @since 2017-03-20 10:50
 */
@Entity
@Table(name = "user_attempts")
public class UserAttempts {

    private int id;
    private String username;
    private int attempts;
    private Date lastModified;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "attempts")
    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastModified")
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
