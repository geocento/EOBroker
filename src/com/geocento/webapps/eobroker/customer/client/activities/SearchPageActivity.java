package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.SearchResult;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.customer.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.SearchPageView;
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
public class SearchPageActivity extends AbstractApplicationActivity implements SearchPageView.Presenter {

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
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        handlers.add(searchPageView.getChangeSearch().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new LandingPagePlace(""));
            }
        }));
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
        searchPageView.setCategory(category);
        // either text or product is provided
        if(text == null && productId == null && (browse == null || (browse != null && category == null))) {
            clientFactory.getPlaceController().goTo(new LandingPagePlace());
            return;
        }
        // now start the search
        searchPageView.clearResults();
        if(browse != null) {
            switch (category) {
                case products:
                    searchPageView.setCurrentSearch("Browsing existing products");
                    searchPageView.displayLoadingResults("Loading products...");
                    try {
                        final int start = 0, limit = 10;
                        REST.withCallback(new MethodCallback<SearchResult>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                Window.alert("Error");
                            }

                            @Override
                            public void onSuccess(Method method, SearchResult searchResult) {
                                searchPageView.hideLoadingResults();
                                // add all results to the interface
                                List<ProductDTO> products = searchResult.getProducts();
                                searchPageView.displayProductsList(products, start, limit, text);
                            }
                        }).call(ServicesUtil.searchService).listProducts(text, start, limit, aoiId);
                    } catch (RequestException e) {
                    }
                    break;
            }
        } else if(productId != null) {
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
                        searchPageView.setProductSelection(suggestedProducts.get(0), searchResult.getProductServices(), suggestedProducts.subList(0, Math.min(1, suggestedProducts.size() - 1)));
                    }
                }).call(ServicesUtil.searchService).getMatchingServicesForProduct(productId, null);
            } catch (RequestException e) {
            }
        } else if(text != null) {
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
                        // add all results to the interface
                        List<ProductDTO> suggestedProducts = searchResult.getProducts();
                        searchPageView.setMatchingProducts(suggestedProducts);
                        searchPageView.setMatchingServices(searchResult.getProductServices());
                    }
                }).call(ServicesUtil.searchService).getMatchingServices(text, category, null);
            } catch (RequestException e) {
            }
        }
    }

}