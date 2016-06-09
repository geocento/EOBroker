package com.geocento.webapps.eobroker.client.activities;

import com.geocento.webapps.eobroker.client.ClientFactory;
import com.geocento.webapps.eobroker.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.client.views.ImageSearchView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchActivity extends AbstractApplicationActivity implements ImageSearchView.Presenter {

    private ImageSearchView imageSearchView;

    public ImageSearchActivity(ImageSearchPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        imageSearchView = clientFactory.getImageSearchView();
        imageSearchView.setPresenter(this);
        panel.setWidget(imageSearchView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
    }

    @Override
    protected void bind() {
    }

}
