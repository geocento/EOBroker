package com.geocento.webapps.eobroker.supplier.client.utils;

import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.MessageEvent;
import com.geocento.webapps.eobroker.supplier.client.places.NotificationEvent;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierWebSocketMessage;
import com.geocento.webapps.eobroker.supplier.shared.dtos.WebSocketMessageMapper;
import com.google.gwt.core.client.GWT;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListenerAdapter;

/**
 * Created by thomas on 08/05/2017.
 */
public class SupplierNotificationSocketHelper {

    static private SupplierNotificationSocketHelper instance;

    private WebSocket webSocket;

    protected SupplierNotificationSocketHelper() {
    }

    static public SupplierNotificationSocketHelper getInstance() {
        if(instance == null) {
            instance = new SupplierNotificationSocketHelper();
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
                    WebSocketMessageMapper mapper = GWT.create(WebSocketMessageMapper.class);
                    SupplierWebSocketMessage webSocketMessage = mapper.read(data);
                    switch (webSocketMessage.getType()) {
                        case notification:
                            NotificationEvent notificationEvent = new NotificationEvent(webSocketMessage.getNotificationDTO());
                            Supplier.clientFactory.getEventBus().fireEvent(notificationEvent);
                            break;
                        case conversationMessage:
                        case requestMessage:
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(webSocketMessage.getType());
                            messageEvent.setDestination(webSocketMessage.getDestination());
                            messageEvent.setMessage(webSocketMessage.getMessageDTO());
                            Supplier.clientFactory.getEventBus().fireEvent(messageEvent);
                            break;
                    }
                }

                @Override
                public void onClose(WebSocket webSocket, boolean wasClean, int code, String reason) {
                    super.onClose(webSocket, wasClean, code, reason);
                    // TODO - needs a strategy to restart the socket
                }
            } );
            webSocket.connect("ws://" + baseUrl.substring(baseUrl.indexOf("://") + 3) + "suppliernotifications");
        }
    }
}
