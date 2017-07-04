package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by thomas on 06/06/2016.
 */
public class ContextListener implements ServletContextListener {

    static private SimpleDateFormat timeFormat = new SimpleDateFormat("YYYY-MMM-dd 'at' hh:mm:ss");
    static {
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    Logger logger = Logger.getLogger(ContextListener.class);

    private Timer notificationsTimer;
    private Timer errorReportingTimer;
    private Timer supplierNotificationsTimer;

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
        supplierNotificationsTimer = new Timer();
        // run the checks every 5 minutes
        final long timerChecks = 5 * ServerUtil.minuteInMs;
        supplierNotificationsTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                // get all notifications which happend in the las   t timer checks time period
                EntityManager em = EMF.get().createEntityManager();
                try {
                    em.getTransaction().begin();
                    Date now = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now);
                    calendar.add(Calendar.MINUTE, ServerUtil.getSettings().getNotificationDelay());
                    // TODO - first get the users which are concerned by this new round of notifications
                    TypedQuery<SupplierNotification> notificationQuery = em.createQuery("SELECT s FROM SupplierNotification s " +
                            "WHERE s.sent = :sent AND (s.company.lastNotificationCheck is NULL OR s.company.lastNotificationCheck < :lastNotificationCheck)", SupplierNotification.class);
                    notificationQuery.setParameter("sent", false);
                    notificationQuery.setParameter("lastNotificationCheck", calendar.getTime());
                    logger.debug("New supplier notifications: " + notificationQuery.getResultList().size());
                    // group notifications by users
                    HashMap<Company, List<SupplierNotification>> notifications = ListUtil.group(notificationQuery.getResultList(), new ListUtil.GetValue<Company, SupplierNotification>() {
                        @Override
                        public Company getLabel(SupplierNotification value) {
                            return value.getCompany();
                        }

                        @Override
                        public List<SupplierNotification> createList() {
                            return new ArrayList<SupplierNotification>();
                        }
                    });
                    // send emails
                    for (Company company : notifications.keySet()) {
                        // TODO - only send notifications based on user preferences
                        List<SupplierNotification> companyNotifications = notifications.get(company);
                        try {
                            MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.ORDER);
                            mailContent.addTitle("You have new notifications");
                            mailContent.addTable(ListUtil.mutate(companyNotifications, notification -> ListUtil.toList(new String[]{
                                    notification.getMessage(), "on " + timeFormat.format(notification.getCreationDate()),
                                    "<a href='" + ServerUtil.getSettings().getSupplierWebsiteUrl() + "#notifications:id=" + notification.getId() +
                                            "'>View</a>"
                            })));
                            mailContent.addAction("view all notifications", null, ServerUtil.getSettings().getSupplierWebsiteUrl() + "#notifications:");
                            // send email to all users within the company which are supplier users
                            TypedQuery<User> supplierUsersQuery = em.createQuery("select u from users u where u.company = :company AND u.role = :role", User.class);
                            supplierUsersQuery.setParameter("company", company);
                            supplierUsersQuery.setParameter("role", User.USER_ROLE.supplier);
                            for(User user : supplierUsersQuery.getResultList()) {
                                mailContent.sendEmail(user.getEmail(), "New notifications on EO Broker", false);
                            }
                            // update notification
                            for(SupplierNotification notification : companyNotifications) {
                                notification.setSent(true);
                            }
                            company.setLastNotificationCheck(now);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    logger.error("Error running notification email service. Reason is " + e.getMessage());
                } finally {
                    em.close();
                }
            }
        }, 1000, timerChecks);
    }

    private void startNotifications() {
        notificationsTimer = new Timer();
        // run the checks every 5 minutes
        final long timerChecks = 5 * ServerUtil.minuteInMs;
        notificationsTimer.schedule(new TimerTask() {

            public Date lastFetchedNotifications = new Date();

            @Override
            public void run() {
                // get all notifications which happend in the las   t timer checks time period
                EntityManager em = EMF.get().createEntityManager();
                try {
                    em.getTransaction().begin();
                    Date now = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now);
                    calendar.add(Calendar.MINUTE, ServerUtil.getSettings().getNotificationDelay());
                    // TODO - optimise the query as when many users are registered it might end up a problem
                    TypedQuery<Notification> notificationQuery = em.createQuery("SELECT n FROM Notification n WHERE n.sent = :sent AND (n.user.lastNotificationCheck is NULL OR n.user.lastNotificationCheck < :lastNotificationCheck)", Notification.class);
                    notificationQuery.setParameter("sent", false);
                    notificationQuery.setParameter("lastNotificationCheck", calendar.getTime());
                    logger.debug("New notifications: " + notificationQuery.getResultList().size());
                    // group notifications by users
                    HashMap<User, List<Notification>> notifications = ListUtil.group(notificationQuery.getResultList(), new ListUtil.GetValue<User, Notification>() {
                        @Override
                        public User getLabel(Notification value) {
                            return value.getUser();
                        }

                        @Override
                        public List<Notification> createList() {
                            return new ArrayList<Notification>();
                        }
                    });
                    // send emails
                    for (User user : notifications.keySet()) {
                        // TODO - only send notifications based on user preferences
                        List<Notification> userNotifications = notifications.get(user);
                        try {
                            MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.CONSUMER);
                            mailContent.addTitle("You have new notifications");
                            mailContent.addTable(ListUtil.mutate(userNotifications, new ListUtil.Mutate<Notification, List<String>>() {
                                @Override
                                public List<String> mutate(Notification notification) {
                                    return ListUtil.toList(new String[]{
                                            notification.getMessage(), ", on " + timeFormat.format(notification.getCreationDate()),
                                            "<a href='" + ServerUtil.getSettings().getWebsiteUrl() + "#notifications:id=" + notification.getId() +
                                                    "'>View</a>"
                                    });
                                }
                            }));
                            mailContent.addAction("view all notifications", null, ServerUtil.getSettings().getWebsiteUrl() + "#notifications:");
                            mailContent.sendEmail(user.getEmail(), "New notifications on EO Broker", false);
                            // update notification
                            for(Notification notification : userNotifications) {
                                notification.setSent(true);
                            }
                            user.setLastNotificationCheck(now);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    logger.error("Error running notification email service. Reason is " + e.getMessage());
                } finally {
                    em.close();
                }
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
            {
                // check all companies have their supplier settings
                TypedQuery<User> query = em.createQuery("select u from users u where u.status is NULL", User.class);
                List<User> users = query.getResultList();
                if (users.size() > 0) {
                    em.getTransaction().begin();
                    for (User user : users) {
                        user.setStatus(REGISTRATION_STATUS.APPROVED);
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
