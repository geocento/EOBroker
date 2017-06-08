package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.LogsPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.LogsView;
import com.geocento.webapps.eobroker.admin.shared.dtos.LogsDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class LogsActivity extends TemplateActivity implements LogsView.Presenter {

    private LogsView logsView;

    public LogsActivity(LogsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        logsView = clientFactory.getLogsView();
        logsView.setPresenter(this);
        panel.setWidget(logsView.asWidget());
        setTemplateView(logsView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        loadLogs();
    }

    private void loadLogs() {
        try {
            displayLoading("Loading logs...");
            REST.withCallback(new MethodCallback<LogsDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError(exception.getMessage());
                }

                @Override
                public void onSuccess(Method method, LogsDTO response) {
                    hideLoading();
                    logsView.setLogs(response.getLogValue());
                }
            }).call(ServicesUtil.assetsService).getLogs();
        } catch (RequestException e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(logsView.getReload().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loadLogs();
            }
        }));
    }

}
