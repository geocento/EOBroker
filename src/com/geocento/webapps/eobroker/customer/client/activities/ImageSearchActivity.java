package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.*;
import com.geocento.webapps.eobroker.customer.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ImageSearchView;
import com.geocento.webapps.eobroker.customer.shared.ImagesRequestDTO;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchActivity extends TemplateActivity implements ImageSearchView.Presenter {

    private ImageSearchView imageSearchView;
    private AoI aoi;
    private String sensors;
    private Date startDate;
    private Date stopDate;
    private String currency;
    private long lastCall = 0;

    public Suggestion selectedSuggestion;

    private List<ImageService> imageServices;

    private ImageService selectedImageService;

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
        setTemplateView(imageSearchView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        imageSearchView.showQuery();
        imageSearchView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                try {
                    REST.withCallback(new MethodCallback<List<ImageService>>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {

                        }

                        @Override
                        public void onSuccess(Method method, List<ImageService> imageServices) {
                            ImageSearchActivity.this.imageServices = imageServices;
                            imageSearchView.setSuppliers(imageServices);
                            selectService(imageServices.get(0));
                            handleHistory();
                        }
                    }).call(ServicesUtil.assetsService).getImageServices();
                } catch (RequestException e) {
                }
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        final String text = tokens.get(ImageSearchPlace.TOKENS.text.toString());
        Long productId = null;
        if (tokens.containsKey(ImageSearchPlace.TOKENS.product.toString())) {
            try {
                productId = Long.parseLong(tokens.get(ImageSearchPlace.TOKENS.product.toString()));
            } catch (Exception e) {

            }
        }
        Long aoiId = null;
        if (tokens.containsKey(ImageSearchPlace.TOKENS.aoiId.toString())) {
            try {
                aoiId = Long.parseLong(tokens.get(ImageSearchPlace.TOKENS.aoiId.toString()));
            } catch (Exception e) {
            }
        }
        imageSearchView.clearMap();
        setAoi(Customer.currentAoI);
        Date now = new Date();
        setStartDate(new Date(now.getTime() - 10 * 24 * 3600 * 1000));
        setStopDate(now);
        setCurrency("EUR");
        if(productId != null) {
            try {
                REST.withCallback(new MethodCallback<ProductDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, ProductDTO productDTO) {
                        setSensors("Suitable for '" + productDTO.getName() + "'");
                        enableUpdateMaybe();
                        if(aoi != null && text != null && text.length() > 0) {
                            updateSearch();
                        }
                    }
                }).call(ServicesUtil.assetsService).getProduct(productId);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } else {
            setSensors(text);
            enableUpdateMaybe();
            if(aoi != null && text != null && text.length() > 0) {
                updateSearch();
            }
        }
    }

    private void selectService(ImageService imageService) {
        this.selectedImageService = imageService;
        imageSearchView.displayService(imageService);
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

    @Override
    protected void bind() {
        super.bind();

        handlers.add(imageSearchView.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateSearch();
            }
        }));

        handlers.add(imageSearchView.getQuoteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                List<Product> products = imageSearchView.getSelectedProducts();
                ImagesRequestDTO imagesRequestDTO = new ImagesRequestDTO();
                imagesRequestDTO.setImageServiceId(selectedImageService.getId());
                imagesRequestDTO.setAoiWKT(AoIUtil.toWKT(aoi));
                imagesRequestDTO.setProducts(products);
                try {
                    REST.withCallback(new MethodCallback<RequestDTO>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            Window.alert("Could not save image request");
                        }

                        @Override
                        public void onSuccess(Method method, RequestDTO imageOrder) {
                            activityEventBus.fireEvent(new RequestCreated(imageOrder));
                            // clean the selections
                            imageSearchView.clearProductsSelection();
                            imageSearchView.displaySuccess("Quotation request submitted");
                        }
                    }).call(ServicesUtil.ordersService).submitImagesRequest(imagesRequestDTO);
                } catch (RequestException e) {
                }
            }
        }));

        activityEventBus.addHandler(TextSelected.TYPE, new TextSelectedHandler() {
            @Override
            public void onTextSelected(TextSelected event) {
                // TODO - correct the input if needed
                imageSearchView.setText(event.getText());
                enableUpdateMaybe();
            }
        });
        
        activityEventBus.addHandler(SuggestionSelected.TYPE, new SuggestionSelectedHandler() {

            @Override
            public void onSuggestionSelected(SuggestionSelected event) {
                imageSearchView.setText(event.getSuggestion().getName());
                selectedSuggestion = event.getSuggestion();
                imageSearchView.setSearchTextValid(true);
                enableUpdateMaybe();
            }
        });
    }

    private void updateSuggestions() {
        this.lastCall++;
        final long lastCall = this.lastCall;

        if(sensors != null && sensors.length() > 0) {
            REST.withCallback(new MethodCallback<List<Suggestion>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<Suggestion> response) {
                    // show only if last one to be called
                    if (lastCall == ImageSearchActivity.this.lastCall) {
                        imageSearchView.displaySensorSuggestions(response);
                    }
                }
            }).call(ServicesUtil.searchService).completeSensors(sensors);
        }
    }

    private void updateSearch() {
        // check current suggestion
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setAoiWKT("POLYGON((" + ((AoIPolygon) aoi).getWktRings() + "))");
        if(selectedSuggestion != null) {
            if(selectedSuggestion.getCategory() == Category.imagery) {
                searchQuery.setSensors(selectedSuggestion.getName());
            } else {
                searchQuery.setProduct(Long.parseLong(selectedSuggestion.getUri().replace("product::", "")));
            }
        } else {
            searchQuery.setSensors(sensors);
        }
        searchQuery.setStart((long) (startDate.getTime() / 1000.0));
        searchQuery.setStop((long) (stopDate.getTime() / 1000.0));
        imageSearchView.clearMap();
        imageSearchView.displayAoI(aoi);
        imageSearchView.displayLoadingResults("Searching products...");
        enableUpdate(false);
        try {
            REST.withCallback(new MethodCallback<List<Product>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<Product> imageProductDTOs) {
                    imageSearchView.hideLoadingResults();
                    // add all results to the interface
                    imageSearchView.displayImageProducts(imageProductDTOs);
                }
            }).call(ServicesUtil.searchService).queryImages(searchQuery);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void aoiChanged(AoI aoi) {
        this.aoi = aoi;
        Customer.setAoI(aoi);
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
/*
        if(selectedSuggestion == null) {
            enableUpdate(false);
            return;
        }
*/
        enableUpdate(true);
    }

    @Override
    public void onProviderChanged(ImageService imageService) {
        selectService(imageService);
/*
        updateSearch();
*/
        enableUpdateMaybe();
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
        selectedSuggestion = null;
        imageSearchView.setSearchTextValid(false);
        updateSuggestions();
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
