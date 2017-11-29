package com.geocento.webapps.eobroker.admin.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.PerformanceDescription;
import com.geocento.webapps.eobroker.common.shared.entities.ProductCategory;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class EditProductDTO extends ProductDTO {

    List<FeatureDescription> geoinformation;
    List<PerformanceDescription> performances;
    List<FormElement> formFields;
    String recommendationRule;
    List<FormElement> apiFormFields;
    List<ProductCategory> categories;
    private List<ProductCategory> availableCategories;

    public EditProductDTO() {
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
    }

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
    }

    public List<PerformanceDescription> getPerformances() {
        return performances;
    }

    public void setPerformances(List<PerformanceDescription> performances) {
        this.performances = performances;
    }

    public List<FormElement> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<FormElement> formFields) {
        this.formFields = formFields;
    }

    public String getRecommendationRule() {
        return recommendationRule;
    }

    public void setRecommendationRule(String recommendationRule) {
        this.recommendationRule = recommendationRule;
    }

    public List<FormElement> getApiFormFields() {
        return apiFormFields;
    }

    public void setApiFormFields(List<FormElement> apiFormFields) {
        this.apiFormFields = apiFormFields;
    }

    public List<ProductCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ProductCategory> categories) {
        this.categories = categories;
    }

    public List<ProductCategory> getAvailableCategories() {
        return availableCategories;
    }

    public void setAvailableCategories(List<ProductCategory> availableCategories) {
        this.availableCategories = availableCategories;
    }
}
