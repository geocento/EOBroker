package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.List;

/**
 * Created by thomas on 07/03/2016.
 */
public class SearchResponse extends StatusResponse {

    String id;
    List<ImageProductDTO> productDTOs;

    public SearchResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ImageProductDTO> getImageProductDTOs() {
        return productDTOs;
    }

    public void setImageProductDTOs(List<ImageProductDTO> imageProductDTOs) {
        this.productDTOs = imageProductDTOs;
    }

}
