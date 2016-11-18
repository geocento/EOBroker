package com.geocento.webapps.eobroker.admin.shared.dtos;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by thomas on 08/06/2016.
 */
public class ProductHelper {

    public static Product addProduct(String name, String description, Sector sector, Thematic thematic) {
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            Product product = addProduct(em, name, description, sector, thematic);
            em.getTransaction().commit();
            return product;
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public static Product addProduct(EntityManager em, String name, String description, Sector sector, Thematic thematic) {
        Product product = new Product();
        product.setName(name);
        product.setShortDescription(description);
        product.setSector(sector);
        product.setThematic(thematic);
        em.persist(product);
        // update the keyphrases
        Query query = em.createNativeQuery("UPDATE product SET tsv = " + getProductTSV(product) +
                ", tsvname = " + getProductNameTSV(product) + " where id = " + product.getId() +
                ";");
        query.executeUpdate();
        return product;
    }

    private static String getProductNameTSV(Product product) {
        return "setweight(to_tsvector('english','product'), 'A') || setweight(to_tsvector('english','" + product.getName() + "'), 'B')";
    }

    private static String getProductTSV(Product product) {
        return "setweight(to_tsvector('english','product " + product.getSector().getName() + " " + product.getThematic().getName() + " " + product.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + product.getShortDescription() + "'), 'B')";
    }

    public static ProductDTO createProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getShortDescription());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setSector(product.getSector());
        productDTO.setThematic(product.getThematic());
        return productDTO;
    }

}
