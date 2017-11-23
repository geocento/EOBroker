package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.shared.dtos.ProductDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ChallengeView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    String getIconUrl();

    void setIconUrl(String iconURL);

    HasText getDescription();

    String getFullDescription();

    void setFullDescription(String fullDescription);

    HasClickHandlers getSubmit();

    TemplateView getTemplateView();

    void setLoading(String message);

    void setLoadingError(String message);

    void hideLoading(String message);

    void setProducts(List<ProductDTO> productDTOs);

    public interface Presenter {
    }

}
