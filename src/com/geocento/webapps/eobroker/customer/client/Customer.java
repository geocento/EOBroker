package com.geocento.webapps.eobroker.customer.client;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.customer.client.activities.AppActivityMapper;
import com.geocento.webapps.eobroker.customer.client.places.AppPlaceHistoryMapper;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.activity.shared.FilteredActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;
import org.fusesource.restygwt.client.Defaults;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class Customer implements EntryPoint {

    public static AoI currentAoI;
    private SimpleLayoutPanel appWidget = new SimpleLayoutPanel();

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
                return place;
            }
        }, activityMapper);
        ActivityManager activityManager = new ActivityManager(filteredActivityMapper, eventBus);
        activityManager.setDisplay(appWidget);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
        final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, clientFactory.getDefaultPlace());

        RootLayoutPanel.get().add(appWidget);

        // remove the loading div
        DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));

        initialise();

        historyHandler.handleCurrentHistory();

/*
        final MaterialSplashScreen splash = new MaterialSplashScreen();
        splash.setBackgroundColor("blue");
        splash.setTextColor("white");
        splash.setTextAlign(TextAlign.CENTER);
        MaterialImage materialImage = new MaterialImage();
        materialImage.setResource(StyleResources.INSTANCE.logoEOBroker());
        splash.add(materialImage);
        MaterialTitle materialTitle = new MaterialTitle("EO Broker", "The ESA EO Broker description");
        splash.add(materialTitle);
        splash.show();
        Timer timer = new Timer() {
            @Override
            public void run() {
                splash.hide();
                historyHandler.handleCurrentHistory();
            }
        };
        timer.schedule(3000);
*/
    }

    private void initialise() {
        Defaults.setServiceRoot("/customer/api");
        Defaults.setDateFormat(null);
    }

}
