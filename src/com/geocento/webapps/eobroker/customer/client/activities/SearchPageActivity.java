package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.events.RequestImagery;
import com.geocento.webapps.eobroker.customer.client.events.RequestImageryHandler;
import com.geocento.webapps.eobroker.customer.client.events.SearchImagery;
import com.geocento.webapps.eobroker.customer.client.events.SearchImageryHandler;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.SearchPageView;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
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
public class SearchPageActivity extends TemplateActivity implements SearchPageView.Presenter {

    private SearchPageView searchPageView;
    private String text;
    private int start;
    private int limit;

    private int lastCall = 0;
    private String previousSearch = "";

    public SearchPageActivity(SearchPagePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        searchPageView = clientFactory.getSearchPageView();
        searchPageView.setPresenter(this);
        setTemplateView(searchPageView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(RequestImagery.TYPE, new RequestImageryHandler() {
            @Override
            public void onRequestImagery(RequestImagery event) {
                clientFactory.getPlaceController().goTo(new RequestImageryPlace(place.getToken()));
            }
        });

        activityEventBus.addHandler(SearchImagery.TYPE, new SearchImageryHandler() {
            @Override
            public void onSearchImagery(SearchImagery event) {
                clientFactory.getPlaceController().goTo(new ImageSearchPlace(place.getToken()));
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        Long aoiId = null;
        if(tokens.containsKey(SearchPagePlace.TOKENS.aoiId.toString())) {
            try {
                aoiId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.aoiId.toString()));
            } catch (Exception e) {
            }
        }
        Category category = null;
        if(tokens.containsKey(SearchPagePlace.TOKENS.category.toString())) {
            try {
                category = Category.valueOf(tokens.get(SearchPagePlace.TOKENS.category.toString()));
            } catch (Exception e) {

            }
        }
        String text = "";
        if(tokens.containsKey(SearchPagePlace.TOKENS.text.toString())) {
            text = tokens.get(SearchPagePlace.TOKENS.text.toString());
        }
        this.start = 0;
        this.limit = 24;
        // now start the search
        setSearchText(text, true);
        selectMenu(category);
        if(category == null) {
            searchPageView.displayFilters(null);
        } else {
            searchPageView.displayFilters(category);
            String filterText =  text.length() > 0 ? " with text '" + text + "'" : "";
            switch (category) {
                case products:
                    searchPageView.setFilterTitle("Products" + filterText);
                    break;
                case productservices:
                    searchPageView.setFilterTitle("Services" + filterText);
                    break;
                case productdatasets:
                    searchPageView.setFilterTitle("Off the shelf data" + filterText);
                    break;
                case software:
                    searchPageView.setFilterTitle("Software solutions" + filterText);
                    break;
                case project:
                    searchPageView.setFilterTitle("Projects" + filterText);
                    break;
                case companies:
                    searchPageView.setFilterTitle("Companies" + filterText);
                    break;
            }
        }

        // add current AoI
        searchPageView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                searchPageView.displayAoI(currentAoI);
                if(currentAoI != null) {
                    searchPageView.centerOnAoI();
                }
            }
        });

