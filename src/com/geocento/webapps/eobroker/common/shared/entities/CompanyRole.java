package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 07/11/2016.
 */
@Entity
public class CompanyRole {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 1000)
    String role;

    @ManyToOne
    Project project;

    @ManyToOne
    Company company;

    public CompanyRole() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
