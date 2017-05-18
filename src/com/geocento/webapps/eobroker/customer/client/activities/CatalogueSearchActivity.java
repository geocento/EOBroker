package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.SearchQuery;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.CatalogueSearchPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.CatalogueSearchView;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetCatalogueDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gwt.material.design.client.ui.MaterialToast;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class CatalogueSearchActivity extends TemplateActivity implements CatalogueSearchView.Presenter {

    private CatalogueSearchView catalogueSearchView;
    private String query;
    private Date startDate;
    private Date stopDate;

    public CatalogueSearchActivity(CatalogueSearchPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        catalogueSearchView = clientFactory.getCatalogueSearchView();
        catalogueSearchView.setPresenter(this);
        setTemplateView(catalogueSearchView.asWidget());
        setTitleText("Browse catalogue");
        displayMenu(false);
        Window.setTitle("Earth Observation Broker");
        bind();
        catalogueSearchView.showQuery();
        catalogueSearchView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        final String text = tokens.get(CatalogueSearchPlace.TOKENS.text.toString());
        Long productId = null;
        if (tokens.containsKey(CatalogueSearchPlace.TOKENS.productId.toString())) {
            try {
                productId = Long.parseLong(tokens.get(CatalogueSearchPlace.TOKENS.productId.toString()));
            } catch (Exception e) {

            }
        }
        if(productId == null) {
            Window.alert("Missing off the shelf product id!");
            History.back();
        }
        catalogueSearchView.clearMap();
        setAoi(currentAoI);
        catalogueSearchView.centerOnAoI();
        Date now = new Date();
        setStartDate(new Date(now.getTime() - 10 * 24 * 3600 * 1000));
        setStopDate(now);
        if(productId != null) {
            try {
                REST.withCallback(new MethodCallback<ProductDatasetCatalogueDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, ProductDatasetCatalogueDTO productDatasetCatalogueDTO) {
                        catalogueSearchView.setProductDatasetCatalogDTO(productDatasetCatalogueDTO);
                    }
                }).call(ServicesUtil.assetsService).getProductDatasetCatalogueDTO(productId);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        }
    }

    private void setStartDate(Date startDate) {
        this.startDate = startDate;
        catalogueSearchView.displayStartDate(startDate);
    }

    private void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
        catalogueSearchView.displayStopDate(stopDate);
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(catalogueSearchView.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateSearch();
            }
        }));

        handlers.add(catalogueSearchView.getQuoteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
            }
        }));

    }

    private void updateSearch() {
        if(currentAoI == null) {
            MaterialToast.fireToast("Please select an area of interest");
            return;
        }
        if(query == null || query.length() == 0) {
            MaterialToast.fireToast("Please select sensors");
            return;
        }
        // check current suggestion
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setAoiWKT(currentAoI.getWktGeometry());
        searchQuery.setStart((long) (startDate.getTime() / 1000.0));
        searchQuery.setStop((long) (stopDate.getTime() / 1000.0));
        catalogueSearchView.clearMap();
        catalogueSearchView.displayAoI(currentAoI);
        catalogueSearchView.displayLoadingResults("Searching products...");
        enableUpdate(false);
        try {
            REST.withCallback(new MethodCallback<List<Product>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<Product> imageProductDTOs) {
                    catalogueSearchView.hideLoadingResults();
                    // add all results to the interface
                    catalogueSearchView.displayImageProducts(imageProductDTOs);
                }
            }).call(ServicesUtil.searchService).queryImages(searchQuery);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void aoiChanged(AoIDTO aoi) {
        setAoi(aoi);
    }

    @Override
    public void onStartDateChanged(Date value) {
        startDate = value;
    }

    private void enableUpdate(boolean enable) {
        // always enabled
        catalogueSearchView.enableUpdate(true);
    }

    @Override
    public void onStopDateChanged(Date value) {
        stopDate = value;
    }

    @Override
    public void onQueryChanged(String value) {
        query = value;
    }

    public void setAoi(AoIDTO aoi) {
        super.setAoI(aoi);
        catalogueSearchView.displayAoI(aoi);
    }

}
