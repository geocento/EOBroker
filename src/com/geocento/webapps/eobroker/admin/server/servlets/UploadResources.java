package com.geocento.webapps.eobroker.admin.server.servlets;

import com.geocento.webapps.eobroker.admin.server.util.UserUtils;
import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.server.Utils.DBHelper;
import com.geocento.webapps.eobroker.common.server.Utils.ImageUtils;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.shared.entities.Challenge;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.utils.LevenshteinDistance;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.http.client.RequestException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomas on 22/11/2017.
 */
@Path("/upload")
public class UploadResources {

    static Logger logger = Logger.getLogger(UploadResources.class);

    static KeyGenerator keyGenerator = new KeyGenerator(16);

    // assumes service is not a singleton
    @Context
    HttpServletRequest request;

    @POST
    @Path("/challenges/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    public void importChallenges(@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
        String userName = UserUtils.verifyUserAdmin(request);
        logger.debug("Importing challenges from CSV file " + fileDetail.getFileName());
        File csvFile = ServerUtil.getTmpDataFile(userName + "/" + fileDetail.getFileName());
        FileUtils.copyInputStreamToFile(inputStream, csvFile);
        CSVParser csvParser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.EXCEL.withHeader());
        Map<String, Integer> headerMap = csvParser.getHeaderMap();
        List<String> challengeItems = ListUtil.toList(new String[]{"challenge", "description", "picture"});
        EntityManager em = EMF.get().createEntityManager();
        try {
            List<Product> products = em.createQuery("select p from Product p", Product.class).getResultList();
            // match products and labels in csv file
            HashMap<String, Product> productHashMap = new HashMap<String, Product>();
/*
            HashMap<String, HashMap<Integer, Product>> productMatchingHashMap = new HashMap<String, HashMap<Integer, Product>>();
            for (String productName : headerMap.keySet()) {
                if(!challengeItems.contains(productName)) {
                    HashMap<Integer, Product> productMatching = new HashMap<Integer, Product>();
                    productMatchingHashMap.put(productName, productMatching);
                    for(Product product : products) {
                        productMatching.put(LevenshteinDistance.getDefaultInstance().apply(productName, product.getName()), product);
                    }
                }
            }
*/
            for (String productName : headerMap.keySet()) {
                if(!challengeItems.contains(productName.toLowerCase())) {
                    HashMap<Integer, Product> productMatching = new HashMap<Integer, Product>();
                    Product selectedProduct = null;
                    int distance = 1000;
                    for(Product product : products) {
                        int currentDistance = LevenshteinDistance.getDefaultInstance().
                                apply(productName.toLowerCase(), product.getName().toLowerCase());
                        if(currentDistance < distance) {
                            selectedProduct = product;
                            distance = currentDistance;
                        }
                    }
                    productHashMap.put(productName, selectedProduct);
                }
            }
            em.getTransaction().begin();
            for (CSVRecord csvRecord : csvParser.getRecords()) {
                String name = csvRecord.get("Challenge");
                logger.debug("Processing challenge " + name);
                // check if challenge doesn't exist yet
                TypedQuery<Challenge> query = em.createQuery("select c from Challenge c where c.name = :name", Challenge.class);
                query.setParameter("name", name);
                List<Challenge> challenges = query.getResultList();
                // create or update the challenge
                Challenge challenge = challenges.size() > 0 ? challenges.get(0) : new Challenge();
                challenge.setName(name);
                challenge.setShortDescription(csvRecord.get("Description"));
                if(challenge.getShortDescription() != null && challenge.getShortDescription().length() >= 1000) {
                    challenge.setShortDescription(challenge.getShortDescription().substring(0, 999));
                }
                String imageUrl = csvRecord.get("Picture");
                if(!StringUtils.isEmpty(imageUrl)) {
                    File imageFile = ServerUtil.getTmpDataFile(challenge.getName() + ".jpg");
                    if(!imageFile.exists()) {
                        try {
                            FileUtils.copyURLToFile(new URL(imageUrl), imageFile);
                        } catch (Exception e) {
                            logger.error("Could not save image " + imageUrl + " for challenge " + challenge.getName() + ", reason is " + e.getMessage());
                        }
                        challenge.setImageUrl(ImageUtils.processAndStoreImage(imageFile, keyGenerator.CreateKey() + ".jpg", 300, 200));
                    }
                }
                logger.debug("Assiging products");
                // find which products are matching the challenge
                List<Product> challengeProducts = new ArrayList<Product>();
                for(String productName : productHashMap.keySet()) {
                    if(csvRecord.get(productName) != null && csvRecord.get(productName).contentEquals("1")) {
                        Product product = productHashMap.get(productName);
                        if(product != null && !challengeProducts.contains(product)) {
                            challengeProducts.add(product);
                        } else {
                            if(product == null) {
                                logger.error("No product for " + productName);
                            } else {
                                logger.error("Product " + product.getName() + " for " + productName + " repeated!");
                            }
                        }
                    }
                }
                challenge.setProducts(challengeProducts);
                em.persist(challenge);
                // update the keyphrases
                em.createNativeQuery("UPDATE challenge SET tsv = " + DBHelper.getChallengeTSV(challenge) +
                        ", tsvname = " + DBHelper.getChallengeNameTSV(challenge) + " where id = " + challenge.getId() +
                        ";").executeUpdate();
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Server error");
        } finally {
            em.close();
        }
    }

}
