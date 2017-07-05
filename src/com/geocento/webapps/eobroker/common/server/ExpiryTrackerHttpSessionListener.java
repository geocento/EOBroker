package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.admin.server.websockets.AdminNotificationSocket;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.customer.server.websockets.NotificationSocket;
import com.geocento.webapps.eobroker.supplier.server.websockets.SupplierNotificationSocket;
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
        String sessionID = httpSession.getId();
        // TODO - implement equivalent ones for the other applications?
        UserSession userSession = (UserSession) httpSession.getAttribute("userSession");
        if(userSession != null) {
            User.USER_ROLE userRole = userSession.getUserRole();
            String userName = userSession.getUserName();
            logger.info("User signed off is " + userName);
            if (userRole == User.USER_ROLE.administrator) {
                try {
                    AdminNotificationSocket.sendLogout(sessionID);
                } catch (IOException ex) {
                }
            }
            if (userRole == User.USER_ROLE.supplier || userRole == User.USER_ROLE.administrator) {
                try {
                    SupplierNotificationSocket.sendLogout(sessionID);
                } catch (IOException ex) {
                }
            }
            try {
                NotificationSocket.sendLogout(sessionID);
            } catch (IOException ex) {
            }
        }
    }
}
