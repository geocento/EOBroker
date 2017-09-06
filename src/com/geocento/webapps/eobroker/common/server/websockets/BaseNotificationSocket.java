package com.geocento.webapps.eobroker.common.server.websockets;

import com.geocento.webapps.eobroker.common.server.UserSession;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by thomas on 04/07/2017.
 */
public class BaseNotificationSocket {

    static Logger logger = Logger.getLogger(BaseNotificationSocket.class);

    static private ConcurrentHashMap<String, List<Session>> httpSessions = new ConcurrentHashMap<String, List<Session>>();

    private HttpSession httpSession;

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    protected UserSession getUserSession() {
        if(httpSession == null) {
            return null;
        }
        return (UserSession) httpSession.getAttribute("userSession");
    }

    public void addHttpSession(Session session) {
        // add http session
        {
            String sessionId = httpSession.getId();
            List<Session> sessions = httpSessions.get(sessionId);
            if (sessions == null) {
                sessions = new ArrayList<Session>();
                httpSessions.put(sessionId, sessions);
            }
            sessions.add(session);
        }
    }

    public void removeHttpSession(Session session) {
        {
            String sessionId = httpSession.getId();
            List<Session> sessions = httpSessions.get(sessionId);
            if (sessions == null) {
                return;
            }
            sessions.remove(session);
        }
    }

    protected void handleError(Throwable t) {
        if(t instanceof SocketTimeoutException) {
            logger.error(t.getMessage());
        } else {
            logger.error(t.getMessage(), t);
        }
    }

    static public void sendHttpSessionMessage(String sessionId, String message) {
        if(httpSessions == null || httpSessions.size() == 0 || !httpSessions.containsKey(sessionId)) {
            return;
        }
        for (Session session : httpSessions.get(sessionId)) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                // TODO - remove session?

            }
        }
    }

}
