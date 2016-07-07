package com.geocento.webapps.eobroker.customer.server.utils;

import com.geocento.webapps.eobroker.common.server.UserSession;
import com.geocento.webapps.eobroker.common.shared.AuthorizationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by thomas on 13/06/2016.
 */
public class UserUtils {

    public static String verifyUser(HttpServletRequest request) throws AuthorizationException {
        HttpSession session = request.getSession(true);
        if(session != null) {
            UserSession userSession = (UserSession) session.getAttribute("userSession");
            if(userSession != null) {
                return userSession.getUserName();
            }
        }
        throw new AuthorizationException();
    }

}
