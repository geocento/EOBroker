package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.events.SearchImagery;
import com.geocento.webapps.eobroker.customer.client.events.SearchImageryHandler;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.FullView;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class FullViewActivity extends TemplateActivity implements FullView.Presenter {

    private FullView fullView;

    public FullViewActivity(FullViewPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        fullView = clientFactory.getFullView();
        fullView.setPresenter(this);
        setTemplateView(fullView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(SearchImagery.TYPE, new SearchImageryHandler() {
            @Override
            public void onSearchImagery(SearchImagery event) {
                clientFactory.getPlaceController().goTo(new ImageSearchPlace(place.getToken()));
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        String selectedTab = null;
        if(tokens.containsKey(FullViewPlace.TOKENS.tab.toString())) {
            selectedTab = tokens.get(FullViewPlace.TOKENS.tab.toString());
        }
        if(tokens.containsKey(FullViewPlace.TOKENS.companyid.toString())) {
            selectMenu("companies");
            try {
                Long companyId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.companyid.toString()));
                loadCompanyDetails(companyId, selectedTab);
            } catch (Exception e) {

            }
        } else if(tokens.containsKey(FullViewPlace.TOKENS.productserviceid.toString())) {
            selectMenu("productservices");
            try {
                Long productServiceId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.productserviceid.toString()));
                loadProductServiceDetails(productServiceId, selectedTab);
            } catch (Exception e) {

            }
        } else if(tokens.containsKey(FullViewPlace.TOKENS.productid.toString())) {
            selectMenu("products");
            try {
                Long productId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.productid.toString()));
                loadProductDetails(productId, selectedTab);
            } catch (Exception e) {

            }
        } else if(tokens.containsKey(FullViewPlace.TOKENS.productdatasetid.toString())) {
            selectMenu("productdatasets");
            try {
                Long productDatasetId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.productdatasetid.toString()));
                loadProductDatasetDetails(productDatasetId, selectedTab);
            } catch (Exception e) {

            }
        } else if(tokens.containsKey(FullViewPlace.TOKENS.softwareid.toString())) {
            selectMenu("software");
            try {
                Long softwareId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.softwareid.toString()));
                loadSoftwareDetails(softwareId, selectedTab);
            } catch (Exception e) {

            }
        } else if(tokens.containsKey(FullViewPlace.TOKENS.projectid.toString())) {
            selectMenu("project");
            try {
                Long projectId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.projectid.toString()));
                loadProjectDetails(projectId, selectedTab);
            } catch (Exception e) {

            }
        }
    }

    private void loadProductDetails(Long productId, String selectedTab) {
        fullView.setTitle("Loading product details...");
        fullView.clearDetails();
        displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProductDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, ProductDescriptionDTO productDescriptionDTO) {
                    hideLoading();
                    setTitleText("Product");
                    fullView.displayProduct(productDescriptionDTO);
                    if(selectedTab != null) {
                        fullView.selectTab(selectedTab);
                    }
                }
            }).call(ServicesUtil.assetsService).getProductDescription(productId);
        } catch (RequestException e) {
        }
    }

    private void loadCompanyDetails(Long companyId, String selectedTab) {
        fullView.setTitle("Loading company details...");
        fullView.clearDetails();
        displayLoading();
        try {
            REST.withCallback(new MethodCallback<CompanyDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, CompanyDescriptionDTO companyDescriptionDTO) {
                    hideLoading();
                    setTitleText("Company");
                    fullView.displayCompany(companyDescriptionDTO);
                    if(selectedTab != null) {
                        fullView.selectTab(selectedTab);
                    }
                }
            }).call(ServicesUtil.assetsService).getCompanyDescription(companyId);
        } catch (RequestException e) {
        }
    }

    private void loadProductServiceDetails(Long productServiceId, String selectedTab) {
        fullView.setTitle("Loading product service details...");
        fullView.clearDetails();
        displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProductServiceDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, ProductServiceDescriptionDTO productServiceDescriptionDTO) {
                    hideLoading();
                    setTitleText("Bespoke service");
                    fullView.displayProductService(productServiceDescriptionDTO);
                    if(selectedTab != null) {
                        fullView.selectTab(selectedTab);
                    }
                }
            }).call(ServicesUtil.assetsService).getProductServiceDescription(productServiceId);
        } catch (RequestException e) {
        }
    }

    private void loadProductDatasetDetails(Long productDatasetId, String selectedTab) {
        fullView.setTitle("Loading off the shelf data details...");
        fullView.clearDetails();
        displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProductDatasetDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, ProductDatasetDescriptionDTO productDatasetDescriptionDTO) {
                    hideLoading();
                    setTitleText("Off-the-shelf data");
                    fullView.displayProductDataset(productDatasetDescriptionDTO);
                    if(selectedTab != null) {
                        fullView.selectTab(selectedTab);
                    }
                }
            }).call(ServicesUtil.assetsService).getProductDatasetDescription(productDatasetId);
        } catch (RequestException e) {
        }
    }

    private void loadSoftwareDetails(Long softwareId, String selectedTab) {
        fullView.setTitle("Loading software information...");
        fullView.clearDetails();
        displayLoading();
        try {
            REST.withCallback(new MethodCallback<SoftwareDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not find software");
                }

                @Override
                public void onSuccess(Method method, SoftwareDescriptionDTO softwareDescriptionDTO) {
                    hideLoading();
                    setTitleText("Software");
                    fullView.displaySoftware(softwareDescriptionDTO);
                    if(selectedTab != null) {
                        fullView.selectTab(selectedTab);
                    }
                }
            }).call(ServicesUtil.assetsService).getSoftwareDescription(softwareId);
        } catch (RequestException e) {
        }
    }

    private void loadProjectDetails(Long projectId, String selectedTab) {
        fullView.setTitle("Loading project information...");
        fullView.clearDetails();
        displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProjectDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not find project");
                }

                @Override
                public void onSuccess(Method method, ProjectDescriptionDTO projectDescriptionDTO) {
                    hideLoading();
                    setTitleText("Project");
                    fullView.displayProject(projectDescriptionDTO);
                    if(selectedTab != null) {
                        fullView.selectTab(selectedTab);
                    }
                }
            }).call(ServicesUtil.assetsService).getProjectDescription(projectId);
        } catch (RequestException e) {
        }
    }

}
