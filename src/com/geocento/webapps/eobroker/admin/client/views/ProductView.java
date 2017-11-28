package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.PerformanceDescription;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasText getName();

    HasText getShortDescription();

    String getImageUrl();

    void setImageUrl(String imageUrl);

    HasText getOtherNames();

    String getDescription();

    void setDescription(String description);

    Sector getSector();

    void setSector(Sector sector);

    Thematic getThematic();

    void setThematic(Thematic thematic);

    List<FormElement> getFormFields();

    void setFormFields(List<FormElement> fields);

    HasClickHandlers getSubmit();

    void setLoading(String message);

    void setLoadingError(String message);

    void hideLoading(String message);

    void setRecommendationRule(String recommendationRule);

    void setAPIFields(List<FormElement> apiFields);

    List<FormElement> getAPIFields();

    String getRecommendationRule();

    void setGeoinformation(List<FeatureDescription> geoinformation);

    List<FeatureDescription> getGeoinformation();

    void setPerformances(List<PerformanceDescription> performanceDescriptions);

    List<PerformanceDescription> getPerformances();

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
