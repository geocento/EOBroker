package com.geocento.webapps.eobroker.customer.shared;

/**
 * Created by thomas on 21/04/2017.
 */
public class OfferingSearchParameters {

    Long aoiId;
    String aoiWKT;
    Long productId;
    Long companyId;

    public OfferingSearchParameters() {
    }

    public Long getAoiId() {
        return aoiId;
    }

    public void setAoiId(Long aoiId) {
        this.aoiId = aoiId;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
