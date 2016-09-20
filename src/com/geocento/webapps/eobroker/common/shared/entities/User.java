package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 13/06/2016.
 */
@Entity(name = "users")
public class User {

    static public enum USER_ROLE {customer, supplier, administrator};

    static public enum USER_PERMISSION {};

    @Id
    String username;

    @Column(length = 100)
    String password;

    @Column(length = 1000)
    String email;

    @Column(length = 1000)
    String userIcon;

    @Enumerated(EnumType.STRING)
    USER_ROLE role;

    @ElementCollection
    List<USER_PERMISSION> permissions;

    @ManyToOne
    Company company;

    @Temporal(TemporalType.TIMESTAMP)
    Date lastLoggedIn;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public USER_ROLE getRole() {
        return role;
    }

    public void setRole(USER_ROLE role) {
        this.role = role;
    }

    public List<USER_PERMISSION> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<USER_PERMISSION> permissions) {
        this.permissions = permissions;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

}
