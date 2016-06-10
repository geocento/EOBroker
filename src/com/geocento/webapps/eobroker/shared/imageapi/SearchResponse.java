package com.geocento.webapps.eobroker.shared.imageapi;

import java.util.List;

/**
 * Created by thomas on 07/03/2016.
 */
public class SearchResponse extends StatusResponse {

    String id;
    List<ImageProductDTO> imageProductDTOs;

    public SearchResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ImageProductDTO> getImageProductDTOs() {
        return imageProductDTOs;
    }

    public void setImageProductDTOs(List<ImageProductDTO> imageProductDTOs) {
        this.imageProductDTOs = imageProductDTOs;
    }

}
