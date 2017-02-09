package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.server.Utils.UserUtils;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.common.shared.entities.utils.LevenshteinDistance;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ContextListener implements ServletContextListener {

    Logger logger = Logger.getLogger(ContextListener.class);

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

        // send an email to say we have started the application
        MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.ADMIN);
        mailContent.addTitle("The " + Configuration.getProperty(Configuration.APPLICATION_SETTINGS.applicationName) + " server application started");
        try {
            mailContent.sendEmail(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.adminEmail), "Server started", false);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private String findNearest(List<String> values, String name) {
        // look for the nearest one
        String bestMatch = null;
        // nearest is 0 and fartherst is 8
        int bestMatchScore = 1000;
        for(String value : values) {
            // look for the highest number of common letters
            Integer score = LevenshteinDistance.getDefaultInstance().apply(name, value);
            if(score < bestMatchScore) {
                bestMatch = value;
                bestMatchScore = score;
            }
        }
        return bestMatch;
    }

    private void initialiseDatabase() {
        // reset the db content if needed
        EntityManager em = EMF.get().createEntityManager();
        try {
            // add some data to database
            TypedQuery<Product> query = em.createQuery("select p from Product p", Product.class);
            List<Product> products = query.getResultList();
            if(reset) {
                em.getTransaction().begin();
                for (Product product : query.getResultList()) {
                    em.remove(product);
                }
                em.getTransaction().commit();
                products.clear();
            }
            if(products.size() == 0) {
                em.getTransaction().begin();
                CSVParser csvParser = new CSVParser(new FileReader(this.getClass().getResource("/products.csv").getFile()), CSVFormat.DEFAULT);
                for (CSVRecord record : csvParser.getRecords()) {
                    String sectorString = findNearest(ListUtil.mutate(ListUtil.toList(Sector.values()), new ListUtil.Mutate<Sector, String>() {
                        @Override
                        public String mutate(Sector sector) {
                            return sector.name();
                        }
                    }), record.get(2).toLowerCase());
                    String thematicString = findNearest(ListUtil.mutate(ListUtil.toList(Thematic.values()), new ListUtil.Mutate<Thematic, String>() {
                        @Override
                        public String mutate(Thematic thematic) {
                            return thematic.name();
                        }
                    }), record.get(3).toLowerCase());
                    com.geocento.webapps.eobroker.admin.shared.dtos.ProductHelper.addProduct(em, record.get(0), record.get(4), Sector.valueOf(sectorString), Thematic.valueOf(thematicString));
                }
                em.getTransaction().commit();
            }
            // add companies and some users
            em.getTransaction().begin();
            TypedQuery<Company> companyQuery = em.createQuery("select c from Company c", Company.class);
            List<Company> companies = companyQuery.getResultList();
            if(companies.size() == 0) {
                Company geocentoCompany = new Company();
                geocentoCompany.setName("Geocento");
                geocentoCompany.setDescription("Geocento company description");
                geocentoCompany.setIconURL("http://geocento.com/wp-content/uploads/2016/03/logo-geocento-global-earth-imaging.jpg");
                em.persist(geocentoCompany);
                // add a few users
                User geocentoUser = UserUtils.createUser("geocentoUser", "password", User.USER_ROLE.administrator, null, geocentoCompany);
                em.persist(geocentoUser);
                Company ksatCompany = new Company();
                ksatCompany.setName("KSAT");
                ksatCompany.setDescription("KSAT company description");
                ksatCompany.setIconURL("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQpRLyRdwnk5c9AolA9OtMb3ALjqxSG3CHyy2xx1TzzRvlclx9_");
                em.persist(ksatCompany);
                // add a few users
                User ksatUser = UserUtils.createUser("ksatUser", "password", User.USER_ROLE.supplier, null, ksatCompany);
                em.persist(ksatUser);
                Company eomapCompany = new Company();
                eomapCompany.setName("EOMAP");
                eomapCompany.setDescription("EOMAP company description");
                eomapCompany.setIconURL("images/eomapLogo.png");
                em.persist(eomapCompany);
                // add a few users
                User eomapUser = UserUtils.createUser("eomapUser", "password", User.USER_ROLE.supplier, null, eomapCompany);
                em.persist(eomapUser);
                companies.addAll(Arrays.asList(geocentoCompany, ksatCompany, eomapCompany));
            }
            TypedQuery<ImageService> imageservicesQuery = em.createQuery("select i from ImageService i", ImageService.class);
            List<ImageService> suppliers = imageservicesQuery.getResultList();
            if(suppliers == null || suppliers.size() == 0) {
                ImageService geocentoImageService = new ImageService();
                geocentoImageService.setName("EarthImages");
                geocentoImageService.setDescription("Geocento's image search and ordering service. EarthImages provides access to over 30 of the most popular satellite missions.");
                geocentoImageService.setCompany(ListUtil.findValue(companies, new ListUtil.CheckValue<Company>() {
                    @Override
                    public boolean isValue(Company value) {
                        return value.getName().contentEquals("Geocento");
                    }
                }));
                em.persist(geocentoImageService);
                ImageService ksatImageService = new ImageService();
                ksatImageService.setName("KSAT MMO");
                ksatImageService.setDescription("KSAT's image search and ordering service.");
                ksatImageService.setCompany(ListUtil.findValue(companies, new ListUtil.CheckValue<Company>() {
                    @Override
                    public boolean isValue(Company value) {
                        return value.getName().contentEquals("KSAT");
                    }
                }));
                em.persist(ksatImageService);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
        } finally {
            em.close();
        }
        // create resource handler for serving static files
    }

    private void applyFixes() {
        // reset the db content if needed
        EntityManager em = EMF.get().createEntityManager();
        try {
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
                    for (DatasetAccess datasetAccess : query.getResultList()) {
                        datasetAccess.setHostedData(
                                !(datasetAccess.getUri() != null && datasetAccess.getUri().contains("eobroker.com/")) &&
                                        !(datasetAccess instanceof DatasetAccessOGC && ((DatasetAccessOGC) datasetAccess).getServerUrl() != null && ((DatasetAccessOGC) datasetAccess).getServerUrl().contains("eobroker.com/")));
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

    }
}
