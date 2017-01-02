package com.geocento.webapps.eobroker.common.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Category;
import gwt.material.design.client.constants.IconType;

/**
 * Created by thomas on 09/11/2016.
 */
public class CategoryUtils {

    public static IconType getIconType(Category category) {
        switch (category) {
            case products:
                return IconType.ADD_LOCATION;
            case productservices:
                return IconType.PERM_IDENTITY;
            case productdatasets:
                return IconType.COLLECTIONS;
            case software:
                return IconType.CODE;
            case project:
                return IconType.GROUP_WORK;
            case datasets:
                return IconType.COLLECTIONS;
            case imagery:
                return IconType.PICTURE_IN_PICTURE;
            case companies:
                return IconType.BUSINESS;
            case newsItems:
                return IconType.NOTIFICATIONS;
        }
        return null;
    }

    public static String getColor(Category type) {
        switch (type) {
            case products:
                return "blue-grey";
            case productservices:
                return "orange";
            case productdatasets:
                return "green";
            case software:
                return "brown";
            case project:
                return "blue";
            case companies:
                return "blue";
            case imagery:
                return "blue";
        }
        return "grey";
    }
}
