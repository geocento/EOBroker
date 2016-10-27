package com.geocento.webapps.eobroker.admin.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 07/10/2016.
 */
public class DatasetProviderDTO {

    Long id;
    String name;
    String uri;
    Extent extent;
    CompanyDTO companyDTO;
    private String iconURL;

    public DatasetProviderDTO() {
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public void setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}
