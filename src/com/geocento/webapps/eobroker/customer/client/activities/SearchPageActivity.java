package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
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

        Category category = null;
        if(tokens.containsKey(SearchPagePlace.TOKENS.category.toString())) {
            try {
                category = Category.valueOf(tokens.get(SearchPagePlace.TOKENS.category.toString()));
            } catch (Exception e) {

            }
        }

        if(text == null) {
            text = "";
        }
        if(tokens.containsKey(SearchPagePlace.TOKENS.text.toString())) {
            text = tokens.get(SearchPagePlace.TOKENS.text.toString());
        }
        setSearchText(text, text.length() > 0);
        this.start = 0;
        this.limit = 24;
        // now start the search
        selectMenu(category);
        if(category == null) {
            searchPageView.displayFilters(null);
        } else {
            searchPageView.displayFilters(category);
            String filterText =  text.length() > 0 ? " with text '" + text + "'" : "";
            switch (category) {
                case products:
                    searchPageView.setFilterTitle("Product categories" + filterText);
                    break;
                case productservices:
                    searchPageView.setFilterTitle("Bespoke services" + filterText);
                    break;
                case productdatasets:
                    searchPageView.setFilterTitle("Off the shelf products" + filterText);
                    break;
                case software:
                    searchPageView.setFilterTitle("Software solutions" + filterText);
                    break;
                case project:
                    searchPageView.setFilterTitle("R&D Projects" + filterText);
                    break;
                case companies:
                    searchPageView.setFilterTitle("Companies" + filterText);
                    break;
            }
        }
        // set the filters
        Long aoiId = null;
        if(tokens.containsKey(SearchPagePlace.TOKENS.aoiId.toString())) {
            try {
                aoiId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.aoiId.toString()));
            } catch (Exception e) {
            }
        }
        Long productId = null;
        // generally the filters need to be passed into the URL and the view updated accordingly
        if(tokens.containsKey(SearchPagePlace.TOKENS.product.toString())) {
            try {
                productId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.product.toString()));
            } catch (Exception e) {

            }
        }
        Long companyId = null;
        if(tokens.containsKey(SearchPagePlace.TOKENS.company.toString())) {
            try {
                companyId = Long.parseLong(tokens.get(SearchPagePlace.TOKENS.company.toString()));
            } catch (Exception e) {

            }
        }
        // if we have filters, we need to load the filters first
        if(aoiId != null || productId != null || companyId != null) {
            displayFullLoading("Loading filters...");
            final Long finalAoiId = aoiId;
            final Long finalProductId = productId;
            final Long finalCompanyId = companyId;
            new Runnable() {

                int toLoad =
                        (finalAoiId == null ? 0 : 1) +
                        (finalProductId == null ? 0 : 1) +
                        (finalCompanyId == null ? 0 : 1)
                        ;

                @Override
                public void run() {
                    if(finalAoiId != null) {
                        try {
                            REST.withCallback(new MethodCallback<AoIDTO>() {
                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                    searchPageView.enableAoiFilter(false);
                                    toLoad--;
                                    checkCompleted();
                                }

                                @Override
                                public void onSuccess(Method method, AoIDTO aoIDTO) {
                                    setAoI(aoIDTO);
                                    searchPageView.enableAoiFilter(true);
                                    toLoad--;
                                    checkCompleted();
                                }
                            }).call(ServicesUtil.assetsService).getAoI(finalAoiId);
                        } catch (Exception e) {

                        }
                    } else {
                        searchPageView.enableAoiFilter(false);
                    }
                    if(finalProductId != null) {
                        try {
                            REST.withCallback(new MethodCallback<ProductDTO>() {
                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                    searchPageView.setProductSelection(null);
                                    toLoad--;
                                    checkCompleted();
                                }

                                @Override
                                public void onSuccess(Method method, ProductDTO productDTO) {
                                    searchPageView.setProductSelection(productDTO);
                                    toLoad--;
                                    checkCompleted();
                                }
                            }).call(ServicesUtil.assetsService).getProduct(finalProductId);
                        } catch (Exception e) {

                        }
                    } else {
                        searchPageView.setProductSelection(null);
                    }
                    if(finalCompanyId != null) {
                        try {
                            REST.withCallback(new MethodCallback<CompanyDTO>() {
                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                    searchPageView.setCompanySelection(null);
                                    toLoad--;
                                    checkCompleted();
                                }

                                @Override
                                public void onSuccess(Method method, CompanyDTO companyDTO) {
                                    searchPageView.setCompanySelection(companyDTO);
                                    toLoad--;
                                    checkCompleted();
                                }
                            }).call(ServicesUtil.assetsService).getCompany(finalCompanyId);
                        } catch (Exception e) {

                        }
                    } else {
                        searchPageView.setProductSelection(null);
                    }
                }

                private void checkCompleted() {
                    if(toLoad == 0) {
                        hideFullLoading();
/*
                        searchPageView.expandFilters(true);
*/
                        updateSearchResults();
                    }
                }

            }.run();
        } else {
            updateSearchResults();
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
            // only do this for the first one
            switch (category) {
                case products: {
                    setTitleText("Browse product categories");
                    loadProducts(text, start, limit);
                } break;
                case productservices: {
                    setTitleText("Browse bespoke services");
                    loadProductServices(text, start, limit);
                } break;
                case productdatasets: {
                    setTitleText("Browse off-the-shelf products");
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
                    loadCompanies(text, start, limit);
                } break;
            }
        } else if(text != null) {
            setTitleText("Search Results");
            try {
                searchPageView.showFilters(false);
                searchPageView.displayLoadingResults("Searching matching results...");
                boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
                REST.withCallback(new MethodCallback<SearchResult>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        searchPageView.hideLoadingResults();
                        searchPageView.displaySearchError(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, SearchResult searchResult) {
                        searchPageView.hideLoadingResults();
                        searchPageView.setResultsTitle("");
                        // list of not founds
                        List<String> notFound = new ArrayList<String>();
                        // add all results to the interface
                        // start with off the shelf products
                        List<ProductDatasetDTO> productDatasetDTOs = searchResult.getProductDatasets();
                        if(productDatasetDTOs != null && productDatasetDTOs.size() > 0) {
                            boolean more = searchResult.isMoreProductDatasets();
                            String moreUrl = more ? getSearchCategoryUrl(Category.productdatasets, text) : null;
                            if (more) {
                                productDatasetDTOs = productDatasetDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingDatasets(productDatasetDTOs, moreUrl);
                        } else {
                            notFound.add("off the shelf products");
                        }
                        // add on demand services
                        List<ProductServiceDTO> productServiceDTOs = searchResult.getProductServices();
                        if(productServiceDTOs != null && productServiceDTOs.size() > 0) {
                            boolean more = searchResult.isMoreProductServices();
                            String moreUrl = more ? getSearchCategoryUrl(Category.productservices, text) : null;
                            if (more) {
                                productServiceDTOs = productServiceDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingServices(productServiceDTOs, moreUrl);
                        } else {
                            notFound.add("bespoke services");
                        }
                        List<SoftwareDTO> softwareDTOs = searchResult.getSoftwares();
                        if(softwareDTOs != null && softwareDTOs.size() > 0) {
                            boolean more = searchResult.isMoreSoftware();
                            String moreUrl = more ? getSearchCategoryUrl(Category.software, text) : null;
                            if (more) {
                                softwareDTOs = softwareDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingSoftwares(softwareDTOs, moreUrl);
                        } else {
                            notFound.add("software solutions");
                        }
                        List<ProjectDTO> projectDTOs = searchResult.getProjects();
                        if(projectDTOs != null && projectDTOs.size() > 0) {
                            boolean more = searchResult.isMoreProjects();
                            String moreUrl = more ? getSearchCategoryUrl(Category.project, text) : null;
                            if (more) {
                                projectDTOs = projectDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingProjects(projectDTOs, moreUrl);
                        } else {
                            notFound.add("R&D projects");
                        }
                        List<CompanyDTO> companyDTOs = searchResult.getCompanies();
                        if(companyDTOs != null && companyDTOs.size() > 0) {
                            boolean more = searchResult.isMoreCompanies();
                            String moreUrl = more ? getSearchCategoryUrl(Category.companies, text) : null;
                            if (more) {
                                companyDTOs = companyDTOs.subList(0, 4);
                            }
                            searchPageView.setMatchingCompanies(companyDTOs, moreUrl);
                        } else {
                            notFound.add("companies");
                        }
                        List<ProductDTO> products = searchResult.getProducts();
                        if(products != null && products.size() > 0) {
                            boolean more = searchResult.isMoreProducts();
                            String moreUrl = more ? getSearchCategoryUrl(Category.products, text) : null;
                            if (more) {
                                products = products.subList(0, 4);
                            }
                            searchPageView.setMatchingProducts(products, moreUrl);
                        } else {
                            notFound.add("product categories");
                        }
                        if(notFound.size() > 0) {
                            if(notFound.size() == 6) {
                                searchPageView.setMatchingComments("No items found for your search");
                            } else {
                                searchPageView.setMatchingComments("No " + com.geocento.webapps.eobroker.common.shared.utils.StringUtils.join(notFound, ", ") + " found for your search");
                            }
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
            boolean isChallenges = searchPageView.isChallengesSelected();
            if(isChallenges) {
                REST.withCallback(new MethodCallback<List<ChallengeDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        searchPageView.hideLoadingResults();
                        searchPageView.displaySearchError(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, List<ChallengeDTO> challengeDTOS) {
                        searchPageView.hideLoadingResults();
                        // add all results to the interface
                        searchPageView.addChallenges(challengeDTOS, start, challengeDTOS != null && challengeDTOS.size() != 0 && challengeDTOS.size() % limit == 0, text);
                    }
                }).call(ServicesUtil.searchService).listChallenges(text, start, limit, filterByAoI ? currentAoI.getId() : null);
            } else {
                ChallengeDTO challengeDTO = searchPageView.getChallengeSelection();
                Sector sector = searchPageView.getSectorFilter();
                Thematic thematic = searchPageView.getThematicFilter();
                REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        searchPageView.hideLoadingResults();
                        searchPageView.displaySearchError(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, List<ProductDTO> products) {
                        searchPageView.hideLoadingResults();
                        // add all results to the interface
                        searchPageView.addProducts(products, start, products != null && products.size() != 0 && products.size() % limit == 0, text);
                    }
                }).call(ServicesUtil.searchService).listProducts(text, start, limit, filterByAoI ? currentAoI.getId() : null,
                        challengeDTO == null ? null : challengeDTO.getId(), sector, thematic);
            }
        } catch (RequestException e) {
        }
    }

    private void loadProductServices(final String text, final int start, final int limit) {
        boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
        Date startTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStartTimeFrameFilter().getValue() : null;
        Date stopTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStopTimeFrameFilter().getValue() : null;
        ProductDTO product = searchPageView.getProductSelection();
        CompanyDTO company = searchPageView.getCompanySelection();
        boolean affiliatesOnly = searchPageView.getFilterByAffiliates().getValue();
        String searchHash = generateSearchHash("productservices" + text, start, limit,
                null,
                filterByAoI ? AoIUtil.toWKT(currentAoI) : null,
                null,
                null, null,
                affiliatesOnly,
                company != null ? company.getId() : null,
                product != null ? product.getId() : null);
        if(true) { //!previousSearch.contentEquals(searchHash)) {
            searchPageView.displayLoadingResults("Loading services...");
            try {
                REST.withCallback(new MethodCallback<List<ProductServiceDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        searchPageView.hideLoadingResults();
                        searchPageView.displaySearchError(method.getResponse().getText());
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
                        null, null, //startTimeFrame == null ? null : startTimeFrame.getTime() / 1000, stopTimeFrame == null ? null : stopTimeFrame.getTime() / 1000,
                        affiliatesOnly,
                        company != null ? company.getId() : null,
                        product != null ? product.getId() : null,
                        getSelectedFeatures(),
                        getSelectedPerformances()
                );
            } catch (RequestException e) {
            }
/*
            previousSearch = searchHash;
*/
        }
    }

    private void loadProductDatasets(final String text, final int start, final int limit) {
        boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
        Date startTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStartTimeFrameFilter().getValue() : null;
        Date stopTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStopTimeFrameFilter().getValue() : null;
        ServiceType serviceType = searchPageView.getProductServiceType();
        ProductDTO product = searchPageView.getProductSelection();
        CompanyDTO company = searchPageView.getCompanySelection();
        boolean affiliatesOnly = searchPageView.getFilterByAffiliates().getValue();
        searchPageView.displayLoadingResults("Loading data...");
        try {
            REST.withCallback(new MethodCallback<List<ProductDatasetDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    searchPageView.hideLoadingResults();
                    searchPageView.displaySearchError(method.getResponse().getText());
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
                    affiliatesOnly,
                    company != null ? company.getId() : null,
                    product != null ? product.getId() : null,
                    getSelectedFeatures(),
                    getSelectedPerformances()
            );
        } catch (RequestException e) {
        }
    }

    private String getSelectedPerformances() {
        List<PerformanceValue> selectedPerformances = searchPageView.getSelectedPerformances();
        if(!ListUtil.isNullOrEmpty(selectedPerformances)) {
            return ListUtil.toString(selectedPerformances, new ListUtil.GetLabel<PerformanceValue>() {
                @Override
                public String getLabel(PerformanceValue value) {
                    return value.getPerformanceDescription().getId() + ":" +
                            (value.getMinValue() == null ? "" : value.getMinValue()) + ":" +
                            (value.getMaxValue() == null ? "" : value.getMaxValue());
                }
            }, ",");
        }
        return null;
    }

    private String getSelectedFeatures() {
        List<FeatureDescription> selectedFeatures = searchPageView.getSelectedGeoInformation();
        if(ListUtil.isNullOrEmpty(selectedFeatures)) {
            return null;
        }
        return ListUtil.toString(selectedFeatures, new ListUtil.GetLabel<FeatureDescription>() {
            @Override
            public String getLabel(FeatureDescription value) {
                return value.getId() + "";
            }
        }, ",");
    }

    private String generateSearchHash(String text, int start, int limit, Long aoiId, String aoiWKT, ServiceType serviceType, Long startTime, Long stopTime, boolean affiliatesOnly, Long companyId, Long productId) {
        return StringUtils.join(new String[] {text, start + "", limit + "", aoiId + "", aoiWKT, serviceType + "", startTime + "", stopTime + "", companyId + "", affiliatesOnly + "", productId + ""}, "_");
    }

    private void loadSoftware(final String text, final int start, final int limit) {
        try {
            searchPageView.displayLoadingResults("Loading software...");
            boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
            ProductDTO product = searchPageView.getProductSelection();
            CompanyDTO company = searchPageView.getCompanySelection();
            boolean affiliatesOnly = searchPageView.getFilterByAffiliates().getValue();
            REST.withCallback(new MethodCallback<List<SoftwareDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    searchPageView.hideLoadingResults();
                    searchPageView.displaySearchError(method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, List<SoftwareDTO> softwareDTOs) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addSoftware(softwareDTOs, start, softwareDTOs != null && softwareDTOs.size() != 0 && softwareDTOs.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listSoftware(text, start, limit, filterByAoI ? currentAoI.getId() : null,
                    searchPageView.getSoftwareType(),
                    affiliatesOnly,
                    company != null ? company.getId() : null,
                    product != null ? product.getId() : null);
        } catch (RequestException e) {
        }
    }

    private void loadProjects(final String text, final int start, final int limit) {
        try {
            searchPageView.displayLoadingResults("Loading projects...");
            boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
            Date startTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStartTimeFrameFilter().getValue() : null;
            Date stopTimeFrame = searchPageView.getTimeFrameFilterActivated().getValue() ? searchPageView.getStopTimeFrameFilter().getValue() : null;
            ProductDTO product = searchPageView.getProductSelection();
            CompanyDTO company = searchPageView.getCompanySelection();
            boolean affiliatesOnly = searchPageView.getFilterByAffiliates().getValue();
            REST.withCallback(new MethodCallback<List<ProjectDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    searchPageView.hideLoadingResults();
                    searchPageView.displaySearchError(method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, List<ProjectDTO> projectDTOs) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addProjects(projectDTOs, start, projectDTOs != null && projectDTOs.size() != 0 && projectDTOs.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listProjects(text, start, limit, filterByAoI ? currentAoI.getId() : null,
                    startTimeFrame,
                    stopTimeFrame,
                    affiliatesOnly,
                    company != null ? company.getId() : null,
                    product != null ? product.getId() : null);
        } catch (RequestException e) {
        }
    }

    private void loadCompanies(final String text, final int start, final int limit) {
        try {
            searchPageView.displayLoadingResults("Loading companies...");
            COMPANY_SIZE companySize = searchPageView.getCompanySizeFilter();
            int minYears = searchPageView.getCompanyAgeFilter();
            String countryCode = searchPageView.getCompanyCountryFilter();
            REST.withCallback(new MethodCallback<List<CompanyDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    searchPageView.hideLoadingResults();
                    searchPageView.displaySearchError(method.getResponse().getText());
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

/*
    @Override
    public void textChanged(String text) {
        this.text = text;
        updateSuggestions();
    }

    private void updateSuggestions() {
        this.lastCall++;
        final long lastCall = this.lastCall;

        if(text != null && text.length() > 0) {
            displayListSuggestionsLoading("Searching...");
            try {
                REST.withCallback(new MethodCallback<List<Suggestion>>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideListSuggestionsLoading();
                        displayListSuggestionsError(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, List<Suggestion> response) {
                        // show only if last one to be called
                        if (lastCall == SearchPageActivity.this.lastCall) {
                            hideListSuggestionsLoading();
                            displayListSuggestions(response);
                        }
                    }
                }).call(ServicesUtil.searchService).complete(text, category);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } else {
*/
/*
            // TODO - move to the server side
            List<Suggestion> suggestions = new ArrayList<Suggestion>();
            for(Category suggestionCategory : Category.values()) {
                suggestions.addAll(getSuggestion(suggestionCategory));
            }
            displayListSuggestions(suggestions);
*//*

        }
    }
*/

/*
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
        Category suggestionCategory = suggestion.getCategory();
        switch(action) {
            case "access": {
                FullViewPlace.TOKENS token = null;
                switch (suggestionCategory) {
                    case products:
                        token = FullViewPlace.TOKENS.productid;
                        break;
                    case companies:
                        token = FullViewPlace.TOKENS.companyid;
                        break;
                    case productdatasets:
                        token = FullViewPlace.TOKENS.productdatasetid;
                        break;
                    case productservices:
                        token = FullViewPlace.TOKENS.productserviceid;
                        break;
                    case software:
                        token = FullViewPlace.TOKENS.softwareid;
                        break;
                    case project:
                        token = FullViewPlace.TOKENS.projectid;
                        break;
                    case challenges:
                        token = FullViewPlace.TOKENS.challengeid;
                        break;
                    default:
                        token = null;
                }
                if(token != null) {
                    searchPlace = new FullViewPlace(token.toString() + "=" + parameters);
                }
            } break;
            case "browse": {
                searchPlace = new SearchPagePlace(SearchPagePlace.TOKENS.category.toString() + "=" + suggestionCategory.toString());
            } break;
            default:
                // TODO - find something to do
        }
        if (searchPlace != null) {
            clientFactory.getPlaceController().goTo(searchPlace);
        } else {
            displaySearchError("Sorry I could not understand your request...");
        }
    }
*/

    @Override
    public void textSelected(String text) {
        this.text = text;
        EOBrokerPlace eoBrokerPlace = null;
        if(category == null) {
            // go to general search results page
            String token = "";
            token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
            if(category != null) {
                token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
            }
            eoBrokerPlace = new SearchPagePlace(token);
        } else {
            switch (category) {
                case products:
                case companies:
                case productservices:
                case productdatasets:
                case software:
                case project:
                    // go to general search results page
                    String token = "";
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
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
    public void loadMoreCompanies() {
        start += limit;
        loadCompanies(text, start, limit);
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

    @Override
    public void removeCategoryFilter() {
        Customer.clientFactory.getPlaceController().goTo(
                new SearchPagePlace(Utils.generateTokens(SearchPagePlace.TOKENS.text.toString(), text)));
    }

    public void setAoI(AoIDTO aoIDTO) {
        super.setAoI(aoIDTO);
        searchPageView.displayAoI(aoIDTO);
    }

}
