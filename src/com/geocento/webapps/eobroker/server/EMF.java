package com.geocento.webapps.eobroker.server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
    private static final EntityManagerFactory emfInstance =
        Persistence.createEntityManagerFactory("eobroker");

    private EMF() {}

    public static EntityManagerFactory get() {
        return emfInstance;
    }
}
