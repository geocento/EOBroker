package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
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
        panel.setWidget(searchPageView.asWidget());
        setTemplateView(searchPageView.getTemplateView());
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
        final String text = tokens.get(SearchPagePlace.TOKENS.text.toString());
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
        // either text or product is provided
        if(text == null && category == null) {
            clientFactory.getPlaceController().goTo(new LandingPagePlace());
            return;
        }
        this.start = 0;
        this.limit = 24;
        // now start the search
        setSearchText(text);
        showCategories(true, text);
        selectCategory(category);
        searchPageView.displayFilters(category);

        // add current AoI
        searchPageView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                searchPageView.displayAoI(currentAoI);
            }
        });

        updateSearchResults();

    }

    private void selectCategory(Category category) {
        this.category = category;
        searchPageView.selectCategory(category);
    }

    private void setSearchText(String text) {
        this.text = text;
        searchPageView.setSearchText(text);
    }

    private void updateSearchResults() {
        searchPageView.clearResults();
        if(category != null) {
            // search using the category
            switch (category) {
                case products: {
                    searchPageView.setTitleText("Browse products");
                    loadProducts(text, start, limit);
                } break;
                case productservices: {
                    searchPageView.setTitleText("Browse on-demand services");
                    loadProductServices(text, start, limit);
                } break;
                case productdatasets: {
                    searchPageView.setTitleText("Browse off-the-shelf data");
                    loadProductDatasets(text, start, limit);
                } break;
                case software: {
                    searchPageView.setTitleText("Browse software solutions");
                    loadSoftware(text, start, limit);
                } break;
                case project: {
                    searchPageView.setTitleText("Browse projects");
                    loadProjects(text, start, limit);
                } break;
                case companies: {
                    searchPageView.setTitleText("Browse companies");
                    searchPageView.displayLoadingResults("Loading companies...");
                    loadCompanies(text, start, limit);
                } break;
            }
        } else if(text != null) {
            searchPageView.setTitleText("Search Results");
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
                }).call(ServicesUtil.searchService).getMatchingServices(text, filterByAoI ? currentAoI.getId() : null);
            } catch (RequestException e) {
            }
        }
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
        try {
            searchPageView.displayLoadingResults("Loading services...");
            boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
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
            }).call(ServicesUtil.searchService).listProductServices(text, start, limit, filterByAoI ? currentAoI.getId() : null);
        } catch (RequestException e) {
        }
    }

    private void loadProductDatasets(final String text, final int start, final int limit) {
        try {
            searchPageView.displayLoadingResults("Loading data...");
            boolean filterByAoI = currentAoI != null && searchPageView.getFilterByAoI().getValue();
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
            }).call(ServicesUtil.searchService).listProductDatasets(text, start, limit, filterByAoI ? currentAoI.getId() : null);
        } catch (RequestException e) {
        }
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
            }).call(ServicesUtil.searchService).listSoftware(text, start, limit, filterByAoI ? currentAoI.getId() : null);
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
            REST.withCallback(new MethodCallback<List<CompanyDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error");
                }

                @Override
                public void onSuccess(Method method, List<CompanyDTO> companyDTOs) {
                    searchPageView.hideLoadingResults();
                    // add all results to the interface
                    searchPageView.addCompanies(companyDTOs, start, companyDTOs != null && companyDTOs.size() != 0 && companyDTOs.size() % limit == 0, text);
                }
            }).call(ServicesUtil.searchService).listCompanies(text, start, limit, currentAoI == null ? null : currentAoI.getId());
        } catch (RequestException e) {
        }
    }

    private String getSearchCategoryUrl(Category category, String text) {
        return "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), category.toString(),
                SearchPagePlace.TOKENS.text.toString(), text == null ? "" : text
        )));
    }

    private void showCategories(boolean display, String text) {
        searchPageView.displayCategories(display);
        searchPageView.getProductsCategory().setHref(getSearchCategoryUrl(Category.products, text));
        searchPageView.getProductServicesCategory().setHref(getSearchCategoryUrl(Category.productservices, text));
        searchPageView.getProductDatasetsCategory().setHref(getSearchCategoryUrl(Category.productdatasets, text));
        searchPageView.getSoftwareCategory().setHref(getSearchCategoryUrl(Category.software, text));
        searchPageView.getProjectsCategory().setHref(getSearchCategoryUrl(Category.project, text));
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
        if(aoi == null) {
            setAoI(null);
            if(searchPageView.getFilterByAoI().getValue()) {
                filtersChanged();
            }
        } else {
            displayLoading();
            // untick the filtering
            try {
                REST.withCallback(new MethodCallback<AoIDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideLoading();
                    }

                    @Override
                    public void onSuccess(Method method, AoIDTO aoIDTO) {
                        hideLoading();
                        setAoI(aoIDTO);
                        if(searchPageView.getFilterByAoI().getValue()) {
                            filtersChanged();
                        }
                    }
                }).call(ServicesUtil.assetsService).updateAoI(aoi);
            } catch (Exception e) {

            }
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
