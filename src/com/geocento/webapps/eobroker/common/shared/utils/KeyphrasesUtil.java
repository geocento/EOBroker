package com.geocento.webapps.eobroker.common.shared.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Product;

import javax.persistence.EntityManager;

/**
 * Created by thomas on 07/06/2016.
 */
public class KeyphrasesUtil {

    static public enum type {products, productservices, imageryproviders}

    static public void updateProduct(EntityManager em, Product product) {

    }

    public static javax.persistence.Query addProductQuery(EntityManager em, Product product) {
        return em.createNativeQuery("UPDATE product SET tsv = " + getProductTSV(product) +
                ", tsvname = " + getProductNameTSV(product) + " where id = " + product.getId() +
                ";");
/*
        return em.createNativeQuery("INSERT INTO searchphrases " +
                "(uri, 'type', phrase, tsv) VALUES (" + product.getId() + ", '" +
                type.products.toString() + "', '" +
                product.getName() + "', " +
                "setweight(to_tsvector('english','product'), 'A') || setweight(to_tsvector('english','" + product.getName() + "'), 'B')" +
                ");");
*/
    }

    private static String getProductNameTSV(Product product) {
        return "setweight(to_tsvector('english','product'), 'A') || setweight(to_tsvector('english','" + product.getName() + "'), 'B')";
/*
        return "to_tsvector('english','" + product.getSector().name() + "')";
*/
    }

    public static javax.persistence.Query updateProductQuery(EntityManager em, Product product) {
        return em.createNativeQuery("UPDATE keyphrases SET tsv = " +
                getProductTSV(product) +
                " where id = " + product.getId() + " and category = '" + type.products.toString() + "'" +
                ");");
    }

    private static String getProductTSV(Product product) {
        return "setweight(to_tsvector('english','product " + product.getSector().name() + " " + product.getThematic().name() + " " + product.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + product.getDescription() + "'), 'B')";
    }

}
