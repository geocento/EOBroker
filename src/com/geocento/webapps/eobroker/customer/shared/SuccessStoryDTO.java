package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class SuccessStoryDTO {

    Long id;
    String name;
    String description;
    String imageUrl;
    CompanyDTO customer;
    ProductDTO productDTO;
    Date date;
    CompanyDTO company;
    private List<EndorsementDTO> endorsements;

    public SuccessStoryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public CompanyDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CompanyDTO customer) {
        this.customer = customer;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public ProductDTO getProductDTO() {
        return productDTO;
    }

    public void setProductDTO(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }

    public List<EndorsementDTO> getEndorsements() {
        return endorsements;
    }

    public void setEndorsements(List<EndorsementDTO> endorsements) {
        this.endorsements = endorsements;
    }
}
