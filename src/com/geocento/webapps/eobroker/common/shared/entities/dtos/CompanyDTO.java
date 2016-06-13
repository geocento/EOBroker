package com.geocento.webapps.eobroker.common.shared.entities.dtos;

/**
 * Created by thomas on 07/06/2016.
 */
public class CompanyDTO {

    Long id;
    String name;
    String iconURL;
    String description;

    public CompanyDTO() {
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

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
