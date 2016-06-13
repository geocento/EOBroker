package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.activities.Activity;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.EOBrokerPlace;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractApplicationActivity extends AbstractActivity {

    protected ClientFactory clientFactory;

    protected EOBrokerPlace place;

	protected List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

	protected EventBus activityEventBus;
	
    public AbstractApplicationActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, com.google.gwt.event.shared.EventBus eventBus) {
		activityEventBus = eventBus;
	}
	
	abstract protected void bind();

	protected void unbind() {
		
		((ResettableEventBus) activityEventBus).removeHandlers();
		
		for(HandlerRegistration handler : handlers) {
			handler.removeHandler();
		}
		handlers.clear();
		
	}

    @Override
	public String mayStop() {
		unbind();
		Activity.triggerActivityClose();
		return super.mayStop();
	}

}
