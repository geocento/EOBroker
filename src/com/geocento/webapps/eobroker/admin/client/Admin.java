package com.geocento.webapps.eobroker.admin.client;

import com.geocento.webapps.eobroker.admin.client.activities.AppActivityMapper;
import com.geocento.webapps.eobroker.admin.client.places.AppPlaceHistoryMapper;
import com.geocento.webapps.eobroker.admin.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.activity.shared.FilteredActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 20/06/2016.
 */
public class Admin implements EntryPoint {

    private static LoginInfo loginInfo;

    /*
        private SimpleLayoutPanel appWidget = new SimpleLayoutPanel();
    */
    private SimplePanel appWidget = new SimplePanel();

    public static ClientFactory clientFactory;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // Create ClientFactory using deferred binding so we can replace with different
        // impls in gwt.xml
        clientFactory = GWT.create(ClientFactory.class);
        EventBus eventBus = clientFactory.getEventBus();
        final PlaceController placeController = clientFactory.getPlaceController();

        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
        FilteredActivityMapper filteredActivityMapper = new FilteredActivityMapper(new FilteredActivityMapper.Filter() {

            @Override
            public Place filter(Place place) {
                // sign in is required for any page
                if(loginInfo == null) {
                    return new LoginPagePlace();
                }
                return place;
            }
        }, activityMapper);
        ActivityManager activityManager = new ActivityManager(filteredActivityMapper, eventBus);
        activityManager.setDisplay(appWidget);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
        final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, clientFactory.getDefaultPlace());

        RootPanel.get().add(appWidget);

        // remove the loading div
        DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));

        initialise();

        // check we have an active session
        REST.withCallback(new MethodCallback<LoginInfo>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                // TODO - decide what to do!
            }

            @Override
            public void onSuccess(Method method, LoginInfo loginInfo) {
                Admin.loginInfo = loginInfo;
                historyHandler.handleCurrentHistory();
            }
        }).call(ServicesUtil.loginService).getSession();

    }

    private void initialise() {
        Defaults.setServiceRoot(GWT.getModuleBaseURL() + "api");
        Defaults.setDateFormat(null);
    }

    public static void setLoginInfo(LoginInfo loginInfo) {
        Admin.loginInfo = loginInfo;
    }

    public static LoginInfo getLoginInfo() {
        return Admin.loginInfo;
    }
}
