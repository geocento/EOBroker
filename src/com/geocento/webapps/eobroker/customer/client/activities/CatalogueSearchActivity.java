package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.utils.opensearch.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.IntegerFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.TextFormElement;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.CatalogueSearchPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class CatalogueSearchActivity extends TemplateActivity implements CatalogueSearchView.Presenter {

    private CatalogueSearchView catalogueSearchView;
    private String query;
    private Date startDate;
    private Date stopDate;

    private ProductDatasetCatalogueDTO productDatasetCatalogueDTO;

    // the different search points possible
    private OpenSearchDescription openSearchDescription;

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
        displayFullLoading("Loading resources...");
        catalogueSearchView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                hideFullLoading();
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                hideFullLoading();
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
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
        final String text = tokens.get(CatalogueSearchPlace.TOKENS.text.toString());
        setQueryText(text);
        Date startDate = null;
        if(tokens.containsKey(CatalogueSearchPlace.TOKENS.start.toString())) {
            try {
                startDate = new Date(Long.parseLong(tokens.get(CatalogueSearchPlace.TOKENS.start.toString())));
            } catch (Exception e) {

            }
        }
        Date stopDate = null;
        if(tokens.containsKey(CatalogueSearchPlace.TOKENS.stop.toString())) {
            try {
                stopDate = new Date(Long.parseLong(tokens.get(CatalogueSearchPlace.TOKENS.stop.toString())));
            } catch (Exception e) {

            }
        }
        if(startDate == null && stopDate == null) {
            Date now = new Date();
            setStartDate(new Date(now.getTime() - 10 * 24 * 3600 * 1000));
            setStopDate(now);
        } else {
            setStartDate(startDate);
            setStopDate(stopDate);
        }
        catalogueSearchView.clearMap();
        setAoi(currentAoI);
        catalogueSearchView.centerOnAoI();
        if(productId != null) {
            displayFullLoading("Loading product dataset...");
            try {
                REST.withCallback(new MethodCallback<ProductDatasetCatalogueDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Could not find product dataset catalogue");
                    }

                    @Override
                    public void onSuccess(Method method, ProductDatasetCatalogueDTO productDatasetCatalogueDTO) {
                        hideLoading();
                        CatalogueSearchActivity.this.productDatasetCatalogueDTO = productDatasetCatalogueDTO;
                        catalogueSearchView.setProductDatasetCatalogDTO(productDatasetCatalogueDTO);
                        // now load the description
                        switch (productDatasetCatalogueDTO.getDatasetStandard()) {
                            case OpenSearch:
                                displayFullLoading("Loading catalogue service...");
                                OpenSearchUtils.getDescription(productDatasetCatalogueDTO.getDatasetURL(), new AsyncCallback<OpenSearchDescription>() {
                                    @Override
                                    public void onFailure(Throwable caught) {
                                        hideFullLoading();
                                        Window.alert("Could not reach open search service");
                                    }

                                    @Override
                                    public void onSuccess(OpenSearchDescription result) {
                                        hideFullLoading();
                                        CatalogueSearchActivity.this.openSearchDescription = result;
                                        // TODO - configure view to reflect the open search interface
                                        List<Parameter> parameters = openSearchDescription.getUrl().get(0).getParameters();
                                        parameters = ListUtil.filterValues(parameters, new ListUtil.CheckValue<Parameter>() {
                                            @Override
                                            public boolean isValue(Parameter value) {
                                                return !value.isReserved();
                                            }
                                        });
                                        List<FormElement> formElements = new ArrayList<FormElement>();
                                        for(Parameter parameter : parameters) {
                                            FormElement formElement = null;
                                            switch (parameter.getFieldType()) {
                                                case "integer": {
                                                    IntegerFormElement integerFormElement = new IntegerFormElement();
                                                    formElement = integerFormElement;
                                                } break;
                                                case "string": {
                                                    TextFormElement textFormElement = new TextFormElement();
                                                    textFormElement.setPattern(parameter.getPattern());
                                                    formElement = textFormElement;
                                                } break;
                                                default:
                                                    break;
                                            }
                                            if(formElement != null) {
                                                formElement.setName(parameter.getName());
                                                formElement.setDescription(parameter.getTitle());
                                                formElements.add(formElement);
                                            }
                                        }
                                        catalogueSearchView.setParameters(formElements);
                                    }
                                });
                                break;
                            case CSW:
                                displayLoading();
/*
                                CSWUtils.getDescription(productDatasetCatalogueDTO.getDatasetURL(), new AsyncCallback<OpenSearchDescription>() {
                                    @Override
                                    public void onFailure(Throwable caught) {
                                        hideLoading();
                                        Window.alert("Could not reach open search service");
                                    }

                                    @Override
                                    public void onSuccess(OpenSearchDescription result) {
                                        hideLoading();
                                        CatalogueSearchActivity.this.openSearchDescription = result;
                                        // TODO - configure view to reflect the open search interface
                                        List<Parameter> parameters = OpenSearchUtils.getSupportedParameters(openSearchDescription.getUrl().get(0).getTemplate());
                                        List<FormElement> formElements = new ArrayList<FormElement>();
                                        for (Parameter parameter : parameters) {
                                            switch (parameter.getType()) {
                                                case "integer":
                                                    IntegerFormElement integerFormElement = new IntegerFormElement();
                                                    integerFormElement.setName(parameter.getName());
                                                    formElements.add(integerFormElement);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        catalogueSearchView.setParameters(formElements);
                                    }
                                });
*/
                                break;
                        }

                    }
                }).call(ServicesUtil.assetsService).getProductDatasetCatalogueDTO(productId);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        }
    }

    private void setQueryText(String text) {
        query = text;
        catalogueSearchView.getQuery().setText(text);
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
/*
        if(query == null || query.length() == 0) {
            MaterialToast.fireToast("Please provide a query");
            return;
        }
*/
        if(query == null) {
            query = "";
        }
        catalogueSearchView.clearMap();
        catalogueSearchView.displayAoI(currentAoI);
        catalogueSearchView.displayLoadingResults("Searching...");
        enableUpdate(false);
        // TODO - save search parameters in URL or on the server
        History.newItem(PlaceHistoryHelper.convertPlace(new CatalogueSearchPlace(Utils.generateTokens(
                CatalogueSearchPlace.TOKENS.productId.toString(), productDatasetCatalogueDTO.getId() + "",
                CatalogueSearchPlace.TOKENS.text.toString(), query,
                CatalogueSearchPlace.TOKENS.start.toString(), startDate.getTime() + "",
                CatalogueSearchPlace.TOKENS.stop.toString(), stopDate.getTime() + ""
        ))), false);
        search(0, 0);
    }

    private void search(int start, int startPage) {
        // check standard used
        switch (productDatasetCatalogueDTO.getDatasetStandard()) {
            case OpenSearch:
                try {
                    OpenSearchUtils.getRecords(start, startPage, openSearchDescription, currentAoI.getWktGeometry(), startDate, stopDate, query, new AsyncCallback<SearchResponse>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            catalogueSearchView.hideLoadingResults();
                            displayError("Problem running query...");
                        }

                        @Override
                        public void onSuccess(SearchResponse result) {
                            catalogueSearchView.hideLoadingResults();
                            catalogueSearchView.displayQueryResponse(result);
                        }
                    });
                } catch (Exception e) {
                    catalogueSearchView.hideLoadingResults();
                    displayError("Problem running query...");
                }
                break;
        }
    }

    private void searchOpenSearch(OpenSearchDescription openSearchDescription, String wktGeometry, Date startDate, Date stopDate, String query) throws Exception {
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

    @Override
    public void onRecordRangeChanged(int start, int pageStart) {
        search(start, pageStart);
    }

    public void setAoi(AoIDTO aoi) {
        super.setAoI(aoi);
        catalogueSearchView.displayAoI(aoi);
    }

}
