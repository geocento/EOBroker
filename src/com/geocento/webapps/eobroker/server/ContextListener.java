package com.geocento.webapps.eobroker.server;

import com.geocento.webapps.eobroker.shared.entities.Product;
import com.geocento.webapps.eobroker.shared.entities.Sector;
import com.geocento.webapps.eobroker.shared.entities.Thematic;
import com.geocento.webapps.eobroker.shared.entities.utils.LevenshteinDistance;
import com.geocento.webapps.eobroker.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.shared.utils.ListUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ContextListener implements ServletContextListener {

    Logger logger = Logger.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("Application starting");
        // check all is ok with database
        EntityManager em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Product> query = em.createQuery("select p from Product p", Product.class);
        for(Product product : query.getResultList()) {
            em.remove(product);
        }
        em.getTransaction().commit();
        em.getTransaction().begin();
        try {
            CSVParser csvParser = new CSVParser(new FileReader(this.getClass().getResource("/products.csv").getFile()), CSVFormat.DEFAULT);
            for(CSVRecord record : csvParser.getRecords()) {
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
                ProductHelper.addProduct(em, record.get(0), record.get(4), Sector.valueOf(sectorString), Thematic.valueOf(thematicString));
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

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
