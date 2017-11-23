package com.geocento.webapps.eobroker.common.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Category;
import gwt.material.design.client.constants.Color;
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
            case challenges:
                return IconType.WHATSHOT;
            case newsItems:
                return IconType.NOTIFICATIONS;
        }
        return null;
    }

    public static Color getColor(Category type) {
        switch (type) {
            case products:
                return Color.BLUE_GREY;
            case productservices:
                return Color.ORANGE;
            case productdatasets:
                return Color.GREEN;
            case software:
                return Color.BROWN;
            case project:
                return Color.BLUE;
            case companies:
                return Color.BLUE;
            case challenges:
                return Color.AMBER;
            case successStories:
                return Color.AMBER;
            case imagery:
                return Color.BLUE;
        }
        return Color.GREY;
    }
}
