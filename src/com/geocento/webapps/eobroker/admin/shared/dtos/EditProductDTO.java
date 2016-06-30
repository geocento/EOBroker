package com.geocento.webapps.eobroker.admin.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class EditProductDTO extends ProductDTO {

    private List<FormElement> formFields;
    private String recommendationRule;
    private List<FormElement> apiFormFields;

    public EditProductDTO() {
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
