package com.geocento.webapps.eobroker.common.server.Utils;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class WriteToDatabase {

    private DataStore dataStore;

    public WriteToDatabase(Properties params) throws IOException {

        dataStore = DataStoreFinder.getDataStore(params);

    }

    public boolean isReady() {
        return dataStore != null;
    }

    public boolean writeFeatures(
            FeatureCollection<SimpleFeatureType, SimpleFeature> features) {

        if (dataStore == null) {
            throw new IllegalStateException(
                    "Datastore can not be null when writing");
        }
        SimpleFeatureType schema = features.getSchema();

        try {

	    /*
	     * Write the features
	     */
            Transaction transaction = new DefaultTransaction("create");

            String[] typeNames = dataStore.getTypeNames();
            //first check if we need to create table
            boolean exists = false;
            String schemaName = schema.getName().getLocalPart();
            for (String name : typeNames) {

                if (schemaName.equalsIgnoreCase(name)) {
                    exists = true;
                }
            }
            if (!exists) {
                dataStore.createSchema(schema);
            }
            //we should probably check the schema matches the existing table.
            //but we don't
            SimpleFeatureSource featureSource = dataStore
                    .getFeatureSource(schema.getName().getLocalPart());
            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

                featureStore.setTransaction(transaction);
                try {
                    List<FeatureId> ids = featureStore.addFeatures(features);
                    transaction.commit();
                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                } finally {
                    transaction.close();
                }
                dataStore.dispose();
                return true;
            } else {
                dataStore.dispose();
                System.err.println("Database not writable");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
