package com.geocento.webapps.eobroker.supplier.server.util;

import com.geocento.webapps.eobroker.common.server.UserSession;
import com.geocento.webapps.eobroker.common.shared.AuthorizationException;
import com.geocento.webapps.eobroker.common.shared.entities.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by thomas on 13/06/2016.
 */
public class UserUtils {

    public static String verifyUserSupplier(HttpServletRequest request) throws AuthorizationException {
        HttpSession session = request.getSession(true);
        if(session != null) {
            UserSession userSession = (UserSession) session.getAttribute("userSession");
            if(userSession != null && userSession.getUserRole() == User.USER_ROLE.supplier) {
                return userSession.getUserName();
            }
        }
        throw new AuthorizationException();
    }

}
