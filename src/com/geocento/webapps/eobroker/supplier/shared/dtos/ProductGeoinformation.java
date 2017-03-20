package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.PerformanceDescription;

import java.util.List;

/**
 * Created by thomas on 18/03/2017.
 */
public class ProductGeoinformation {

    List<FeatureDescription> featureDescriptions;
    List<PerformanceDescription> performanceDescriptions;

    public ProductGeoinformation() {
    }

    public List<FeatureDescription> getFeatureDescriptions() {
        return featureDescriptions;
    }

    public void setFeatureDescriptions(List<FeatureDescription> featureDescriptions) {
        this.featureDescriptions = featureDescriptions;
    }

    public List<PerformanceDescription> getPerformanceDescriptions() {
        return performanceDescriptions;
    }

    public void setPerformanceDescriptions(List<PerformanceDescription> performanceDescriptions) {
        this.performanceDescriptions = performanceDescriptions;
    }
}
