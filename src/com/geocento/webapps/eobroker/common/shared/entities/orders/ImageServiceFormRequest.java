package com.geocento.webapps.eobroker.common.shared.entities.orders;

import com.geocento.webapps.eobroker.common.shared.entities.ImageService;

import javax.persistence.*;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ImageServiceFormRequest {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    ImageService imageService;

    @ManyToOne
    ImageryFormRequest imageryFormRequest;

    @Column(length = 10000)
    String response;

    public ImageServiceFormRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ImageryFormRequest getImageryFormRequest() {
        return imageryFormRequest;
    }

    public void setImageryFormRequest(ImageryFormRequest imageryFormRequest) {
        this.imageryFormRequest = imageryFormRequest;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
