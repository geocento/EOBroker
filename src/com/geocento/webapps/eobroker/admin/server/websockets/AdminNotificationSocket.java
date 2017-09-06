package com.geocento.webapps.eobroker.admin.server.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.admin.shared.dtos.AdminWebSocketMessage;
import com.geocento.webapps.eobroker.common.server.UserSession;
import com.geocento.webapps.eobroker.common.server.websockets.BaseCustomConfigurator;
import com.geocento.webapps.eobroker.common.server.websockets.BaseNotificationSocket;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/adminnotifications", configurator = BaseCustomConfigurator.class)
public class AdminNotificationSocket extends BaseNotificationSocket {

    static Logger logger = Logger.getLogger(AdminNotificationSocket.class);

    static private ConcurrentHashMap<String, List<Session>> userSessions = new ConcurrentHashMap<String, List<Session>>();

    public AdminNotificationSocket() {
        logger.info("Starting websocket handler");
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        // set maximum time out to 30 minutes
        session.setMaxIdleTimeout(30 * 60 * 1000L);
        UserSession userSession = getUserSession();
        if(userSession == null) {
            sendLoggedOut(session);
            throw new IOException("Not signed in");
        }
        addUserSession(userSession, session);
        logger.info("Open session for user " + userSession.getUserName());
    }

    private void addUserSession(UserSession userSession, Session session) {
        super.addHttpSession(session);
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
        super.removeHttpSession(session);
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
        handleError(t);
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

    static public void sendUserMessage(String userName, AdminWebSocketMessage webSocketMessage) throws JsonProcessingException {
        if(userSessions == null || userSessions.size() == 0) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(webSocketMessage);
        Collection<String> userNames = userName == null ? ListUtil.toList(userName) : userSessions.keySet();
        for(String sessionUserName : userNames) {
            for (Session session : userSessions.get(sessionUserName)) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    // TODO - remove session?

                }
            }
        }
    }

    static public void sendAllMessage(AdminWebSocketMessage webSocketMessage) throws JsonProcessingException {
        sendUserMessage(null, webSocketMessage);
    }

    static public void sendLogout(String sessionID) throws JsonProcessingException {
        AdminWebSocketMessage adminWebSocketMessage = new AdminWebSocketMessage();
        adminWebSocketMessage.setType(AdminWebSocketMessage.TYPE.logout);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(adminWebSocketMessage);
        sendHttpSessionMessage(sessionID, message);
    }

    private void sendLoggedOut(Session session) throws IOException {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setType(WebSocketMessage.TYPE.logout);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(webSocketMessage);
        session.getBasicRemote().sendText(message);
    }

}
