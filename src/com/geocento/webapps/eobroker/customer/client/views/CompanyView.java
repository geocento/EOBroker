package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.COMPANY_SIZE;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.shared.UserDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface CompanyView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    HasText getEmail();

    HasText getWebsite();

    HasText getDescription();

    String getFullDescription();

    void setFullDescription(String fullDescription);

    HasClickHandlers getSubmit();

    String getIconUrl();

    void setIconUrl(String iconURL);

    Date getStartedIn();

    void setStartedIn(Date date);

    HasText getAddress();

    void setCountryCode(String countryCode);

    void setCompanySize(COMPANY_SIZE companySize);

    void setAffiliates(List<CompanyDTO> affiliates);

    String getCountryCode();

    COMPANY_SIZE getCompanySize();

    List<CompanyDTO> getAffiliates();

    void setUsers(List<UserDTO> users);

    HasClickHandlers getViewClient();

    public interface Presenter {
    }

}
