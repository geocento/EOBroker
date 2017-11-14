package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceSearchFormDTO {

    String searchId;
    String aoiWKT;
    List<FormElementValue> values;
    ProductServiceFormDTO productServiceFormDTO;

    public ProductServiceSearchFormDTO() {
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public List<FormElementValue> getValues() {
        return values;
    }

    public void setValues(List<FormElementValue> values) {
        this.values = values;
    }

    public ProductServiceFormDTO getProductServiceFormDTO() {
        return productServiceFormDTO;
    }

    public void setProductServiceFormDTO(ProductServiceFormDTO productServiceFormDTO) {
        this.productServiceFormDTO = productServiceFormDTO;
    }
}
