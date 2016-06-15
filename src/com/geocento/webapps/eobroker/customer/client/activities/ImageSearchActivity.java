package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.geocento.webapps.eobroker.common.shared.entities.ImageryService;
import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.customer.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.customer.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ImageSearchView;
import com.google.gwt.core.client.Callback;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchActivity extends AbstractApplicationActivity implements ImageSearchView.Presenter {

    private ImageSearchView imageSearchView;
    private AoI aoi;
    private String sensors;
    private Date startDate;
    private Date stopDate;
    private String currency;

    public ImageSearchActivity(ImageSearchPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        imageSearchView = clientFactory.getImageSearchView();
        imageSearchView.setPresenter(this);
        panel.setWidget(imageSearchView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        imageSearchView.setMapLoadedHandler(new Callback<Void, Exception>() {
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
        final String text = tokens.get(SearchPagePlace.TOKENS.text.toString());
        Long productId = null;
        if (tokens.containsKey(SearchPagePlace.TOKENS.product.toString())) {
            try {
                productId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.product.toString()));
            } catch (Exception e) {

            }
        }
        String browse = tokens.get(SearchPagePlace.TOKENS.browse.toString());
        Long aoiId = null;
        if (tokens.containsKey(SearchPagePlace.TOKENS.aoiId.toString())) {
            try {
                aoiId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.aoiId.toString()));
            } catch (Exception e) {
            }
        }
        // TODO - load suppliers
        List<ImageryService> suppliers = generateSuppliersList();
        imageSearchView.setSuppliers(suppliers);
        imageSearchView.displaySupplier(suppliers.get(0));
/*
        AoIPolygon aoIPolygon = new AoIPolygon();
        aoIPolygon.setWktRings("-2.4609375 56.87749838693283,0.439453125 58.6583707283785,3.076171875 57.25973712933438,3.251953125 55.5581558834549,0.087890625 55.10822970202758,-2.4609375 55.805911967706635,-2.4609375 56.87749838693283");
        setAoi(aoIPolygon);
*/
        setAoi(Customer.currentAoI);
        setSensors(text);
        Date now = new Date();
        setStartDate(new Date(now.getTime() - 10 * 24 * 3600 * 1000));
        setStopDate(now);
        setCurrency("EUR");
        enableUpdateMaybe();
        if(aoi != null && text != null && text.length() > 0) {
            updateSearch();
        }
    }

    private void setSensors(String sensors) {
        this.sensors = sensors;
        imageSearchView.displaySensors(sensors);
    }

    private void setStartDate(Date startDate) {
        this.startDate = startDate;
        imageSearchView.displayStartDate(startDate);
    }

    private void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
        imageSearchView.displayStopDate(stopDate);
    }

    private List<ImageryService> generateSuppliersList() {
        List<ImageryService> suppliers = new ArrayList<ImageryService>();
        ImageryService geocentoImageryService = new ImageryService();
        geocentoImageryService.setName("EarthImages");
        geocentoImageryService.setDescription("Geocento's image search and ordering service. EarthImages provides access to over 30 of the most popular satellite missions.");
        suppliers.add(geocentoImageryService);
        ImageryService ksatImageryService = new ImageryService();
        ksatImageryService.setName("KSAT MMO");
        ksatImageryService.setDescription("KSAT's image search and ordering service.");
        suppliers.add(ksatImageryService);
        return suppliers;
    }

    @Override
    protected void bind() {

        handlers.add(imageSearchView.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateSearch();
            }
        }));

        handlers.add(imageSearchView.getHomeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new LandingPagePlace());
            }
        }));
    }

    private void updateSearch() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setAoiWKT("POLYGON((" + ((AoIPolygon) aoi).getWktRings() + "))");
        searchRequest.setSensors(getSensorsFilter(sensors));
        searchRequest.setStart(startDate.getTime());
        searchRequest.setStop(stopDate.getTime());
        searchRequest.setCurrency("EUR");
        imageSearchView.clearMap();
        imageSearchView.displayAoI(aoi);
        imageSearchView.displayLoadingResults("Searching products...");
        enableUpdate(false);
        try {
            REST.withCallback(new MethodCallback<List<ImageProductDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<ImageProductDTO> imageProductDTOs) {
                    imageSearchView.hideLoadingResults();
                    // add all results to the interface
                    imageSearchView.displayImageProducts(imageProductDTOs);
                }
            }).call(ServicesUtil.searchService).queryImages(searchRequest);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private String getSensorsFilter(String sensors) {
        return sensors.startsWith("free") ? "SENTI*;LANDSAT*" : sensors;
    }

    @Override
    public void aoiChanged(AoI aoi) {
        this.aoi = aoi;
        enableUpdateMaybe();
    }

    private void enableUpdateMaybe() {
        if(aoi == null) {
            MaterialToast.fireToast("Please select AoI");
            enableUpdate(false);
            return;
        }
        if(sensors == null || sensors.length() == 0) {
            MaterialToast.fireToast("Please select sensors");
            enableUpdate(false);
            return;
        }
        enableUpdate(true);
    }

    @Override
    public void onProviderChanged(ImageryService imageryService) {
        updateSearch();
    }

    @Override
    public void onStartDateChanged(Date value) {
        startDate = value;
        enableUpdateMaybe();
    }

    private void enableUpdate(boolean enable) {
        imageSearchView.enableUpdate(enable);
    }

    @Override
    public void onStopDateChanged(Date value) {
        stopDate = value;
        enableUpdateMaybe();
    }

    @Override
    public void onSensorsChanged(String value) {
        sensors = value;
        enableUpdateMaybe();
    }

    public void setAoi(AoI aoi) {
        this.aoi = aoi;
        imageSearchView.displayAoI(aoi);
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
