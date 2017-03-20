package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 20/03/2017.
 */
public class SuccessStoryEditDTO extends SuccessStoryDTO {

    String fullDescription;
    List<ProductProjectDTO> products;
    List<CompanyRoleDTO> consortium;

    public SuccessStoryEditDTO() {
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public CompanyDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CompanyDTO customer) {
        this.customer = customer;
    }

    public List<ProductProjectDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductProjectDTO> products) {
        this.products = products;
    }

    public List<CompanyRoleDTO> getConsortium() {
        return consortium;
    }

    public void setConsortium(List<CompanyRoleDTO> consortium) {
        this.consortium = consortium;
    }
}
