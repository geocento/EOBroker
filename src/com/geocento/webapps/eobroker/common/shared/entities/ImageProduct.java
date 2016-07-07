package com.geocento.webapps.eobroker.common.shared.entities;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class ImageProduct {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 1000, unique = true)
    String name;

    @Column(length = 1000)
    String imageUrl;

    @Column(length = 1000)
    String shortDescription;

    @Column(length = 10000)
    String description;

    @JoinTable(name = "imageproduct_formelement")
    @OneToMany(cascade = CascadeType.ALL)
    List<FormElement> formFields;

    @OneToMany(mappedBy = "product")
    Set<ImageryService> imageServices;

    public ImageProduct() {
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public List<FormElement> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<FormElement> formFields) {
        this.formFields = formFields;
    }

    @PreRemove
    private void removeProduct() {
        if(imageServices == null) {
            return;
        }
        for(ImageryService imageryService : imageServices) {
            imageryService.setProduct(null);
        }
    }
}
