package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.PerformanceDescription;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductWithFiltersDTO {

    Long id;
    String name;
    private List<FeatureDescription> geoinformation;
    private List<PerformanceDescription> performances;

    public ProductWithFiltersDTO() {
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

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
    }

    public void setPerformances(List<PerformanceDescription> performances) {
        this.performances = performances;
    }

    public List<PerformanceDescription> getPerformances() {
        return performances;
    }
}
