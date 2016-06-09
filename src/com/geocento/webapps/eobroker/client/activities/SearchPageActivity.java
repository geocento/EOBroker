package com.geocento.webapps.eobroker.client.activities;

import com.geocento.webapps.eobroker.client.ClientFactory;
import com.geocento.webapps.eobroker.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.client.utils.Utils;
import com.geocento.webapps.eobroker.client.views.SearchPageView;
import com.geocento.webapps.eobroker.shared.entities.Category;
import com.geocento.webapps.eobroker.shared.entities.SearchResult;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

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

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        String text = tokens.get(SearchPagePlace.TOKENS.text.toString());
        // text cannot be null
        if(text == null) {
            clientFactory.getPlaceController().goTo(new LandingPagePlace());
        }
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
                Category.valueOf(tokens.get(SearchPagePlace.TOKENS.category.toString()));
            } catch (Exception e) {

            }
        }
        // update the interface
        searchPageView.setCurrentSearch(text);
        searchPageView.setCategory(category);
        // now start the search
        searchPageView.clearResults();
        searchPageView.displayLoadingResults("Searching matching results...");
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
                    for(ProductDTO productDTO : searchResult.getProducts()) {
                        // TODO map the services corresponding to the product
                        searchPageView.addProduct(productDTO, searchResult.getProductServices());
                    }
                }
            }).call(ServicesUtil.searchService).getMatchingServices(text, category, null);
        } catch (RequestException e) {
        }

    }

    @Override
    protected void bind() {

    }

}
