package com.geocento.webapps.eobroker.supplier.server.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.UserSession;
import com.geocento.webapps.eobroker.common.server.websockets.BaseCustomConfigurator;
import com.geocento.webapps.eobroker.common.server.websockets.BaseNotificationSocket;
import com.geocento.webapps.eobroker.common.shared.entities.Conversation;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.server.websockets.NotificationSocket;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierWebSocketMessage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/suppliernotifications", configurator = BaseCustomConfigurator.class)
public class SupplierNotificationSocket extends BaseNotificationSocket {

    static Logger logger = Logger.getLogger(SupplierNotificationSocket.class);

    static private ConcurrentHashMap<Long, List<Session>> companySessions = new ConcurrentHashMap<Long, List<Session>>();
    static private ConcurrentHashMap<String, List<Session>> userSessions = new ConcurrentHashMap<String, List<Session>>();
    static private ConcurrentHashMap<String, List<Session>> conversationSubscriptions = new ConcurrentHashMap<String, List<Session>>();
/*
    static private ConcurrentHashMap<Long, List<Session>> companyConversationSubscriptions = new ConcurrentHashMap<Long, List<Session>>();
*/

    public SupplierNotificationSocket() {
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
        boolean firstCompanySession = false;
        // add company session
        {
            Long companyId = userSession.getUserCompanyId();
            List<Session> sessions = companySessions.get(companyId);
            if (sessions == null) {
                sessions = new ArrayList<Session>();
                companySessions.put(companyId, sessions);
            }
            sessions.add(session);
            firstCompanySession = sessions.size() == 1;
        }
        // add user session
        {
            String userName = userSession.getUserName();
            List<Session> sessions = userSessions.get(userName);
            if (sessions == null) {
                sessions = new ArrayList<Session>();
                userSessions.put(userName, sessions);
            }
            sessions.add(session);
/*
            if (httpSession != null) {
                httpSession.setAttribute("ADMINWEBSOCKET_SESSION", userName);
            }
*/
        }
        if(firstCompanySession) {
            notifyOnlineMaybe(userSession.getUserCompanyId(), true);
        }
    }

    private void notifyOnlineMaybe(Long companyId, boolean online) {
        // check if we need to notify users
        // look for conversations opened with the user's company
        EntityManager em = EMF.get().createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery("select c.id from Conversation c where c.id IN :ids and c.company.id = :companyId", String.class);
            query.setParameter("ids", new ArrayList<String>(conversationSubscriptions.keySet()));
            query.setParameter("companyId", companyId);
            List<String> conversationIds = query.getResultList();
            if(!ListUtil.isNullOrEmpty(conversationIds)) {
                for(String conversationId : conversationIds) {
                    WebSocketMessage webSocketMessage = new WebSocketMessage();
                    webSocketMessage.setType(WebSocketMessage.TYPE.conversationOnline);
                    webSocketMessage.setDestination(conversationId);
                    webSocketMessage.setConversationStatus(online);
                    try {
                        NotificationSocket.sendMessageSessions(conversationSubscriptions.get(conversationId), webSocketMessage);
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    private void removeUserSession(UserSession userSession, Session session) {
        super.removeHttpSession(session);
        {
            List<Session> sessions = companySessions.get(userSession.getUserCompanyId());
            if (sessions == null) {
                return;
            }
            sessions.remove(session);
        }
        {
            List<Session> sessions = userSessions.get(userSession.getUserName());
            if (sessions == null) {
                return;
            }
            sessions.remove(session);
        }
        // if no one left from the company notify the user
        if(ListUtil.isNullOrEmpty(companySessions.get(userSession.getUserCompanyId()))) {
            notifyOnlineMaybe(userSession.getUserCompanyId(), false);
        }
    }

    @OnMessage
    public String echo(String message, Session session) {
        if(message != null && message.startsWith("typing:")) {
            String conversationId = message.replace("typing:", "");
            List<Session> userSessions = conversationSubscriptions.get(conversationId);
            if(!ListUtil.isNullOrEmpty(userSessions)) {
                try {
                    NotificationSocket.broadcastTyping(userSessions, conversationId);
                } catch (JsonProcessingException e) {
                    logger.error("Could not send typing broadcast", e);
                }
            }
        }
        return null;
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

    static public void sendMessage(List<Session> sessions, SupplierWebSocketMessage webSocketMessage) throws JsonProcessingException {
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

    static public void sendCompanyMessage(Long companyId, SupplierWebSocketMessage webSocketMessage) throws JsonProcessingException {
        List<Session> sessions = companySessions.get(companyId);
        if(sessions == null) {
            return;
        }
        sendMessage(sessions, webSocketMessage);
    }

    static public void sendUserMessage(String userName, SupplierWebSocketMessage webSocketMessage) throws JsonProcessingException {
        List<Session> sessions = userSessions.get(userName);
        if(sessions == null) {
            return;
        }
        sendMessage(sessions, webSocketMessage);
    }

    static public void sendLogout(String sessionID) throws JsonProcessingException {
        SupplierWebSocketMessage supplierWebSocketMessage = new SupplierWebSocketMessage();
        supplierWebSocketMessage.setType(SupplierWebSocketMessage.TYPE.logout);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(supplierWebSocketMessage);
        sendHttpSessionMessage(sessionID, message);
    }

    private void sendLoggedOut(Session session) throws IOException {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setType(WebSocketMessage.TYPE.logout);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(webSocketMessage);
        session.getBasicRemote().sendText(message);
    }

    // for a customer to subscribe to conversation updates such as company online or company typing in
    public static Boolean subscribeConversation(Conversation conversation, Session session) {
        String conversationId = conversation.getId();
        List<Session> sessions = conversationSubscriptions.get(conversationId);
        if(sessions == null) {
            sessions = new ArrayList<Session>();
            conversationSubscriptions.put(conversationId, sessions);
        }
        sessions.add(session);
/*
        // add company to subscriptions
        Long companyId = conversation.getCompany().getId();
        List<Session> companySessions = companyConversationSubscriptions.get(companyId);
        if(companySessions == null) {
            companySessions = new ArrayList<Session>();
            companyConversationSubscriptions.put(companyId, companySessions);
        }
        companySessions.add(session);
*/
        // return whether or not company is online
        return !ListUtil.isNullOrEmpty(SupplierNotificationSocket.companySessions.get(conversation.getCompany().getId()));
    }

    public static Boolean unSubscribeConversation(String userName, Conversation conversation) {
        //supplierConversations.put(conversation.getCompany().getId(), conversation.getId());
        conversationSubscriptions.remove(conversation.getId(), userName);
        return !ListUtil.isNullOrEmpty(companySessions.get(conversation.getCompany().getId()));
    }
}
