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
        }
        return null;
    }
}