package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 05/07/2016.
 */
public class FeasibilityRequestDTO {

    Long productServiceId;
    String aoiWKT;
    Date start;
    Date stop;
    List<FormElementValue> formElementValues;

    public FeasibilityRequestDTO() {
    }

    public Long getProductServiceId() {
        return productServiceId;
    }

    public void setProductServiceId(Long productServiceId) {
        this.productServiceId = productServiceId;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    public List<FormElementValue> getFormElementValues() {
        return formElementValues;
    }

    public void setFormElementValues(List<FormElementValue> formElementValues) {
        this.formElementValues = formElementValues;
    }
}
