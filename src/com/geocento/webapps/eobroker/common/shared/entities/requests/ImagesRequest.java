package com.geocento.webapps.eobroker.common.shared.entities.requests;

import com.geocento.webapps.eobroker.common.server.Utils.GeometryConverter;
import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.entities.Message;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ImagesRequest extends Request {

    @ManyToOne
    ImageService imageService;

    @Convert(converter = GeometryConverter.class)
    String aoiWKT;

    @OneToMany(cascade = CascadeType.ALL)
    List<ImageProductEntity> productRequests;

    @Column(length = 10000)
    String response;

    @OneToMany
    List<Message> messages;

    public ImagesRequest() {
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
