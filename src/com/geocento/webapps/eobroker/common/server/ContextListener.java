package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.server.Utils.GISUtils;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.AdminNotification;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.apache.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
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

    // notification timers
    private Timer notificationsTimer;
    private Timer adminReportingTimer;
    private Timer supplierNotificationsTimer;

    // clean up timer
    private Timer cleanupTimer;

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

        System.setProperty("org.geotools.referencing.forceXY", "true");

        // apply fixes if needed
        applyFixes();

/*
        // quick check of libraries
        try {
            CoordinateReferenceSystem geo = CRS.decode("EPSG:4326", false);
            File shapeFile = new File("C:\\Users\\thomas\\Downloads\\bristol1\\bristol1.shp");
            GISUtils.reprojectShapefile(new ShapefileDataStore(DataUtilities.fileToURL(shapeFile)), geo, new File(shapeFile.getParent(), "_" + shapeFile.getName()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
*/

        // TODO - check that it works before erasing the data
        // clean up the samples
        //cleanupSamples();

        // start the email notification timer
        startNotifications();
        // start the supplier email notifications timer
        startSupplierNotifications();
        // start the reporting thread
        startAdminTimer();
        // start the clean up thread
        startCleanupTimer();

        // send an email to say we have started the application
        MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.ADMIN);
        mailContent.addTitle("The " + ServerUtil.getSettings().getApplicationName() + " server application started");
        try {
            mailContent.sendEmail(ServerUtil.getUsersAdministrator(), "Server started", false);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    private void cleanupSamples() {
        // reset the db content if needed
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            // find samples which are not linked to any items
            TypedQuery<DatasetAccess> query = em.createQuery("select d from DatasetAccess d where (d.id not in (select p.samples_id from productdataset_samples p)) and\n" +
                    "            (d.id not in (select s.samples_id from productservice_samples s)) and\n" +
                    "            (d.id not in (select c.coveragelayers_id from productservice_coveragelayers c)) and\n" +
                    "            (d.id not in (select a.baselayers_id from applicationsettings_datasetaccess a)) and\n" +
                    "            (d.id not in (select cs.savedlayers_id from company_savedLayers cs)) and\n" +
                    "            (d.id not in (select pc.coveragelayers_id from productdataset_coveragelayers pc)) and\n" +
                    "            (d.id not in (select us.savedlayers_id from user_savedLayers us)) and\n" +
                    "            (d.id not in (select ue.selectedlayers_id from user_selectedLayers ue))\n" +
                    "", DatasetAccess.class);
            List<DatasetAccess> datasetAccesses = query.getResultList();
            for(DatasetAccess datasetAccess : datasetAccesses) {
                if(!datasetAccess.isHostedData()) {
                    boolean hasFile = datasetAccess.getUri() != null &&
                            (datasetAccess instanceof DatasetAccessFile || datasetAccess instanceof DatasetAccessOGC);
                    if (hasFile) {
                        File datasetFile = ServerUtil.getDataFile(datasetAccess.getUri());
                        if(datasetFile.exists()) {
                            datasetFile.delete();
                        }
                    }
                    boolean hasLayers = datasetAccess instanceof DatasetAccessOGC && ((DatasetAccessOGC) datasetAccess).getLayerName() != null;
                    if(hasLayers) {
                        String layerName = ((DatasetAccessOGC) datasetAccess).getLayerName();
                        String[] layerNames = layerName.split(":");
                        if(layerNames.length == 2) {
                            try {
                                GeoserverUtils.getGeoserverPublisher().unpublishCoverage(layerNames[0], layerNames[1], layerNames[1]);
                            } catch (Exception e) {
                                logger.error("Could not unpublish layer " + layerName + " from datasetAccess " + datasetAccess.getId());
                            }
                        }
                    }
                }
            }
            em.remove(datasetAccesses);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    private void startCleanupTimer() {
        cleanupTimer = new Timer();
        // run the clean up every day
        final long timerChecks = 24 * ServerUtil.hoursInMs;
        cleanupTimer.schedule(new TimerTask() {

            public Date lastFetchedNotifications = new Date();

            @Override
            public void run() {
                // TODO - do all the required clean up
                EntityManager em = EMF.get().createEntityManager();
                try {
                    em.getTransaction().begin();
                    em.getTransaction().commit();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                } finally {
                    em.close();
                }
            }
        }, 1000, timerChecks);
    }

    private void startAdminTimer() {
        adminReportingTimer = new Timer();
        // run the checks every 5 minutes
        final long timerChecks = 5 * ServerUtil.minuteInMs;
        adminReportingTimer.schedule(new TimerTask() {

            public Date lastFetchedNotifications = new Date();

            @Override
            public void run() {
                // get all notifications which happend in the las   t timer checks time period
                EntityManager em = EMF.get().createEntityManager();
                try {
                    Date now = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now);
                    calendar.add(Calendar.MINUTE, ServerUtil.getSettings().getNotificationDelay());
                    // TODO - optimise the query as when many users are registered it might end up a problem
                    TypedQuery<AdminNotification> notificationQuery = em.createQuery("SELECT a FROM AdminNotification a WHERE a.sent = :sent", AdminNotification.class);
                    notificationQuery.setParameter("sent", false);
                    List<AdminNotification> adminNotifications = notificationQuery.getResultList();
                    logger.debug("New notifications: " + adminNotifications.size());
                    if(adminNotifications.size() > 0) {
                        // send emails
                        try {
                            em.getTransaction().begin();
                            // get list of administrator users
                            TypedQuery<String> adminQuery = em.createQuery("select u.email from users u where u.role = :role", String.class);
                            adminQuery.setParameter("role", User.USER_ROLE.administrator);
                            List<String> adminEmails = adminQuery.getResultList();
                            // now send emails
                            MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.ADMIN);
                            mailContent.addTitle("You have new notifications");
                            mailContent.addTable(ListUtil.mutate(adminNotifications, new ListUtil.Mutate<AdminNotification, List<String>>() {
                                @Override
                                public List<String> mutate(AdminNotification notification) {
                                    notification.setSent(true);
                                    return ListUtil.toList(new String[]{
                                            notification.getMessage(), ", on " + timeFormat.format(notification.getCreationDate()),
                                            "<a href='" + ServerUtil.getSettings().getAdminWebsiteUrl() + "#notifications:id=" + notification.getId() +
                                                    "'>View</a>"
                                    });
                                }
                            }));
                            mailContent.addAction("view all notifications", null, ServerUtil.getSettings().getAdminWebsiteUrl() + "#notifications:");
                            mailContent.sendEmail(StringUtils.join(adminEmails, ","), "New notifications on EO Broker", false);
                            em.getTransaction().commit();
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            if(em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error running notification email service. Reason is " + e.getMessage());
                } finally {
                    em.close();
                }
            }
        }, 1000, timerChecks);
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
                            em.getTransaction().begin();
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
                                try {
                                    mailContent.sendEmail(user.getEmail(), "New notifications on EO Broker", false);
                                } catch (Exception e) {
                                    logger.error("Problem sending email to user " + user.getUsername() + " error is " + e.getMessage(), e);
                                }
                            }
                            // update notification
                            for(SupplierNotification notification : companyNotifications) {
                                notification.setSent(true);
                            }
                            company.setLastNotificationCheck(now);
                            em.getTransaction().commit();
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            if(em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                        }
                    }
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
                            em.getTransaction().begin();
                            MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.CONSUMER);
                            mailContent.addTitle("You have new notifications");
                            mailContent.addTable(ListUtil.mutate(userNotifications, new ListUtil.Mutate<Notification, List<String>>() {
                                @Override
                                public List<String> mutate(Notification notification) {
                                    return ListUtil.toList(new String[]{
                                            notification.getMessage(), "on " + timeFormat.format(notification.getCreationDate()),
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
                            em.getTransaction().commit();
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            if(em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                        }
                    }
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
            {
                // look for OGC datasets access without layername but uri set
                TypedQuery<DatasetAccessOGC> query = em.createQuery("select d from DatasetAccessOGC d where d.layerName is NULL", DatasetAccessOGC.class);
                List<DatasetAccessOGC> datasetAccessOGCs = query.getResultList();
                if (datasetAccessOGCs.size() > 0) {
                    em.getTransaction().begin();
                    for (DatasetAccessOGC datasetAccessOGC : datasetAccessOGCs) {
                        datasetAccessOGC.setLayerName(datasetAccessOGC.getUri());
                        datasetAccessOGC.setUri(null);
                    }
                    em.getTransaction().commit();
                }
            }
            {
                // look for local datasets access without file size set
                TypedQuery<DatasetAccess> query = em.createQuery("select d from DatasetAccess d where d.hostedData = :hostedData AND d.size is NULL", DatasetAccess.class);
                query.setParameter("hostedData", false);
                List<DatasetAccess> datasetAccesses = query.getResultList();
                if (datasetAccesses.size() > 0) {
                    em.getTransaction().begin();
                    for (DatasetAccess datasetAccess : datasetAccesses) {
                        try {
                            File file = new File(ServerUtil.getSettings().getDataDirectory() + datasetAccess.getUri());
                            if (file.exists()) {
                                datasetAccess.setSize((int) file.length());
                            } else {
                                logger.error("File " + file.getName() + " does not exist on disk");
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    em.getTransaction().commit();
                }
            }
            {
                // check all users have a settings
                TypedQuery<User> query = em.createQuery("select u from users u where u.customerSettings is NULL", User.class);
                List<User> users = query.getResultList();
                if (users.size() > 0) {
                    em.getTransaction().begin();
                    for (User user : users) {
                        user.setCustomerSettings(new CustomerSettings());
                    }
                    em.getTransaction().commit();
                }
            }
            {
                // check all users have a creation date
                TypedQuery<User> query = em.createQuery("select u from users u where u.creationDate is NULL", User.class);
                List<User> users = query.getResultList();
                if (users.size() > 0) {
                    em.getTransaction().begin();
                    for (User user : users) {
                        user.setCreationDate(user.getLastLoggedIn() == null ? new Date() : user.getLastLoggedIn());
                    }
                    em.getTransaction().commit();
                }
            }
            // set the column to a geometry type if there is no record
            {
                if (em.createQuery("select count(f) from FeasibilitySearch f", Long.class).getSingleResult() == 0) {
                    // check the type of the geometry extent first
                    em.createNativeQuery("alter table feasibilitysearch drop column selectiongeometry;" +
                            "alter table feasibilitysearch add column selectiongeometry geometry;").executeUpdate();
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
        if(adminReportingTimer != null) {
            adminReportingTimer.cancel();
            adminReportingTimer = null;
        }
        if(cleanupTimer != null) {
            cleanupTimer.cancel();
            cleanupTimer = null;
        }
    }
}
