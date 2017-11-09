package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 20/03/2017.
 */
public class SuccessStoryEditDTO extends SuccessStoryDTO {

    String fullDescription;
    ProductDTO productDTO;
    List<ProductServiceDTO> serviceDTOs;
    List<ProductDatasetDTO> datasetDTOs;
    List<SoftwareDTO> softwareDTOs;
    List<CompanyRoleDTO> consortium;
    private List<EndorsementDTO> endorsements;

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

    public ProductDTO getProductDTO() {
        return productDTO;
    }

    public void setProductDTO(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }

    public List<ProductServiceDTO> getServiceDTOs() {
        return serviceDTOs;
    }

    public void setServiceDTOs(List<ProductServiceDTO> serviceDTOs) {
        this.serviceDTOs = serviceDTOs;
    }

    public List<ProductDatasetDTO> getDatasetDTOs() {
        return datasetDTOs;
    }

    public void setDatasetDTOs(List<ProductDatasetDTO> datasetDTOs) {
        this.datasetDTOs = datasetDTOs;
    }

    public List<SoftwareDTO> getSoftwareDTOs() {
        return softwareDTOs;
    }

    public void setSoftwareDTOs(List<SoftwareDTO> softwareDTOs) {
        this.softwareDTOs = softwareDTOs;
    }

    public List<CompanyRoleDTO> getConsortium() {
        return consortium;
    }

    public void setConsortium(List<CompanyRoleDTO> consortium) {
        this.consortium = consortium;
    }

    public void setEndorsements(List<EndorsementDTO> endorsements) {
        this.endorsements = endorsements;
    }

    public List<EndorsementDTO> getEndorsements() {
        return endorsements;
    }
}
