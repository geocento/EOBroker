package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.MailContent;
import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.server.Utils.UserUtils;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.geocento.webapps.eobroker.customer.server.entities.UserResetToken;
import com.geocento.webapps.eobroker.customer.server.utils.StatsHelper;
import com.google.gwt.http.client.RequestException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Path;
import java.util.Date;
import java.util.List;

@Path("/")
public class LoginResource extends com.geocento.webapps.eobroker.common.server.servlets.LoginResource implements com.geocento.webapps.eobroker.customer.client.services.LoginService {

    @Override
    protected LoginInfo getLoginInfo(User user) {
        LoginInfo loginInfo = super.getLoginInfo(user);
        EntityManager em = EMF.get().createEntityManager();
        try {
            TypedQuery<AoI> query = em.createQuery("select a from AoI a where a.user = :user order by a.lastAccessed DESC", AoI.class);
            query.setParameter("user", user);
            query.setMaxResults(1);
            List<AoI> dbAoIs = query.getResultList();
            if (dbAoIs.size() > 0) {
                AoI dbAoI = dbAoIs.get(0);
                AoIDTO aoIDTO = new AoIDTO();
                aoIDTO.setId(dbAoI.getId());
                aoIDTO.setName(dbAoI.getName());
                aoIDTO.setWktGeometry(dbAoI.getGeometry());
                loginInfo.setAoIDTO(aoIDTO);
            }
            StatsHelper.addCounter("users.signin", 1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            em.close();
        }
        return loginInfo;
    }

    @Override
    public void resetPassword(String accountName) throws RequestException {
        EntityManager em = null;
        try {
            em = EMF.get().createEntityManager();
            // generate a rest password token and send a link to the user
            // find the user
            User user = UserUtils.findUserByNameOrEmail(em, accountName);
            if(user == null) {
                throw new RequestException("Unknown user name or email");
            }
            // create a token
            UserResetToken token = new UserResetToken(user.getUsername(), 2 * 24 * 3600 * 1000);
            // save it to the database
            em.getTransaction().begin();
            em.persist(token);
            em.getTransaction().commit();
            try {
                // now send the email to the user with the link
                String websiteUrl = ServerUtil.getSettings().getWebsiteUrl();
                String linkUrl = websiteUrl + "#pwdrset:username=" + user.getUsername() + "&resetToken=" + token.getToken();
                MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.CONSUMER);
                mailContent.addTitle("Reset your EO Broker password");
                mailContent.addLine("You have requested to have your EO Broker password reset.");
                mailContent.addAction("reset your EO Broker password", null, linkUrl);
                mailContent.sendEmail(user.getEmail(), "Reset your EO Broker password", false);
            } catch(Exception e) {
                logger.error(e.getMessage());
                throw new Exception("Could not send email, please try again...");
            }
            StatsHelper.addCounter("users.reset", 1);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error on the server side.");
        } finally {
            if(em != null) {
                em.close();
            }
        }
    }

    @Override
    public void changePassword(String userName, String resetToken, String password) throws RequestException {
        // check the values
        if(userName == null || resetToken == null || userName.length() == 0 || resetToken.length() == 0 || password == null || password.length() < 5) {
            throw new RequestException("Invalid parameters");
        }
        // check that the token exists
        EntityManager em = null;
        try {
            em = EMF.get().createEntityManager();
            em.getTransaction().begin();
            TypedQuery<UserResetToken> query = em.createQuery("select u from UserResetToken u where u.userName = :userName", UserResetToken.class);
            query.setParameter("userName", userName);
            List<UserResetToken> result = query.getResultList();
            if(result == null || result.size() == 0) {
                throw new RequestException("Token is not valid");
            }
            // scan list to find one with the same reset token
            UserResetToken validToken = null;
            for(UserResetToken token : result) {
                if(token.getToken().contentEquals(resetToken)) {
                    validToken = token;
                }
            }
            if(validToken == null) {
                throw new RequestException("Token is not valid");
            }
            if(validToken.getExpiryDate().before(new Date())) {
                throw new RequestException("Token is out of date");
            }
            // token is valid
            // update the user password
            User dbUser = em.find(User.class, userName);
            dbUser.setPassword(UserUtils.createPasswordHash(password));
            em.merge(dbUser);
            // remove the old tokens
            for(UserResetToken token : result) {
                em.remove(token);
            }
            em.getTransaction().commit();
            StatsHelper.addCounter("users.changepassword", 1);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error on the server side.");
        } finally {
            if(em != null) {
                em.close();
            }
        }
    }
}
