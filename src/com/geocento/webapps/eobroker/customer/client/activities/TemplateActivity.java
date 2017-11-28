package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.*;
import com.geocento.webapps.eobroker.customer.client.places.EOBrokerPlace;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.TemplateView;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialToast;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 29/06/2016.
 */
public abstract class TemplateActivity extends AbstractApplicationActivity implements TemplateView.Presenter {

    private List<RequestDTO> userRequests = null;
    private List<NotificationDTO> userNotifications = null;

    private TemplateView templateView;

    protected Category category = null;

    private String text;
    private int lastCall = 0;

    static protected AoIDTO currentAoI = null;

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        templateView = clientFactory.getTemplateView();
        panel.setWidget(templateView);
        // reset the search text if we are in a non search activity
        if(!(this instanceof SearchPageActivity)) {
            setSearchText("", false);
        }
    }

    public void setTemplateView(Widget widget) {
        templateView.clear();
        templateView.add(widget);
        // check use is logged in
        if(Customer.isLoggedIn()) {
            templateView.displaySignedIn(true);
            LoginInfo loginInfo = Customer.getLoginInfo();
            templateView.setUser(loginInfo);
/*
            // load notifications and orders
            if(userRequests == null) {
                loadUserOrders();
            } else {
                templateView.setRequests(userRequests);
            }
*/
            if(userNotifications == null) {
                loadUserNotifications();
            } else {
                templateView.setNotifications(userNotifications);
            }
            // if no AoI loaded load the initial one
            if(currentAoI == null) {
                currentAoI = loginInfo.getAoIDTO();
            }
        } else {
            templateView.displaySignedIn(false);
        }
        templateView.setPresenter(this);
        // reset some values
        //templateView.setSearchText(null);

        // set the menu links
        templateView.getProductsCategory().setHref(getSearchCategoryUrl(Category.products));
        templateView.getProductServicesCategory().setHref(getSearchCategoryUrl(Category.productservices));
        templateView.getProductDatasetsCategory().setHref(getSearchCategoryUrl(Category.productdatasets));
        templateView.getSoftwareCategory().setHref(getSearchCategoryUrl(Category.software));
        templateView.getProjectsCategory().setHref(getSearchCategoryUrl(Category.project));
        templateView.getCompaniesCategory().setHref(getSearchCategoryUrl(Category.companies));

        // make sure page scrolls to the top
        templateView.scrollToTop();
    }

    private String getSearchCategoryUrl(Category category) {
        return "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), category.toString()
        )));
    }

    public void selectMenu(String menu) {
        // TODO - change somehow
        templateView.setMenu(menu);
    }

    // TODO - change for a request count instead?
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
            }).call(ServicesUtil.requestsService).getRequests(Request.STATUS.submitted);
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
            }).call(ServicesUtil.assetsService).listNotifications(0, 10);
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

/*
        activityEventBus.addHandler(RequestCreated.TYPE, new ImageOrderCreatedHandler() {
            @Override
            public void onImageOrderCreated(RequestCreated event) {
                loadUserOrders();
            }
        });
*/

        activityEventBus.addHandler(NotificationEvent.TYPE, new NotificationEventHandler() {
            @Override
            public void onNotification(NotificationEvent event) {
                // update notifications
                NotificationDTO notificationDTO = event.getNotification();
                // make sure it is not already included
                if(!userNotifications.contains(notificationDTO)) {
                    userNotifications.add(notificationDTO);
                    templateView.setNotifications(userNotifications);
                }
            }
        });

        activityEventBus.addHandler(WebSocketFailedEvent.TYPE, new WebSocketFailedEventHandler() {
            @Override
            public void onWebSocketFailed(WebSocketFailedEvent event) {
                templateView.displayWebsocketError("Failed to connect with server...");
                // TODO - take specific action when 
            }
        });
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
                        if (lastCall == TemplateActivity.this.lastCall) {
                            hideListSuggestionsLoading();
                            displayListSuggestions(response);
                        }
                    }
                }).call(ServicesUtil.searchService).complete(text, category);
            } catch (RequestException e) {
                e.printStackTrace();
            }
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

    @Override
    public void textSelected(String text) {
        this.text = text;
        EOBrokerPlace eoBrokerPlace = null;
        // go to general search results page
        String token = "";
        token += SearchPagePlace.TOKENS.text.toString() + "=" + text;
        eoBrokerPlace = new SearchPagePlace(token);
        clientFactory.getPlaceController().goTo(eoBrokerPlace);
    }

    @Override
    public void onFocus() {
    }

    public void setAoI(AoIDTO aoi) {
        this.currentAoI = aoi;
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

    protected void displaySuccess(String message) {
        templateView.displaySuccess(message);
    }

    public void displayFullLoading(String message) {
        templateView.displayFullLoading(message);
    }

    public void displayFullError(String message) {
        templateView.displayFullError(message);
    }

    public void hideFullLoading() {
        templateView.hideFullLoading();
    }

    public void displayListSuggestions(List<Suggestion> suggestions) {
        templateView.displayListSuggestions(suggestions);
    }

    protected void displayListSuggestionsLoading(String message) {
        templateView.displayListSuggestionsLoading(message);
    }

    protected void hideListSuggestionsLoading() {
        templateView.hideListSuggestionsLoading();
    }

    protected void displayListSuggestionsError(String message) {
        templateView.displayListSuggestionsError(message);
    }

    public void displaySearchError(String message) {
        MaterialToast.fireToast(message);
    }

    public void displayMenu(boolean display) {
        templateView.displayMenu(display);
    }

    public void setSearchText(String search, boolean forceFocus) {
        templateView.setSearchText(search);
        if(forceFocus) {
            templateView.setSearchTextFocus(true);
        }
    }

    public void setTitleText(String title) {
        templateView.setTitleText(title);
    }

    @Override
    public void onStop() {
        // make sure we won't display the suggestions after stopping
        lastCall = 0;
    }
}
