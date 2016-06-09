package com.geocento.webapps.eobroker.client.activities;

import com.geocento.webapps.eobroker.client.ClientFactory;
import com.geocento.webapps.eobroker.client.events.SuggestionSelected;
import com.geocento.webapps.eobroker.client.events.SuggestionSelectedHandler;
import com.geocento.webapps.eobroker.client.events.TextSelected;
import com.geocento.webapps.eobroker.client.events.TextSelectedHandler;
import com.geocento.webapps.eobroker.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.client.views.LandingPageView;
import com.geocento.webapps.eobroker.shared.Suggestion;
import com.geocento.webapps.eobroker.shared.entities.AoI;
import com.geocento.webapps.eobroker.shared.entities.Category;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

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
                // update the fields
                setCategory(event.getCategory());
                setText(event.getName());
                gotoSearchPage();
            }
        });
        activityEventBus.addHandler(TextSelected.TYPE, new TextSelectedHandler() {
            @Override
            public void onTextSelected(TextSelected event) {
                gotoSearchPage();
            }
        });
    }

    private void gotoSearchPage() {
        String token = "";
        token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
        if(category != null) {
            token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
        }
        if(aoi != null) {
            token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + aoi.getId();
        }
        clientFactory.getPlaceController().goTo(new SearchPagePlace(token));
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
/*
                        ListUtil.mutate(response, new ListUtil.Mutate<Suggestion, SearchObject>() {
                            @Override
                            public SearchObject mutate(Suggestion suggestion) {
                                return new SearchObject(IconType.ACCESS_ALARM,
                                        suggestion.getName());
                            }
                        }));
*/
                    }
                }
            }).call(ServicesUtil.searchService).complete(text, category, toWkt(aoi));
        } else {
            landingPageView.displayListSuggestions(null);
        }
    }

    private String toWkt(AoI aoi) {
        return null;
    }

    @Override
    public void aoiChanged(AoI aoi) {
        this.aoi = aoi;
        updateSuggestions();
    }

    @Override
    public void textChanged(String text) {
        this.text = text;
        updateSuggestions();
    }
}
