package com.geocento.webapps.eobroker.customer.client.utils;

import com.google.gwt.user.client.Window;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListenerAdapter;

/**
 * Created by thomas on 08/05/2017.
 */
public class NotificationSocketHelper {

    public NotificationSocketHelper(String webSocketURL) {
        final WebSocket webSocket = WebSocket.newWebSocketIfSupported();
        if ( null != webSocket )
        {
            webSocket.setListener( new WebSocketListenerAdapter()
            {
                @Override
                public void onOpen( final WebSocket webSocket ) {
                    //Window.alert("Websocket opened");
                    // After we have connected we can send
                    //webSocket.send("Hello!");
                }

                @Override
                public void onMessage( final WebSocket webSocket, final String data ) {
                    Window.alert("New message " + data);
                }
            } );
            webSocket.connect(webSocketURL);
        }
    }

}
