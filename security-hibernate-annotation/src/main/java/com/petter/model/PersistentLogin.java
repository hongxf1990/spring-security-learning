package com.petter.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author hongxf
 * @since 2017-04-17 14:42
 */
@Entity
@Table(name = "persistent_logins")
public class PersistentLogin {

    private String series;
    private String username;
    private String token;
    private Date lastUsed;

    @Id
    @Column(name = "series")
    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "token", nullable = false)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_used", nullable = false)
    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}
