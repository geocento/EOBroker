package com.geocento.webapps.eobroker.admin.client.utils;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.events.NotificationEvent;
import com.geocento.webapps.eobroker.admin.client.events.WebSocketClosedEvent;
import com.geocento.webapps.eobroker.admin.client.events.WebSocketFailedEvent;
import com.geocento.webapps.eobroker.admin.shared.dtos.AdminWebSocketMessage;
import com.geocento.webapps.eobroker.admin.shared.dtos.AdminWebSocketMessageMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListenerAdapter;

/**
 * Created by thomas on 08/05/2017.
 */
public class AdminNotificationSocketHelper {

    static private AdminNotificationSocketHelper instance;

    private WebSocket webSocket;

    private int attempts = 0;

    protected AdminNotificationSocketHelper() {
    }

    static public AdminNotificationSocketHelper getInstance() {
        if(instance == null) {
            instance = new AdminNotificationSocketHelper();
        }
        return instance;
    }

    public void startMaybeNotifications() {
        if(webSocket != null) {
            return;
        }
        String baseUrl = GWT.getHostPageBaseURL();
        webSocket = WebSocket.newWebSocketIfSupported();
        if (null != webSocket) {
            webSocket.setListener( new WebSocketListenerAdapter() {
                @Override
                public void onOpen( final WebSocket webSocket ) {
                    attempts = 0;
                }

                @Override
                public void onMessage( final WebSocket webSocket, final String data ) {
                    // check the type of message
                    AdminWebSocketMessageMapper mapper = GWT.create(AdminWebSocketMessageMapper.class);
                    AdminWebSocketMessage webSocketMessage = mapper.read(data);
                    switch (webSocketMessage.getType()) {
                        case notification:
                            NotificationEvent notificationEvent = new NotificationEvent();
                            notificationEvent.setNotification(webSocketMessage.getNotificationDTO());
                            Admin.clientFactory.getEventBus().fireEvent(notificationEvent);
                            break;
                    }
                }

                @Override
                public void onClose(WebSocket webSocket, boolean wasClean, int code, String reason) {
                    // TODO - needs a strategy to restart the socket
                    AdminNotificationSocketHelper.this.webSocket = null;
                    // normal close error code
                    if(code == 1000 || code == 1001) {
                        Admin.clientFactory.getEventBus().fireEvent(new WebSocketClosedEvent());
                        startMaybeNotifications();
                    } else {
                        // keep trying
                        Admin.clientFactory.getEventBus().fireEvent(new WebSocketFailedEvent());
                        attempts++;
                        new Timer() {

                            @Override
                            public void run() {
                                startMaybeNotifications();
                            }
                        }.schedule(3000 + (2000 * attempts));
                    }
                }
            } );
            webSocket.connect("ws://" + baseUrl.substring(baseUrl.indexOf("://") + 3) + "adminnotifications");
        }
    }
}
