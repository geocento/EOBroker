package com.geocento.webapps.eobroker.common.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.UserSession;
import com.geocento.webapps.eobroker.common.server.Utils.BCrypt;
import com.geocento.webapps.eobroker.common.server.Utils.UserUtils;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.geocento.webapps.eobroker.supplier.client.services.LoginService;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.Date;

@Path("/")
public class LoginResource implements LoginService {

    Logger logger = Logger.getLogger(LoginResource.class);

    public LoginResource() {
        logger.info("Starting service...");
    }

    // assumes service is not a singleton
    @Context HttpServletRequest request;

    @Override
    public LoginInfo signin(String userName, String passwordHash) {
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            if(user != null) {
                String hashFromDB = user.getPassword();
                boolean valid = BCrypt.checkpw(passwordHash, hashFromDB);
                if(valid) {
                    createUserSession(user, request);
                    LoginInfo loginInfo = UserUtils.getLoginInfo(user);
                    try {
                        em.getTransaction().begin();
/*
                        if(user.getUserStatus() == User.USER_STATUS.SIGNED_UP) {
                            // check if we have a registration token
                            if(registration != null && BCrypt.checkpw(user.getUsername(), registration)) {
                                user.setUserStatus(User.USER_STATUS.EMAILVERIFIED);
                            } else {
                                throw new EIException("User email not yet verified");
                            }
                        }
*/
                        user.setLastLoggedIn(new Date());
                        em.getTransaction().commit();
                    } catch(Exception e) {
                        if(em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        logger.error(e.getMessage(), e);
                    }
                    return loginInfo;
                }
            }
        } catch(Exception e) {
            logger.error(e.getMessage());
        } finally {
            if(em != null) {
                em.close();
            }
        }
        return null;
    }

    @Override
    public void signout() {
        HttpSession session = request.getSession(true);
        if(session != null) {
            session.invalidate();
        }
    }

    @Override
    public LoginInfo getSession() {
        HttpSession session = request.getSession(true);
        if(session != null) {
            UserSession userSession = (UserSession) session.getAttribute("userSession");
            if(userSession != null) {
                return UserUtils.getLoginInfo(UserUtils.findUser(userSession.getUserName()));
            }
        }
        return null;
    }

    public static UserSession createUserSession(User user, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        UserSession userSession = new UserSession(user);
        session.setAttribute("userSession", userSession);
        return userSession;
    }

}
