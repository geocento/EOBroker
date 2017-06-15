package com.geocento.webapps.eobroker.common.server.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.common.server.UserSession;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/notifications", configurator = CustomConfigurator.class)
public class NotificationSocket {

    static Logger logger = Logger.getLogger(NotificationSocket.class);

    static private ConcurrentHashMap<String, List<Session>> userSessions = new ConcurrentHashMap<String, List<Session>>();

    private HttpSession httpSession;

    public NotificationSocket() {
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
        addUserSession(userSession.getUserName(), session);
        logger.info("Open session for user " + userSession.getUserName());
    }

    private void addUserSession(String userName, Session session) {
        List<Session> sessions = userSessions.get(userName);
        if(sessions == null) {
            sessions = new ArrayList<Session>();
            userSessions.put(userName, sessions);
        }
        sessions.add(session);
    }

    private void removeUserSession(String userName, Session session) {
        List<Session> sessions = userSessions.get(userName);
        if(sessions == null) {
            return;
        }
        sessions.remove(session);
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
        removeUserSession(userSession.getUserName(), session);
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

    static public void sendMessage(String userName, WebSocketMessage webSocketMessage) throws JsonProcessingException {
        List<Session> sessions = userSessions.get(userName);
        if(sessions == null) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(webSocketMessage);
        for(Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                // TODO - remove session?

            }
        }
    }
}
