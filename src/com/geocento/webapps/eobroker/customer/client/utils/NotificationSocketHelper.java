package com.geocento.webapps.eobroker.customer.client.utils;

import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.MessageEvent;
import com.geocento.webapps.eobroker.customer.client.events.NotificationEvent;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import com.google.gwt.core.client.GWT;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListenerAdapter;

/**
 * Created by thomas on 08/05/2017.
 */
public class NotificationSocketHelper {

    static private NotificationSocketHelper instance;

    private WebSocket webSocket;

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
                        case message:
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setMessage(webSocketMessage.getMessageDTO());
                            Customer.clientFactory.getEventBus().fireEvent(messageEvent);
                    }
                }

                @Override
                public void onClose(WebSocket webSocket, boolean wasClean, int code, String reason) {
                    super.onClose(webSocket, wasClean, code, reason);
                    // TODO - needs a strategy to restart the socket
                }
            } );
            webSocket.connect("ws://" + baseUrl.substring(baseUrl.indexOf("://") + 3) + "notifications");
        }
    }
}