        updateSearchResults();

    }

    public void selectMenu(Category category) {
        super.selectMenu(category == null ? null : category.toString());
        this.category = category;
    }

    @Override
    public void setSearchText(String text, boolean forceFocus) {
        this.text = text;
        super.setSearchText(text, forceFocus);
    }

    private void updateSearchResults() {
        searchPageView.clearResults();
        if(category != null) {
            // search using the category
            searchPageView.showFilters(true);
            searchPageView.setResultsTitle("");
            switch (category) {
                case products: {
                    setTitleText("Browse products");
                    loadProducts(text, start, limit);
                } break;
                case productservices: {
                    setTitleText("Browse on-demand services");
                    loadProductServices(text, start, limit);
                } break;
                case productdatasets: {
                    setTitleText("Browse off-the-shelf data");
                    loadProductDatasets(text, start, limit);
                } break;
                case software: {
                    setTitleText("Browse software solutions");
                    loadSoftware(text, start, limit);
                } break;
                case project: {
                    setTitleText("Browse projects");
                    loadProjects(text, start, limit);
                } break;
                case companies: {
                    setTitleText("Browse companies");
                    searchPageView.displayLoadingResults("Loading companies...");
                    loadCompanies(text, start, limit);
                } break;
            }
        } else if(text != null) {
            setTitleText("Search Results");
            try {
                searchPageView.displayLoadingResults("Searching matching results...");
                boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
                REST.withCallback(new MethodCallback<SearchResult>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert("Error");
                    }

                    @Override
                    public void onSuccess(Method method, SearchResult searchResult) {
                        searchPageView.hideLoadingResults();
                        searchPageView.setResultsTitle("");
                        searchPageView.showFilters(false);
                        // add all results to the interface
                        // start with products
                        {
                            List<ProductDTO> products = searchResult.getProducts();
                            boolean more = searchResult.isMoreProducts();
                            String moreUrl = more ? getSearchCategoryUrl(Category.products, text) : null;
                            if (more) {
                                products = products.subList(0, 4);
                            }
                            searchPageView.setMatchingProducts(products, moreUrl);
                        }
                        // add on demand services
                        {
                            List<ProductServiceDTO> productServiceDTOs = searchResult.getProductServices();
                            boolean more = searchResult.isMoreProductServices();
                            String moreUrl = more ? getSearchCategoryUrl(Category.productservices, text) : null;
                            if (more) {
                                productServiceDTOs = productServiceDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingServices(productServiceDTOs, moreUrl);
                        }
                        {
                            List<ProductDatasetDTO> productDatasetDTOs = searchResult.getProductDatasets();
                            boolean more = searchResult.isMoreProductDatasets();
                            String moreUrl = more ? getSearchCategoryUrl(Category.productdatasets, text) : null;
                            if (more) {
                                productDatasetDTOs = productDatasetDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingDatasets(productDatasetDTOs, moreUrl);
                        }
                        {
                            List<SoftwareDTO> softwareDTOs = searchResult.getSoftwares();
                            boolean more = searchResult.isMoreSoftware();
                            String moreUrl = more ? getSearchCategoryUrl(Category.software, text) : null;
                            if (more) {
                                softwareDTOs = softwareDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingSoftwares(softwareDTOs, moreUrl);
                        }
                        {
                            List<ProjectDTO> projectDTOs = searchResult.getProjects();
                            boolean more = searchResult.isMoreProjects();
                            String moreUrl = more ? getSearchCategoryUrl(Category.project, text) : null;
                            if (more) {
                                projectDTOs = projectDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingProjects(projectDTOs, moreUrl);
                        }
                    }
                }).call(ServicesUtil.searchService).getMatchingServices(text);
            } catch (RequestException e) {
            }
        }
        // forces title text to explore instead
        setTitleText("Explore");
    }

    private void loadProducts(final String text, final int start, final int limit) {
        try {
            searchPageView.displayLoadingResults("Loading products...");
            boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
            Sector sector = searchPageView.getSectorFilter();
            Thematic thematic = searchPageView.getThematicFilter();
            REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<ProductDTO> products) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addProducts(products, start, products != null && products.size() != 0 && products.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listProducts(text, start, limit, filterByAoI ? currentAoI.getId() : null, sector, thematic);
        } catch (RequestException e) {
        }
    }

    private void loadProductServices(final String text, final int start, final int limit) {
        boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
        ProductDTO product = searchPageView.getProductSelection();
        CompanyDTO company = searchPageView.getCompanySelection();
        String searchHash = generateSearchHash("productservices" + text, start, limit,
                null,
                filterByAoI ? AoIUtil.toWKT(currentAoI) : null,
                null,
                null, null,
                company != null ? company.getId() : null,
                product != null ? product.getId() : null);
        if(!previousSearch.contentEquals(searchHash)) {
            searchPageView.displayLoadingResults("Loading services...");
            try {
                REST.withCallback(new MethodCallback<List<ProductServiceDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert("Error");
                    }

                    @Override
                    public void onSuccess(Method method, List<ProductServiceDTO> products) {
                        searchPageView.hideLoadingResults();
                        // add all results to the interface
                        searchPageView.addProductServices(products, start, products != null && products.size() != 0 && products.size() % limit == 0, text);
                    }
                }).call(ServicesUtil.searchService).listProductServices(text, start, limit,
                        null,
                        filterByAoI ? AoIUtil.toWKT(currentAoI) : null,
                        company != null ? company.getId() : null,
                        product != null ? product.getId() : null
                );
            } catch (RequestException e) {
            }
            previousSearch = searchHash;
        }
    }

    private void loadProductDatasets(final String text, final int start, final int limit) {
        boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
        Date startTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStartTimeFrameFilter().getValue() : null;
        Date stopTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStopTimeFrameFilter().getValue() : null;
        ServiceType serviceType = searchPageView.getProductServiceType();
        ProductDTO product = searchPageView.getProductSelection();
        CompanyDTO company = searchPageView.getCompanySelection();
        searchPageView.displayLoadingResults("Loading data...");
        try {
            REST.withCallback(new MethodCallback<List<ProductDatasetDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<ProductDatasetDTO> products) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addProductDatasets(products, start, products != null && products.size() != 0 && products.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listProductDatasets(text, start, limit,
                    null,
                    filterByAoI ? AoIUtil.toWKT(currentAoI) : null,
                    serviceType,
                    startTimeFrame == null ? null : startTimeFrame.getTime() / 1000, stopTimeFrame == null ? null : stopTimeFrame.getTime() / 1000,
                    company != null ? company.getId() : null,
                    product != null ? product.getId() : null
            );
        } catch (RequestException e) {
        }
    }

    private String generateSearchHash(String text, int start, int limit, Long aoiId, String aoiWKT, ServiceType serviceType, Long startTime, Long stopTime, Long companyId, Long productId) {
        return StringUtils.join(new String[] {text, start + "", limit + "", aoiId + "", aoiWKT, serviceType + "", startTime + "", stopTime + "", companyId + "", productId + ""}, "_");
    }

    private void loadSoftware(final String text, final int start, final int limit) {
        try {
            searchPageView.displayLoadingResults("Loading software...");
            boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
            REST.withCallback(new MethodCallback<List<SoftwareDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<SoftwareDTO> softwareDTOs) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addSoftware(softwareDTOs, start, softwareDTOs != null && softwareDTOs.size() != 0 && softwareDTOs.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listSoftware(text, start, limit, filterByAoI ? currentAoI.getId() : null,
                    searchPageView.getSoftwareType());
        } catch (RequestException e) {
        }
    }

    private void loadProjects(final String text, final int start, final int limit) {
        try {
            searchPageView.displayLoadingResults("Loading projects...");
            boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
            REST.withCallback(new MethodCallback<List<ProjectDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<ProjectDTO> projectDTOs) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addProjects(projectDTOs, start, projectDTOs != null && projectDTOs.size() != 0 && projectDTOs.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listProjects(text, start, limit, filterByAoI ? currentAoI.getId() : null);
        } catch (RequestException e) {
        }
    }

    private void loadCompanies(final String text, final int start, final int limit) {
        try {
            COMPANY_SIZE companySize = searchPageView.getCompanySizeFilter();
            int minYears = searchPageView.getCompanyAgeFilter();
            String countryCode = searchPageView.getCompanyCountryFilter();
            REST.withCallback(new MethodCallback<List<CompanyDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<CompanyDTO> companyDTOs) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addCompanies(companyDTOs, start,
                            companyDTOs != null && companyDTOs.size() != 0 && companyDTOs.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listCompanies(text, start, limit,
                    currentAoI == null ? null : currentAoI.getId(), companySize, minYears, countryCode);
        } catch (RequestException e) {
        }
    }

    private String getSearchCategoryUrl(Category category, String text) {
        return "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), category.toString(),
                SearchPagePlace.TOKENS.text.toString(), text == null ? "" : text
        )));
    }

    @Override
    public void textChanged(String text) {
        this.text = text;
        updateSuggestions();
    }

    private void updateSuggestions() {
        this.lastCall++;
        final long lastCall = this.lastCall;

        if(text != null && text.length() > 0) {
            REST.withCallback(new MethodCallback<List<Suggestion>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<Suggestion> response) {
                    // show only if last one to be called
                    if (lastCall == SearchPageActivity.this.lastCall) {
                        displayListSuggestions(response);
                    }
                }
            }).call(ServicesUtil.searchService).complete(text, category, toWkt(currentAoI));
        } else {
/*
            // TODO - move to the server side
            List<Suggestion> suggestions = new ArrayList<Suggestion>();
            for(Category suggestionCategory : Category.values()) {
                suggestions.addAll(getSuggestion(suggestionCategory));
            }
            displayListSuggestions(suggestions);
*/
        }
    }

    private String toWkt(AoIDTO aoi) {
        return aoi.getWktGeometry();
    }

    private List<Suggestion> getSuggestion(Category category) {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        switch (category) {
            case products:
                suggestions.add(new Suggestion("Browse products", Category.products, "browse::"));
                break;
            case companies:
                suggestions.add(new Suggestion("Browse companies", Category.companies, "browse::"));
                break;
            case imagery:
                suggestions.add(new Suggestion("Search for imagery", Category.imagery, "search::"));
                suggestions.add(new Suggestion("Request quotation for imagery", Category.imagery, "request::"));
                break;
        }
        return suggestions;
    }

    @Override
    public void suggestionSelected(Suggestion suggestion) {
        // TODO - move to a helper class
        String uri = suggestion.getUri();
        String action = uri.split("::")[0];
        String parameters = uri.split("::")[1];
        if (parameters == null) {
            parameters = "";
        }
        EOBrokerPlace searchPlace = null;
        switch (suggestion.getCategory()) {
            case imagery: {
                if (action.contentEquals("search")) {
                    searchPlace = new ImageSearchPlace(ImageSearchPlace.TOKENS.text.toString() + "=" + parameters +
/*
                            (currentAoI == null ? "" : "&" + ImageSearchPlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId())
*/
                            "");
                    //setText(parameters);
                } else if (action.contentEquals("request")) {
                    searchPlace = new RequestImageryPlace(parameters);
                    //setText("");
                }
                ;
            } break;
            case products: {
                String token = "";
                Category category = suggestion.getCategory();
                if (action.contentEquals("product")) {
                    searchPlace = new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + parameters);
                } else if (action.contentEquals("browse")) {
                    token += SearchPagePlace.TOKENS.category.toString() + "=" + Category.products.toString();
                    if (category != null) {
                        token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    }
/*
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
*/
                    searchPlace = new SearchPagePlace(token);
                } else {
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    if (category != null) {
                        token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    }
/*
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
*/
                    searchPlace = new SearchPagePlace(token);
                }
            } break;
            case companies: {
                String token = "";
                Category category = suggestion.getCategory();
                if (action.contentEquals("company")) {
                    searchPlace = new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + parameters);
                } else if (action.contentEquals("browse")) {
                    token += SearchPagePlace.TOKENS.category.toString() + "=" + Category.companies.toString();
                    if (category != null) {
                        token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    }
/*
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
*/
                    searchPlace = new SearchPagePlace(token);
                } else {
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    if (category != null) {
                        token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    }
/*
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
*/
                    searchPlace = new SearchPagePlace(token);
                }
            } break;
        }
        if (searchPlace != null) {
            clientFactory.getPlaceController().goTo(searchPlace);
        } else {
            displaySearchError("Sorry I could not understand your request...");
        }
    }

    @Override
    public void textSelected(String text) {
        this.text = text;
/*
        if(text.trim().length() == 0) {
            return;
        }
*/
        EOBrokerPlace eoBrokerPlace = null;
        if(category == null) {
            // go to general search results page
            String token = "";
            token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
            if(category != null) {
                token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
            }
/*
            if(currentAoI != null) {
                token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
            }
*/
            eoBrokerPlace = new SearchPagePlace(token);
        } else {
            switch (category) {
                case imagery:
                    eoBrokerPlace = new ImageSearchPlace(ImageSearchPlace.TOKENS.text.toString() + "=" + text +
/*
                            (currentAoI == null ? "" : "&" + ImageSearchPlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId())
*/
                            ""
                    );
                    break;
                case products:
                case companies:
                case productservices:
                case productdatasets:
                case software:
                    // go to general search results page
                    String token = "";
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
/*
                    if(currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
*/
                    eoBrokerPlace = new SearchPagePlace(token);
            }
        }
        if(eoBrokerPlace != null) {
            clientFactory.getPlaceController().goTo(eoBrokerPlace);
        } else {
            displaySearchError("Sorry I could not understand your request...");
        }
    }

    @Override
    public void loadMoreProducts() {
        start += limit;
        loadProducts(text, start, limit);
    }

    @Override
    public void loadMoreProductServices() {
        start += limit;
        loadProductServices(text, start, limit);
    }

    @Override
    public void loadMoreProductDatasets() {
        start += limit;
        loadProductDatasets(text, start, limit);
    }

    @Override
    public void loadMoreSofware() {
        start += limit;
        loadSoftware(text, start, limit);
    }

    @Override
    public void loadMoreProjects() {
        start += limit;
        loadProjects(text, start, limit);
    }

    @Override
    public void aoiChanged(final AoIDTO aoi) {
        setAoI(aoi);
        if(searchPageView.getFilterByAoI().getValue()) {
            filtersChanged();
        }
    }

    @Override
    public void filtersChanged() {
        // filters have changed so update results based on new filters
        start = 0;
        updateSearchResults();
    }

    @Override
    public void aoiSelected(AoIDTO aoi) {
        setAoI(aoi);
        if(searchPageView.getFilterByAoI().getValue()) {
            filtersChanged();
        }
    }

    public void setAoI(AoIDTO aoIDTO) {
        super.setAoI(aoIDTO);
        searchPageView.displayAoI(aoIDTO);
    }

}
