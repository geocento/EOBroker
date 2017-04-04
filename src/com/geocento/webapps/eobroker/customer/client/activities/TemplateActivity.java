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
import com.geocento.webapps.eobroker.customer.client.events.ImageOrderCreatedHandler;
import com.geocento.webapps.eobroker.customer.client.events.LogOut;
import com.geocento.webapps.eobroker.customer.client.events.LogOutHandler;
import com.geocento.webapps.eobroker.customer.client.events.RequestCreated;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.TemplateView;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
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
    static protected AoIDTO currentAoI = null;

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
    }

    public void setTemplateView(final TemplateView templateView) {
        this.templateView = templateView;
        // check use is logged in
        if(Customer.isLoggedIn()) {
            templateView.displaySignedIn(true);
            LoginInfo loginInfo = Customer.getLoginInfo();
            templateView.setUser(loginInfo);
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
            // if no AoI loaded load the initial one
            if(currentAoI == null) {
                currentAoI = loginInfo.getAoIDTO();
            }
        } else {
            templateView.displaySignedIn(false);
        }
        templateView.setPresenter(this);
        // reset some values
        templateView.setSearchText(null);

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
    public void textChanged(String text) {
        // go to search activity by default
        clientFactory.getPlaceController().goTo(new SearchPagePlace());
    }

    @Override
    public void suggestionSelected(Suggestion suggestion) {
    }

    @Override
    public void textSelected(String text) {
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

    public void displayFullLoading(String message) {
        templateView.displayFullLoading(message);
    }

    public void hideFullLoading() {
        templateView.hideFullLoading();
    }

    public void displayListSuggestions(List<Suggestion> suggestions) {
        templateView.displayListSuggestions(suggestions);
    }

    public void displaySearchError(String message) {
        MaterialToast.fireToast(message);
    }
}
