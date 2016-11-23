/*
 * Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * 
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.geocento.webapps.eobroker.common.server.Utils;

import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A simple class to manage loading the property file containing needed configuration data
 * from the package. Once loaded the configuration is held in memory as a singleton.  Since
 * we already require the simplejpa.properties file to support SimpleJPA, we use that
 * to store additional configuration values.
 */
public class Configuration {

    static public enum APPLICATION_SETTINGS {
        uploadPath, eiToken, geoserverRESTUri, geoserverUser, geoserverPassword, geoserverOWS
    };

    static private Properties props=new Properties();

    static private Logger logger = Logger.getLogger(Configuration.class.getName());

    private Configuration () throws Exception {
    	loadConfiguration();
    }
    
    static public void loadConfiguration() throws Exception {
        String home = System.getProperty("user.home");
        props.load(new FileReader(home + "/configurations/eobroker.properties"));
/*
        props.load(Configuration.class.getClassLoader().getResourceAsStream("overlay.properties"));
*/
    }

    static public String getProperty (String propertyName) {
        return props.getProperty(propertyName);
    }

    static public int getIntProperty (String propertyName) {
        return Integer.parseInt(props.getProperty(propertyName));
    }

    public static double getDoubleProperty(String propertyName) {
        return Double.parseDouble(props.getProperty(propertyName));
    }

    public static boolean getBooleanProperty(String propertyName) {
        String property = props.getProperty(propertyName);
        if(property == null) {
            return false;
        }
        try {
            return Boolean.parseBoolean(property);
        } catch(Exception e) {
            return false;
        }
    }

    static public String getProperty (APPLICATION_SETTINGS propertyName) {
        return getProperty(propertyName.toString());
    }

    static public int getIntProperty (APPLICATION_SETTINGS propertyName) {
        return getIntProperty(propertyName.toString());
    }

    public static double getDoubleProperty(APPLICATION_SETTINGS propertyName) {
        return getDoubleProperty(propertyName.toString());
    }

    public static boolean getBooleanProperty(APPLICATION_SETTINGS propertyName) {
        return getBooleanProperty(propertyName.toString());
    }

}
