package com.geocento.webapps.eobroker.common.shared.entities.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.orders.ImageProductEntity;

import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
public class ImageryRequestDTO {

    String id;
    String serviceName;
    CompanyDTO companyDTO;
    List<ImageProductEntity> productRequests;

    public ImageryRequestDTO() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public void setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public List<ImageProductEntity> getProductRequests() {
        return productRequests;
    }

    public void setProductRequests(List<ImageProductEntity> productRequests) {
        this.productRequests = productRequests;
    }
}
