package com.geocento.webapps.eobroker.common.shared.entities.orders;

import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.entities.User;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ImageryRequest {

    @Id
    String id;

    @ManyToOne
    User customer;

    @ManyToOne
    ImageService imageService;

    @Column(length = 1000)
    String aoiWKT;

    @OneToMany(cascade = CascadeType.ALL)
    List<ImageProductEntity> productRequests;

    public ImageryRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public List<ImageProductEntity> getProductRequests() {
        return productRequests;
    }

    public void setProductRequests(List<ImageProductEntity> productRequests) {
        this.productRequests = productRequests;
    }
}
