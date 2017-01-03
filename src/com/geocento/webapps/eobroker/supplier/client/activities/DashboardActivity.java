package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.*;
import com.geocento.webapps.eobroker.supplier.client.places.*;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.DashboardView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.OfferDTO;
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
        setTemplateView(dashboardView.getTemplateView());
        panel.setWidget(dashboardView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        try {
            displayFullLoading("Loading assets...");
            REST.withCallback(new MethodCallback<OfferDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Error loading assets...");
                }

                @Override
                public void onSuccess(Method method, OfferDTO response) {
                    hideFullLoading();
                    companyDTO = response.getCompanyDTO();
                    dashboardView.setCompany(companyDTO);
                    dashboardView.setServices(response.getProductServiceDTOs());
                    dashboardView.setProductDatasets(response.getProductDatasetDTOs());
                    dashboardView.setSoftwares(response.getSoftwareDTOs());
                    dashboardView.setProjects(response.getProjectDTOs());
                    dashboardView.setDatasets(response.getDatasetProviderDTOs());
                }

            }).call(ServicesUtil.assetsService).getOffer();
        } catch (RequestException e) {
            e.printStackTrace();
        }
/*
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

            }).call(ServicesUtil.assetsService).getCompany();
        } catch (RequestException e) {
            e.printStackTrace();
        }

        try {
            REST.withCallback(new MethodCallback<List<DatasetProviderDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<DatasetProviderDTO> response) {
                    dashboardView.setDatasets(response);
                }

            }).call(ServicesUtil.assetsService).listDatasets();
        } catch (RequestException e) {
            e.printStackTrace();
        }

        try {
            REST.withCallback(new MethodCallback<List<ProductDatasetDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<ProductDatasetDTO> response) {
                    dashboardView.setProductDatasets(response);
                }

            }).call(ServicesUtil.assetsService).listProductDatasets();
        } catch (RequestException e) {
            e.printStackTrace();
        }

*/
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

        activityEventBus.addHandler(RemoveDataset.TYPE, new RemoveDatasetHandler() {
            @Override
            public void onRemoveDataset(RemoveDataset event) {
                if(Window.confirm("Are you sure you want to remove this dataset?")) {
                    MaterialToast.fireToast("Not implemented yet");
                }
            }
        });

        activityEventBus.addHandler(RemoveProductDataset.TYPE, new RemoveProductDatasetHandler() {
            @Override
            public void onRemoveProductDataset(RemoveProductDataset event) {
                if(Window.confirm("Are you sure you want to remove this product dataset?")) {
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

        handlers.add(dashboardView.getAddProductDataset().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new ProductDatasetPlace());
            }
        }));

        handlers.add(dashboardView.getAddSoftware().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new SoftwarePlace());
            }
        }));

        handlers.add(dashboardView.getAddProject().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new ProjectPlace());
            }
        }));

        handlers.add(dashboardView.getAddDataset().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new DatasetProviderPlace());
            }
        }));
    }

}
