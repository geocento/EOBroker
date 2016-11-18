package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.ImageOrderCreatedHandler;
import com.geocento.webapps.eobroker.customer.client.events.LogOut;
import com.geocento.webapps.eobroker.customer.client.events.LogOutHandler;
import com.geocento.webapps.eobroker.customer.client.events.RequestCreated;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.views.TemplateView;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 29/06/2016.
 */
public abstract class TemplateActivity extends AbstractApplicationActivity implements TemplateView.Presenter {

    private List<RequestDTO> userRequests = null;
    private List<NotificationDTO> userNotifications = null;

    private TemplateView templateView;

    private String text;

    private int lastCall = 0;
    protected Category category = null;
    static protected AoIDTO currentAoI = Utils.getAoI();

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
    }

    public void setTemplateView(final TemplateView templateView) {
        this.templateView = templateView;
        // check use is logged in
        if(Customer.isLoggedIn()) {
            templateView.displaySignedIn(true);
            templateView.setUser(Customer.getLoginInfo());
            // load notifications and orders
            if(userRequests == null) {
                loadUserOrders();
            } else {
                templateView.setRequests(userRequests);
            }
            if(userNotifications == null) {
                loadUserNotifications();
            } else {
                templateView.setNotifications(userNotifications);
            }
        } else {
            templateView.displaySignedIn(false);
        }
        templateView.setPresenter(this);
        // reset some values
        templateView.setSearchText(null);
        // make sure page scrolls to the top
        templateView.scrollToTop();
    }

    private void loadUserOrders() {
        try {
            REST.withCallback(new MethodCallback<List<RequestDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<RequestDTO> requestDTOs) {
                    userRequests = requestDTOs;
                    templateView.setRequests(requestDTOs);
                }
            }).call(ServicesUtil.ordersService).getRequests();
        } catch (RequestException e) {
        }
    }

    private void loadUserNotifications() {
        try {
            REST.withCallback(new MethodCallback<List<NotificationDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<NotificationDTO> notificationDTOs) {
                    userNotifications = notificationDTOs;
                    templateView.setNotifications(notificationDTOs);
                }
            }).call(ServicesUtil.assetsService).getNotifications();
        } catch (RequestException e) {
        }
    }

    @Override
    protected void bind() {
        activityEventBus.addHandler(LogOut.TYPE, new LogOutHandler() {
            @Override
            public void onLogOut(LogOut event) {
                REST.withCallback(new MethodCallback<Void>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, Void response) {
                        Window.Location.reload();
                    }
                }).call(ServicesUtil.loginService).signout();
            }
        });

        activityEventBus.addHandler(RequestCreated.TYPE, new ImageOrderCreatedHandler() {
            @Override
            public void onImageOrderCreated(RequestCreated event) {
                loadUserOrders();
            }
        });
    }

    @Override
    public void categoryChanged(Category category) {

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
                    if (lastCall == TemplateActivity.this.lastCall) {
                        templateView.displayListSuggestions(response);
                    }
                }
            }).call(ServicesUtil.searchService).complete(text, category, toWkt(currentAoI));
        } else {
            // TODO - move to the server side
            List<Suggestion> suggestions = new ArrayList<Suggestion>();
/*
            if(category == null) {
                for(Category suggestionCategory : Category.values()) {
                    suggestions.addAll(getSuggestion(suggestionCategory));
                }
            } else {
                suggestions.addAll(getSuggestion(category));
            }
*/
            for(Category suggestionCategory : Category.values()) {
                suggestions.addAll(getSuggestion(suggestionCategory));
            }
            templateView.displayListSuggestions(suggestions);
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
                            (currentAoI == null ? "" : "&" + ImageSearchPlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId()));
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
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
                    searchPlace = new SearchPagePlace(token);
                } else {
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    if (category != null) {
                        token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    }
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
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
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
                    searchPlace = new SearchPagePlace(token);
                } else {
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    if (category != null) {
                        token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    }
                    if (currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
                    searchPlace = new SearchPagePlace(token);
                }
            } break;
        }
        if (searchPlace != null) {
            clientFactory.getPlaceController().goTo(searchPlace);
        } else {
            templateView.displaySearchError("Sorry I could not understand your request...");
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
            if(currentAoI != null) {
                token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
            }
            eoBrokerPlace = new SearchPagePlace(token);
        } else {
            switch (category) {
                case imagery:
                    eoBrokerPlace = new ImageSearchPlace(ImageSearchPlace.TOKENS.text.toString() + "=" + text +
                            (currentAoI == null ? "" : "&" + ImageSearchPlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId()));
                    break;
                case products:
                case companies:
                case datasets:
                case software:
                    // go to general search results page
                    String token = "";
                    token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
                    token += "&" + SearchPagePlace.TOKENS.category.toString() + "=" + category.toString();
                    if(currentAoI != null) {
                        token += "&" + SearchPagePlace.TOKENS.aoiId.toString() + "=" + currentAoI.getId();
                    }
                    eoBrokerPlace = new SearchPagePlace(token);
            }
        }
        if(eoBrokerPlace != null) {
            clientFactory.getPlaceController().goTo(eoBrokerPlace);
        } else {
            templateView.displaySearchError("Sorry I could not understand your request...");
        }
    }

    public void setAoI(AoIDTO aoi) {
        this.currentAoI = aoi;
        Utils.saveAoI(currentAoI);
    }

    protected void displayLoading() {
        templateView.displayLoading();
    }

    protected void hideLoading() {
        templateView.hideLoading();
    }

    protected void displayError(String message) {
        templateView.displayError(message);
    }

}
