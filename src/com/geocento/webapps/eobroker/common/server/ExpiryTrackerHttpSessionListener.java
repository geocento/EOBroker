package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.admin.server.websockets.AdminNotificationSocket;
import com.geocento.webapps.eobroker.admin.shared.dtos.AdminWebSocketMessage;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.customer.server.websockets.NotificationSocket;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import com.geocento.webapps.eobroker.supplier.server.websockets.SupplierNotificationSocket;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierWebSocketMessage;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;

@WebListener
public class ExpiryTrackerHttpSessionListener 
        implements HttpSessionListener {

    Logger logger = Logger.getLogger(ExpiryTrackerHttpSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        logger.info("HTTP session destroyed");
        HttpSession httpSession = event.getSession();
        // TODO - implement equivalent ones for the other applications?
        UserSession userSession = (UserSession) httpSession.getAttribute("userSession");
        if(userSession != null) {
            User.USER_ROLE userRole = userSession.getUserRole();
            String userName = userSession.getUserName();
            logger.info("User signed off is " + userName);
            if (userRole == User.USER_ROLE.administrator) {
                try {
                    AdminWebSocketMessage adminWebSocketMessage = new AdminWebSocketMessage();
                    adminWebSocketMessage.setType(AdminWebSocketMessage.TYPE.logout);
                    AdminNotificationSocket.sendUserMessage(userName, adminWebSocketMessage);
                } catch (IOException ex) {
                }
            }
            if (userRole == User.USER_ROLE.supplier || userRole == User.USER_ROLE.administrator) {
                try {
                    SupplierWebSocketMessage supplierWebSocketMessage = new SupplierWebSocketMessage();
                    supplierWebSocketMessage.setType(SupplierWebSocketMessage.TYPE.logout);
                    SupplierNotificationSocket.sendUserMessage(userName, supplierWebSocketMessage);
                } catch (IOException ex) {
                }
            }
            try {
                WebSocketMessage webSocketMessage = new WebSocketMessage();
                webSocketMessage.setType(WebSocketMessage.TYPE.logout);
                NotificationSocket.sendMessage(userName, webSocketMessage);
            } catch (IOException ex) {
            }
        }
    }
}
