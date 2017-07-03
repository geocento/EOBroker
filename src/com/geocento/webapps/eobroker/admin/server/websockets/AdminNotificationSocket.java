package com.geocento.webapps.eobroker.admin.server.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.admin.shared.dtos.AdminWebSocketMessage;
import com.geocento.webapps.eobroker.common.server.UserSession;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/adminnotifications", configurator = AdminCustomConfigurator.class)
public class AdminNotificationSocket {

    static Logger logger = Logger.getLogger(AdminNotificationSocket.class);

    static private ConcurrentHashMap<String, List<Session>> userSessions = new ConcurrentHashMap<String, List<Session>>();

    private HttpSession httpSession;

    public AdminNotificationSocket() {
        logger.info("Starting websocket handler");
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        // set maximum time out to 30 minutes
        session.setMaxIdleTimeout(30 * 60 * 1000L);
        UserSession userSession = getUserSession();
        if(userSession == null) {
            throw new IOException("Not signed in");
        }
        addUserSession(userSession, session);
        logger.info("Open session for user " + userSession.getUserName());
    }

    private void addUserSession(UserSession userSession, Session session) {
        // add user session
        {
            String userName = userSession.getUserName();
            List<Session> sessions = userSessions.get(userName);
            if (sessions == null) {
                sessions = new ArrayList<Session>();
                userSessions.put(userName, sessions);
            }
            sessions.add(session);
        }
    }

    private void removeUserSession(UserSession userSession, Session session) {
        {
            List<Session> sessions = userSessions.get(userSession.getUserCompanyId());
            if (sessions == null) {
                return;
            }
            sessions.remove(session);
        }
    }

    @OnMessage
    public String echo(String message) {
        return message + " (from your server)";
    }

    @OnError
    public void onError(Throwable t) {
        logger.error(t.getMessage(), t);
    }

    @OnClose
    public void onClose(Session session) {
        UserSession userSession = getUserSession();
        if(userSession == null) {
            // TODO - find a strategy
            // we need a parallel process to remove sessions when the http session has expired
            return;
        }
        removeUserSession(userSession, session);
        logger.info("Close session for user " + userSession.getUserName());
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public UserSession getUserSession() {
        if(httpSession == null) {
            return null;
        }
        return (UserSession) httpSession.getAttribute("userSession");
    }

    static public void sendMessage(AdminWebSocketMessage webSocketMessage) throws JsonProcessingException {
        if(userSessions == null || userSessions.size() == 0) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(webSocketMessage);
        for(String userName : userSessions.keySet()) {
            for (Session session : userSessions.get(userName)) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    // TODO - remove session?

                }
            }
        }
    }
}
