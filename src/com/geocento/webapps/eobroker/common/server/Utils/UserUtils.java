package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 13/06/2016.
 */
public class UserUtils {

    static public User createUser(String name, String password, User.USER_ROLE role, List<User.USER_PERMISSION> permissions, Company company) {
        String passwordHash = createPasswordHash(password);
        User user = new User();
        user.setUsername(name);
        user.setPassword(passwordHash);
        user.setRole(role);
        user.setPermissions(permissions);
        user.setCompany(company);
        user.setCreationDate(new Date());
        return user;
    }

    public static String createPasswordHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static LoginInfo getLoginInfo(User user) {
        return new LoginInfo(user);
    }

    public static User findUser(String userName) {
        EntityManager em = null;
        try {
            em = EMF.get().createEntityManager();
            return em.find(User.class, userName);
        } finally {
            if(em != null) {
                em.close();
            }
        }
    }

    public static User findUserByNameOrEmail(EntityManager em, String userName) {
        if(userName.contains("@")) {
            TypedQuery<User> query = em.createQuery("select u from users u WHERE u.email = :email", User.class);
            query.setParameter("email", userName);
            List<User> results = query.getResultList();
            if(results == null || results.size() == 0) {
                return null;
            } else {
                return results.get(0);
            }
        } else {
            return em.find(User.class, userName);
        }
    }
}
