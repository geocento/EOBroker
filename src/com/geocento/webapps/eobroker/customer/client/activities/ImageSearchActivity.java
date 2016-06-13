package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.customer.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ImageSearchView;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchActivity extends AbstractApplicationActivity implements ImageSearchView.Presenter {

    private ImageSearchView imageSearchView;

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
        if(tokens.containsKey(SearchPagePlace.TOKENS.product.toString())) {
            try {
                productId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.product.toString()));
            } catch (Exception e) {

            }
        }
        String browse = tokens.get(SearchPagePlace.TOKENS.browse.toString());
        Long aoiId = null;
        if(tokens.containsKey(SearchPagePlace.TOKENS.aoiId.toString())) {
            try {
                aoiId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.aoiId.toString()));
            } catch (Exception e) {
            }
        }
        imageSearchView.displaySupplier("Geocento", "http://geocento.com/wp-content/uploads/2016/03/logo-geocento-global-earth-imaging.jpg");
        AoIPolygon aoIPolygon = new AoIPolygon();
        aoIPolygon.setWktRings("-2.4609375 56.87749838693283,0.439453125 58.6583707283785,3.076171875 57.25973712933438,3.251953125 55.5581558834549,0.087890625 55.10822970202758,-2.4609375 55.805911967706635,-2.4609375 56.87749838693283");
        imageSearchView.displayAoI(aoIPolygon);
        imageSearchView.setText("Free imagery");
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setAoiWKT("POLYGON((" + aoIPolygon.getWktRings() + "))");
        searchRequest.setSensors("SENTI*");
        Date now = new Date();
        searchRequest.setStart(new Date(now.getTime() - 10 * 24 * 3600 * 1000).getTime());
        searchRequest.setStop(now.getTime());
        searchRequest.setCurrency("EUR");
        imageSearchView.displayLoadingResults("Searching products...");
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

    @Override
    protected void bind() {
    }

    @Override
    public void aoiChanged(AoI aoi) {

    }
}
