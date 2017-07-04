package com.geocento.webapps.eobroker.customer.client.utils;

import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.MessageEvent;
import com.geocento.webapps.eobroker.customer.client.events.NotificationEvent;
import com.geocento.webapps.eobroker.customer.client.events.WebSocketClosedEvent;
import com.geocento.webapps.eobroker.customer.client.events.WebSocketFailedEvent;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListenerAdapter;

/**
 * Created by thomas on 08/05/2017.
 */
public class NotificationSocketHelper {

    static private NotificationSocketHelper instance;

    private WebSocket webSocket;

    private int attempts = 0;

    protected NotificationSocketHelper() {
    }

    static public NotificationSocketHelper getInstance() {
        if(instance == null) {
            instance = new NotificationSocketHelper();
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
                    WebSocketMessage.WebSocketMessageMapper mapper = GWT.create(WebSocketMessage.WebSocketMessageMapper.class);
                    WebSocketMessage webSocketMessage = mapper.read(data);
                    switch (webSocketMessage.getType()) {
                        case notification:
                            NotificationEvent notificationEvent = new NotificationEvent();
                            notificationEvent.setNotification(webSocketMessage.getNotificationDTO());
                            Customer.clientFactory.getEventBus().fireEvent(notificationEvent);
                            break;
                        case conversationMessage:
                        case requestMessage:
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setMessage(webSocketMessage.getMessageDTO());
                            messageEvent.setType(webSocketMessage.getType());
                            messageEvent.setDestination(webSocketMessage.getDestination());
                            Customer.clientFactory.getEventBus().fireEvent(messageEvent);
                            break;
                        case logout:
                            Window.alert("You have been signed out");
                            Window.Location.reload();
                            break;
                    }
                }

                @Override
                public void onClose(WebSocket webSocket, boolean wasClean, int code, String reason) {
                    // TODO - needs a strategy to restart the socket
                    NotificationSocketHelper.this.webSocket = null;
                    // normal close error code
                    if(code == 1000 || code == 1001) {
                        Customer.clientFactory.getEventBus().fireEvent(new WebSocketClosedEvent());
                        startMaybeNotifications();
                    } else {
                        // keep trying
                        Customer.clientFactory.getEventBus().fireEvent(new WebSocketFailedEvent());
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
            webSocket.connect("ws://" + baseUrl.substring(baseUrl.indexOf("://") + 3) + "notifications");
        }
    }
}
