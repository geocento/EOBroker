package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.RequestImagery;
import com.geocento.webapps.eobroker.customer.client.events.RequestImageryHandler;
import com.geocento.webapps.eobroker.customer.client.events.SearchImagery;
import com.geocento.webapps.eobroker.customer.client.events.SearchImageryHandler;
import com.geocento.webapps.eobroker.customer.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.customer.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.customer.client.places.RequestImageryPlace;
import com.geocento.webapps.eobroker.customer.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.SearchPageView;
import com.geocento.webapps.eobroker.customer.shared.SearchResult;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

        handlers.add(searchPageView.getChangeSearch().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
/*
                clientFactory.getPlaceController().goTo(new LandingPagePlace(""));
*/
            }
        }));

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
        Category category = null;
        if(tokens.containsKey(SearchPagePlace.TOKENS.category.toString())) {
            try {
                category = Category.valueOf(tokens.get(SearchPagePlace.TOKENS.category.toString()));
            } catch (Exception e) {

            }
        }
        // update the interface
        searchPageView.setCategories(category == null ? ListUtil.toList(Category.values()) : ListUtil.toList(category));
        // either text or product is provided
        if(text == null && productId == null && (browse == null || (browse != null && category == null))) {
            clientFactory.getPlaceController().goTo(new LandingPagePlace());
            return;
        }
        // now start the search
        searchPageView.clearResults();
        if(browse != null) {
            switch (category) {
                case products: {
                    searchPageView.setTitleText("Browse products");
                    searchPageView.setCurrentSearch("");
                    searchPageView.displayLoadingResults("Loading products...");
                    try {
                        final int start = 0, limit = 200;
                        REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                Window.alert("Error");
                            }

                            @Override
                            public void onSuccess(Method method, List<ProductDTO> products) {
                                searchPageView.setSearchResults("Found " + products.size() + " matching products");
                                searchPageView.hideLoadingResults();
                                // add all results to the interface
                                searchPageView.displayProductsList(products, start, limit, text);
                            }
                        }).call(ServicesUtil.searchService).listProducts(text, start, limit, aoiId);
                    } catch (RequestException e) {
                    }
                } break;
                case companies: {
                    searchPageView.setTitleText("Browse companies");
                    searchPageView.setCurrentSearch("");
                    searchPageView.displayLoadingResults("Loading companies...");
                    final int start = 0, limit = 10;
                    REST.withCallback(new MethodCallback<List<CompanyDTO>>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            Window.alert("Error");
                        }

                        @Override
                        public void onSuccess(Method method, List<CompanyDTO> companyDTOs) {
                            searchPageView.setSearchResults("Found " + companyDTOs.size() + " matching companies");
                            searchPageView.hideLoadingResults();
                            // add all results to the interface
                            searchPageView.displayCompaniesList(companyDTOs, start, limit, text);
                        }
                    }).call(ServicesUtil.searchService).listCompanies(text, start, limit, aoiId);
                } break;
            }
        } else if(productId != null) {
            searchPageView.setTitleText("Explore product services");
            searchPageView.displayLoadingResults("Loading product and matching results...");
            try {
                REST.withCallback(new MethodCallback<SearchResult>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert("Error");
                    }

                    @Override
                    public void onSuccess(Method method, SearchResult searchResult) {
                        searchPageView.hideLoadingResults();
                        // add all results to the interface
                        List<ProductDTO> suggestedProducts = searchResult.getProducts();
                        ProductDTO product = suggestedProducts.get(0);
                        searchPageView.setSearchResults("You selected '" + product.getName() + "'");
                        searchPageView.setProductSelection(product, searchResult.getProductServices(), suggestedProducts.subList(0, Math.min(1, suggestedProducts.size() - 1)));
                        searchPageView.setMatchingImagery(product.getName());
                    }
                }).call(ServicesUtil.searchService).getMatchingServicesForProduct(productId, null);
            } catch (RequestException e) {
            }
        } else if(text != null) {
            searchPageView.setTitleText("Search Results");
            searchPageView.displayLoadingResults("Searching matching results...");
            searchPageView.setCurrentSearch(text);
            try {
                REST.withCallback(new MethodCallback<SearchResult>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert("Error");
                    }

                    @Override
                    public void onSuccess(Method method, SearchResult searchResult) {
                        searchPageView.hideLoadingResults();
                        searchPageView.setSearchResults("Results for '" + text + "'");
                        // add all results to the interface
                        List<ProductDTO> suggestedProducts = searchResult.getProducts();
                        searchPageView.setMatchingProducts(suggestedProducts);
                        searchPageView.setMatchingServices(searchResult.getProductServices());
                        searchPageView.setMatchingImagery(text);
                        // search for datasets
                        // TODO - move to server to locally cache? could be issue with timing...
                        searchPageView.setDatasetProviders(searchResult.getDatasetsProviders(), text, aoi);
                    }
                }).call(ServicesUtil.searchService).getMatchingServices(text, category, null);
            } catch (RequestException e) {
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
                if (Customer.currentAoI != null) {
                    searchPageView.displayAoI(Customer.currentAoI);
                }
            }
        });

    }

}
