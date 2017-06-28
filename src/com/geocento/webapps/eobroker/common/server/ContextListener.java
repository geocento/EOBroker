package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thomas on 06/06/2016.
 */
public class ContextListener implements ServletContextListener {

    Logger logger = Logger.getLogger(ContextListener.class);

    private Timer notificationsTimer;
    private Timer errorReportingTimer;
    private Timer supplierNotificationsTimer;

    private boolean reset = false;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("Application starting");
        // load configuration file
        try {
            Configuration.loadConfiguration();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // check all is ok with database
        EntityManager em = EMF.get().createEntityManager();
        em.close();

        // apply fixes if needed
        applyFixes();

        // start the email notification timer
        startNotifications();
        // start the supplier email notifications timer
        startSupplierNotifications();
        // start the reporting thread
        startReportingTimer();

        // send an email to say we have started the application
        MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.ADMIN);
        mailContent.addTitle("The " + ServerUtil.getSettings().getApplicationName() + " server application started");
        try {
            mailContent.sendEmail(ServerUtil.getUsersAdministrator(), "Server started", false);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void startReportingTimer() {

    }

    private void startSupplierNotifications() {

    }

    private void startNotifications() {
        notificationsTimer = new Timer();
        // run the checks every 5 minutes
        // TODO - problem when the server gets restarted, there is a number of notifications that do not get sent, use a sent flag instead?
        final long timerChecks = 30 * 60 * 1000L;
        notificationsTimer.schedule(new TimerTask() {

            public Date lastFetchedNotifications = new Date();

            @Override
            public void run() {
/*
                long notificationsDelay = 30 * ServerUtil.minuteInMs;
                try {
                    notificationsDelay = ServerUtil.getSettings().getNotificationDelay();
                } catch (Exception e) {
                    logger.error("No notification delay set, defaulting to 30 minutes");
                }
                // get all notifications which happend in the las   t timer checks time period
                EntityManager em = EMF.get().createEntityManager();
                try {
                    em.getTransaction().begin();
                    Date nowDate = new Date();
                    Date date = lastFetchedNotifications;
                    lastFetchedNotifications = nowDate;
                    // do only order notifications for now
                    TypedQuery<Notification> query = em.createQuery("SELECT n FROM Notification n WHERE n.type = :type AND n.creationDate > :fetchDate", Notification.class);
                    query.setParameter("type", Notification.TYPE.ORDER);
                    query.setParameter("fetchDate", date);
                    logger.debug("New notifications: " + query.getResultList().size());
                    // group notifications by users
                    HashMap<User, List<Notification>> notifications = ListUtil.group(query.getResultList(), new ListUtil.GetValue<User, Notification>() {
                        @Override
                        public User getLabel(Notification value) {
                            return value.getUser();
                        }

                        @Override
                        public List<Notification> createList() {
                            return new ArrayList<Notification>();
                        }
                    });
                    for(User user : notifications.keySet()) {
                        MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.CONSUMER);
                        mailContent.addTitle("You have new notifications on your orders");
                        mailContent.addTable(ListUtil.mutate(notifications.get(user), new ListUtil.Mutate<Notification, List<String>>() {
                            @Override
                            public List<String> mutate(Notification notification) {
                                EIOrdersPlace.TOKENS token = Notification.getPlace(notification.getLink()) == Notification.PLACES.USER_COVERAGE ? EIOrdersPlace.TOKENS.coverage : EIOrdersPlace.TOKENS.order;
                                return ListUtil.toList(new String[] {
                                        notification.getMessage(), ", on " + timeFormat.format(notification.getLastUpdate()),
                                        "<a href='" + createUserLink(new EIOrdersPlace(Utils.generateTokens(token.toString(), Notification.getValue(notification.getLink())))) +
                                                "'>View</a>"
                                });
                            }
                        }));
                        mailContent.addAction("view your Orders", null, ServerUtil.getApplicationSettings().getWebsiteUrl() + "#orders:");
                        mailContent.sendEmail(user, "New notifications on your orders", false);
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    logger.error("Error running notification email service. Reason is " + e.getMessage());
                } finally {
                    em.close();
                }
*/
            }
        }, 1000, timerChecks);
    }

    private void applyFixes() {
        // reset the db content if needed
        EntityManager em = EMF.get().createEntityManager();
        try {
            {
                // check admin settings exist
                if(ServerUtil.getSettings() == null) {
                    em.getTransaction().begin();
                    ApplicationSettings applicationSettings = new ApplicationSettings();
                    applicationSettings.setApplicationName(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.applicationName));
                    applicationSettings.setDataDirectory(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.uploadPath));
                    applicationSettings.setEmailAccount(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.email_account));
                    applicationSettings.setEmailFrom(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.email_from));
                    applicationSettings.setEmailPassword(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.email_password));
                    applicationSettings.setEmailPort(Configuration.getIntProperty(Configuration.APPLICATION_SETTINGS.email_port));
                    applicationSettings.setEmailServer(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.email_host));
                    applicationSettings.setSmtps(Configuration.getBooleanProperty(Configuration.APPLICATION_SETTINGS.email_issmpts));
                    applicationSettings.setReportByEmail(true);
                    applicationSettings.setGeoserverOWS(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverOWS));
                    applicationSettings.setGeoserverRESTUri(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverRESTUri));
                    applicationSettings.setGeoserverUser(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverUser));
                    applicationSettings.setGeoserverPassword(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverPassword));
                    em.persist(applicationSettings);
                    em.getTransaction().commit();
                }
            }
            {
                // add some data to database
                TypedQuery<Request> query = em.createQuery("select r from Request r where r.status is NULL", Request.class);
                List<Request> requests = query.getResultList();
                if (requests.size() > 0) {
                    em.getTransaction().begin();
                    for (Request request : query.getResultList()) {
                        request.setStatus(Request.STATUS.submitted);
                    }
                    em.getTransaction().commit();
                }
            }
            // make sure each company has a workspace
            {
                GeoServerRESTReader geoserverReader = GeoserverUtils.getGeoserverReader();
                GeoServerRESTPublisher geoserverPublisher = GeoserverUtils.getGeoserverPublisher();
                TypedQuery<Company> query = em.createQuery("select c from Company c", Company.class);
                List<Company> companies = query.getResultList();
                if (companies.size() > 0) {
                    List<String> workspaces = geoserverReader.getWorkspaceNames();
                    for (Company company : query.getResultList()) {
                        String workspace = company.getId() + "";
                        if(!workspaces.contains(workspace)) {
                            // create workspace
                            geoserverPublisher.createWorkspace(workspace);
                        }
                    }
                }
            }
            {
                // initialise the hostedData flag in data access
                TypedQuery<DatasetAccess> query = em.createQuery("select d from DatasetAccess d where d.hostedData is NULL", DatasetAccess.class);
                List<DatasetAccess> datasetAccesses = query.getResultList();
                if (datasetAccesses.size() > 0) {
                    em.getTransaction().begin();
                    for (DatasetAccess datasetAccess : datasetAccesses) {
                        datasetAccess.setHostedData(
                                !(datasetAccess.getUri() != null && datasetAccess.getUri().contains("eobroker.com/")) &&
                                        !(datasetAccess instanceof DatasetAccessOGC && ((DatasetAccessOGC) datasetAccess).getServerUrl() != null && ((DatasetAccessOGC) datasetAccess).getServerUrl().contains("eobroker.com/")));
                    }
                    em.getTransaction().commit();
                }
            }
            {
                // check all companies have their supplier settings
                TypedQuery<Company> query = em.createQuery("select c from Company c where c.settings is NULL", Company.class);
                List<Company> companies = query.getResultList();
                if (companies.size() > 0) {
                    em.getTransaction().begin();
                    for (Company company : companies) {
                        SupplierSettings supplierSettings = new SupplierSettings();
                        em.persist(supplierSettings);
                        supplierSettings.setCompany(company);
                        company.setSettings(supplierSettings);
                    }
                    em.getTransaction().commit();
                }
            }
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if(notificationsTimer != null) {
            notificationsTimer.cancel();
            notificationsTimer = null;
        }
        if(supplierNotificationsTimer != null) {
            supplierNotificationsTimer.cancel();
            supplierNotificationsTimer = null;
        }
        if(errorReportingTimer != null) {
            errorReportingTimer.cancel();
            errorReportingTimer = null;
        }
    }
}
