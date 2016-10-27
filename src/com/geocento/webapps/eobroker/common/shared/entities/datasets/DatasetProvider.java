package com.geocento.webapps.eobroker.common.shared.entities.datasets;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;

import javax.persistence.*;

/**
 * Created by thomas on 07/10/2016.
 */
@Entity
public class DatasetProvider {

    @Id
    @GeneratedValue
    Long id;

    @Column(unique = true)
    String name;

    @Column(length = 1000)
    String iconUrl;

    @Column(length = 1000)
    String uri;

    @Embedded
    Extent extent;

    @ManyToOne
    Company company;

    public DatasetProvider() {
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
