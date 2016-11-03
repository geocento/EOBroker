package com.geocento.webapps.eobroker.common.shared.entities.project;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Product;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 07/10/2016.
 */
@Entity
public class Project {

    @Id
    @GeneratedValue
    Long id;

    @Column(unique = true)
    String name;

    @Column(length = 1000)
    String iconUrl;

    @Column(length = 10000)
    String description;

    @Column(length = 1000)
    String website;

    @Temporal(TemporalType.TIMESTAMP)
    Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @ManyToMany
    List<Product> coveredProducts;

    @ManyToOne
    Company leader;

    @ManyToMany
    List<Company> consortium;

    public Project() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Product> getCoveredProducts() {
        return coveredProducts;
    }

    public void setCoveredProducts(List<Product> coveredProducts) {
        this.coveredProducts = coveredProducts;
    }

    public Company getLeader() {
        return leader;
    }

    public void setLeader(Company leader) {
        this.leader = leader;
    }

    public List<Company> getConsortium() {
        return consortium;
    }

    public void setConsortium(List<Company> consortium) {
        this.consortium = consortium;
    }
}
