package com.geocento.webapps.eobroker.shared.entities.dtos;

import com.geocento.webapps.eobroker.shared.entities.Sector;
import com.geocento.webapps.eobroker.shared.entities.Thematic;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductDTO {

    Long id;
    String name;
    String description;
    Sector sector;
    Thematic thematic;
    private String imageUrl;

    public ProductDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Thematic getThematic() {
        return thematic;
    }

    public void setThematic(Thematic thematic) {
        this.thematic = thematic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
