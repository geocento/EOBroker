package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.SuggestionSelected;
import com.geocento.webapps.eobroker.customer.client.events.SuggestionSelectedHandler;
import com.geocento.webapps.eobroker.customer.client.events.TextSelected;
import com.geocento.webapps.eobroker.customer.client.events.TextSelectedHandler;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.LandingPageView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class LandingPageActivity extends AbstractApplicationActivity implements LandingPageView.Presenter {

    private LandingPageView landingPageView;
    private Category category;
    private AoI aoi;
    private String text;
    private long lastCall = 0;

    public LandingPageActivity(LandingPagePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        landingPageView = clientFactory.getLandingPageView();
        landingPageView.setPresenter(this);
        panel.setWidget(landingPageView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
    }

    @Override
    protected void bind() {

        activityEventBus.addHandler(SuggestionSelected.TYPE, new SuggestionSelectedHandler() {
            @Override
            public void onSuggestionSelected(SuggestionSelected event) {
                // TODO - save or update current AoI
                Customer.currentAoI = aoi;
                // update the fields
                Suggestion suggestion = event.getSuggestion();
                //setCategory(suggestion.getCategory());
                // TODO - move to a helper class
                String uri = suggestion.getUri();
                String action = uri.split("::")[0];
                String parameters = uri.split("::")[1];
                if(parameters == null) {
                    parameters = "";
                }
                EOBrokerPlace searchPlace = null;
                switch(suggestion.getCategory()) {
                    case imagery:
                        if(action.contentEquals("search")) {
                            searchPlace = new ImageSearchPlace(ImageSearchPlace.TOKENS.text.toString() + "=" + parameters +
                                    (aoi == null ? "" : "&" + ImageSearchPlace.TOKENS.aoiId.toString() + "=" + aoi.getId()));
                            //setText(parameters);
                        } else if(action.contentEquals("request")) {
                            searchPlace = new RequestImageryPlace(parameters);
                            //setText("");
                        };
                        break;
                    case products:
                        String token = "";
                        if(action.contentEquals("product")) {
                            token += SearchPagePlace.TOKENS.product.toString() + "=" + parameters;
                            //setText(suggestion.getName());
                        } else if(action.contentEquals("browse")) {
                            token += SearchPagePlace.TOKENS.browse.toString() + "=" + parameters;
                            //setText("");
                        } else {
                            token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                            //setText(text);
                        }
                        Category category = suggestion.getCategory();
                        if(category != null) {
                            token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                        }
                        if(aoi != null) {
                            token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + aoi.getId();
                        }
                        searchPlace = new SearchPagePlace(token);
                        break;
                }
                if(searchPlace != null) {
                    clientFactory.getPlaceController().goTo(searchPlace);
                } else {
                    landingPageView.displaySearchError("Sorry I could not understand your request...");
                }
            }
        });
        activityEventBus.addHandler(TextSelected.TYPE, new TextSelectedHandler() {
            @Override
            public void onTextSelected(TextSelected event) {
                // TODO - save or update current AoI
                Customer.currentAoI = aoi;
                text = event.getText();
                if(text.trim().length() == 0) {
                    return;
                }
                EOBrokerPlace eoBrokerPlace = null;
                if(category == null) {
                    // go to general search results page
                    String token = "";
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    if(category != null) {
                        token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    }
                    if(aoi != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + aoi.getId();
                    }
                    eoBrokerPlace = new SearchPagePlace(token);
                } else {
                    switch (category) {
                        case imagery:
                            eoBrokerPlace = new ImageSearchPlace(ImageSearchPlace.TOKENS.text.toString() + "=" + text +
                                    (aoi == null ? "" : "&" + ImageSearchPlace.TOKENS.aoiId.toString() + "=" + aoi.getId()));
                            break;
                        case products:
                        case companies:
                        case datasets:
                        case software:
                            // go to general search results page
                            String token = "";
                            token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                            token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                            if(aoi != null) {
                                token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + aoi.getId();
                            }
                            eoBrokerPlace = new SearchPagePlace(token);
                    }
                }
                if(eoBrokerPlace != null) {
                    clientFactory.getPlaceController().goTo(eoBrokerPlace);
                } else {
                    landingPageView.displaySearchError("Sorry I could not understand your request...");
                }
            }
        });
    }

    private void setCategory(Category category) {
        this.category = category;
        landingPageView.displayCategory(category);
    }

    private void setText(String text) {
        this.text = text;
        landingPageView.displayText(text);
    }

    @Override
    public void categoryChanged(Category category) {
        this.category = category;
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
                    if (lastCall == LandingPageActivity.this.lastCall) {
                        landingPageView.displayListSuggestions(response);
                    }
                }
            }).call(ServicesUtil.searchService).complete(text, category, toWkt(aoi));
        } else {
            // TODO - move to the server side
            List<Suggestion> suggestions = new ArrayList<Suggestion>();
            if(category == null) {
                for(Category suggestionCategory : Category.values()) {
                    suggestions.addAll(getSuggestion(suggestionCategory));
                }
            } else {
                suggestions.addAll(getSuggestion(category));
            }
            landingPageView.displayListSuggestions(suggestions);
        }
    }

    private List<Suggestion> getSuggestion(Category category) {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        switch (category) {
            case products:
                suggestions.add(new Suggestion("Browse products", Category.products, "browse::"));
                break;
            case imagery:
                suggestions.add(new Suggestion("Search for imagery", Category.imagery, "search::"));
                suggestions.add(new Suggestion("Request quotation for imagery", Category.imagery, "request::"));
                break;
            case companies:
                suggestions.add(new Suggestion("Browse companies", Category.companies, "browse::"));
                break;
            case datasets:
                suggestions.add(new Suggestion("Browse datasets", Category.datasets, "browse::"));
                break;
        }
        return suggestions;
    }

    private String toWkt(AoI aoi) {
        return null;
    }

    @Override
    public void aoiChanged(AoI aoi) {
        this.aoi = aoi;
        //updateSuggestions();
    }

    @Override
    public void textChanged(String text) {
        this.text = text;
        updateSuggestions();
    }
}
