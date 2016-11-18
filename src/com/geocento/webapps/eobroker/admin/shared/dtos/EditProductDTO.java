package com.geocento.webapps.eobroker.admin.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class EditProductDTO extends ProductDTO {

    List<FeatureDescription> geoinformation;
    List<FormElement> formFields;
    String recommendationRule;
    List<FormElement> apiFormFields;

    public EditProductDTO() {
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
    }

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
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
}
