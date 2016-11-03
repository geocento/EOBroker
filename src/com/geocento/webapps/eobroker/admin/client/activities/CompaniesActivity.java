package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.CompaniesPlace;
import com.geocento.webapps.eobroker.admin.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.CompaniesView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompaniesActivity extends TemplateActivity implements CompaniesView.Presenter {

    private int start = 0;
    private int limit = 10;
    private String orderby = "";
    private String filter;

    private CompaniesView companiesView;

    public CompaniesActivity(CompaniesPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        companiesView = clientFactory.getCompaniesView();
        companiesView.setPresenter(this);
        panel.setWidget(companiesView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        // load companies
        loadCompanies();
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(companiesView.getCreateNewButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new CompanyPlace());
            }
        }));
    }

    private void loadCompanies() {
        if(start == 0) {
            companiesView.clearCompanies();
        }
        try {
            companiesView.setCompaniesLoading(true);
            REST.withCallback(new MethodCallback<List<CompanyDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    companiesView.setCompaniesLoading(false);
                }

                @Override
                public void onSuccess(Method method, List<CompanyDTO> response) {
                    companiesView.setCompaniesLoading(false);
                    start += response.size();
                    companiesView.addCompanies(response.size() == limit, response);
                }
            }).call(ServicesUtil.assetsService).listCompanies(start, limit, orderby, filter);
        } catch (RequestException e) {
        }
    }

    @Override
    public void loadMore() {
        loadCompanies();
    }

    @Override
    public void changeFilter(String value) {
        start = 0;
        filter = value;
        loadCompanies();
    }

}
