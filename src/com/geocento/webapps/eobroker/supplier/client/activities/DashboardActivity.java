package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveService;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveServiceHandler;
import com.geocento.webapps.eobroker.supplier.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.supplier.client.places.DashboardPlace;
import com.geocento.webapps.eobroker.supplier.client.places.ServicesPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.DashboardView;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gwt.material.design.client.ui.MaterialToast;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class DashboardActivity extends TemplateActivity implements DashboardView.Presenter {

    private DashboardView dashboardView;

    public CompanyDTO companyDTO;

    public DashboardActivity(DashboardPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        dashboardView = clientFactory.getDashboardView();
        dashboardView.setPresenter(this);
        panel.setWidget(dashboardView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        try {
            REST.withCallback(new MethodCallback<List<ProductServiceDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<ProductServiceDTO> response) {
                    dashboardView.setServices(response);
                }

            }).call(ServicesUtil.assetsService).listProductServices();
        } catch (RequestException e) {
            e.printStackTrace();
        }
        try {
            REST.withCallback(new MethodCallback<CompanyDTO>() {

                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, CompanyDTO companyDTO) {
                    DashboardActivity.this.companyDTO = companyDTO;
                    dashboardView.setCompany(companyDTO);
                }

            }).call(ServicesUtil.assetsService).getCompany(null);
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void bind() {
        super.bind();
        activityEventBus.addHandler(RemoveService.TYPE, new RemoveServiceHandler() {
            @Override
            public void onRemoveService(RemoveService event) {
                if(Window.confirm("Are you sure you want to remove this service?")) {
                    MaterialToast.fireToast("Not implemented yet");
                }
            }
        });

        handlers.add(dashboardView.editCompany().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new CompanyPlace(CompanyPlace.TOKENS.id.toString() + "=" + companyDTO.getId()));
            }
        }));

        handlers.add(dashboardView.getAddService().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new ServicesPlace());
            }
        }));
    }

}
