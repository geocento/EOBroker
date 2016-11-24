package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Path;
import java.util.List;

@Path("/")
public class LoginResource extends com.geocento.webapps.eobroker.common.server.servlets.LoginResource implements com.geocento.webapps.eobroker.admin.client.services.LoginService{

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
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            em.close();
        }
        return loginInfo;
    }

}
