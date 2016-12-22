package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by thomas on 15/06/2016.
 */
public class WidgetUtil {

    static public interface CheckValue {
        boolean isValue(Widget widget);
    }

    static public interface Action {
        /*
         * performs an action on the widget
         */
        void action(Widget widget);
    }

    public static native boolean isActiveElement(Element element) /*-{
        return document.activeElement === element;
    }-*/;

    static public void performAction(HasWidgets container, Action action) {
        Iterator<Widget> iterator = container.iterator();
        while(iterator.hasNext()) {
            Widget widget = iterator.next();
            action.action(widget);
        }
    }

    static public void removeWidgets(HasWidgets container, CheckValue checkValue) {
        List<Widget> widgets = new ArrayList<Widget>();
        Iterator<Widget> iterator = container.iterator();
        while(iterator.hasNext()) {
            Widget widget = iterator.next();
            if(checkValue.isValue(widget)) {
                widgets.add(widget);
            }
        }
        for(Widget widget : widgets) {
            container.remove(widget);
        }
    }

}
