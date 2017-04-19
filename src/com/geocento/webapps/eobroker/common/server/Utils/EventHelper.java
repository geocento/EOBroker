package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.subscriptions.Event;
import com.geocento.webapps.eobroker.common.shared.entities.subscriptions.Following;
import com.geocento.webapps.eobroker.common.shared.entities.subscriptions.FollowingEvent;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;

/**
 * Created by thomas on 19/04/2017.
 */
public class EventHelper {

    public static void createAndPropagateCompanyEvent(EntityManager em, Company company, Category productservices, Event.TYPE type, String message, String linkId) {
        Event event = new Event();
        event.setMessage(message);
        event.setType(type);
        event.setLinkId(linkId);
        event.setCreationDate(new Date());
        em.persist(event);
        // TODO - move this to another separate service?
        // now propagate
        // look for followers
        TypedQuery<Following> query = em.createQuery("select f from Following f where f.company = :company", Following.class);
        query.setParameter("company", company);
        for(Following following : query.getResultList()) {
            // create events
            FollowingEvent followingEvent = new FollowingEvent();
            followingEvent.setEvent(event);
            followingEvent.setUser(following.getUser());
            followingEvent.setCreationDate(new Date());
            em.persist(followingEvent);
        }
    }

}
