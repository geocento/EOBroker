package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 08/11/2016.
 */
public class OfferDTO {

    CompanyDTO companyDTO;
    List<ProductServiceDTO> productServiceDTOs;
    List<ProductDatasetDTO> productDatasetDTOs;
    List<SoftwareDTO> softwareDTOs;
    List<ProjectDTO> projectDTOs;
    List<DatasetProviderDTO> datasetProviderDTOs;

    public OfferDTO() {
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public void setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public List<ProductServiceDTO> getProductServiceDTOs() {
        return productServiceDTOs;
    }

    public void setProductServiceDTOs(List<ProductServiceDTO> productServiceDTOs) {
        this.productServiceDTOs = productServiceDTOs;
    }

    public List<ProductDatasetDTO> getProductDatasetDTOs() {
        return productDatasetDTOs;
    }

    public void setProductDatasetDTOs(List<ProductDatasetDTO> productDatasetDTOs) {
        this.productDatasetDTOs = productDatasetDTOs;
    }

    public List<SoftwareDTO> getSoftwareDTOs() {
        return softwareDTOs;
    }

    public void setSoftwareDTOs(List<SoftwareDTO> softwareDTOs) {
        this.softwareDTOs = softwareDTOs;
    }

    public List<ProjectDTO> getProjectDTOs() {
        return projectDTOs;
    }

    public void setProjectDTOs(List<ProjectDTO> projectDTOs) {
        this.projectDTOs = projectDTOs;
    }

    public List<DatasetProviderDTO> getDatasetProviderDTOs() {
        return datasetProviderDTOs;
    }

    public void setDatasetProviderDTOs(List<DatasetProviderDTO> datasetProviderDTOs) {
        this.datasetProviderDTOs = datasetProviderDTOs;
    }
}
