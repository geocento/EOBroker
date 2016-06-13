package com.geocento.webapps.eobroker.common.shared.entities.utils;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by thomas on 08/06/2016.
 */
public class ProductServiceHelper {

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
        product.setDescription(description);
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
        return "setweight(to_tsvector('english','product " + product.getSector().toString() + " " + product.getThematic().toString() + " " + product.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + product.getDescription() + "'), 'B')";
    }

    public static ProductServiceDTO createProductServiceDTO(ProductService productService) {
        ProductServiceDTO productServiceDTO = new ProductServiceDTO();
        productServiceDTO.setName(productService.getName());
        productServiceDTO.setDescription(productService.getDescription());
        productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
        productServiceDTO.setCompanyName(productService.getCompany().getName());
        productServiceDTO.setServiceImage(getImage());
        productServiceDTO.setProductId(productService.getProduct().getId());
        return productServiceDTO;
    }

    static private String getImage() {
        String[] images = new String[]{
                "https://www3.epa.gov/climatechange/images/basics/factorysmoke.jpg",
                "http://www.nasa.gov/sites/default/files/styles/full_width/public/thumbnails/image/15-115.jpg?itok=-S4q6bvE",
                "https://www3.epa.gov/climatechange/kids/images/scientists-clues-print.jpg",
                "http://a.fastcompany.net/multisite_files/coexist/imagecache/1280/poster/2012/11/1680998-poster-1280-how-to-profit-from-climate-change.jpg",
                "http://www.unep.org/climatechange/Portals/5/images/CC-New/main_banner.jpg"
        };
        return images[((int) Math.floor(Math.random() * images.length))];
    }
}
