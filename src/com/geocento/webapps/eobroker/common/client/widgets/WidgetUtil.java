package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
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

    /**
     *
     * Scans parent tree until it finds a widget matching the check value condition
     *
     * @param widget
     * @param checkValue
     * @return
     */
    public static Widget findParent(Widget widget, CheckValue checkValue) {
        Widget parent = widget.getParent();
        if(parent == null) {
            return null;
        }
        if(checkValue.isValue(parent)) {
            return parent;
        }
        return findParent(parent, checkValue);
    }

    /**
     *
     * Scans children tree until it finds a widget matching the check value condition
     *
     * @param parentWidget
     * @param checkValue
     * @return
     */
    public static Widget findChild(HasWidgets parentWidget, CheckValue checkValue) {
        Iterator<Widget> iterator = parentWidget.iterator();
        while(iterator.hasNext()) {
            Widget widget = iterator.next();
            if(checkValue.isValue(widget)) {
                return widget;
            }
            if(widget instanceof HasWidgets) {
                Widget childWidget = findChild((HasWidgets) widget, checkValue);
                if(childWidget != null) {
                    return childWidget;
                }
            }
        }
        return null;
    }

    static class Position {
        int x;
        int y;

        public Position() {
            reset();
        }

        public void reset() {
            x = -1;
            y = -1;
        }
    }

    static public interface Dragging {

        void onDrag(int clientX, int clientY);

    }

    public static void enableDragging(final Widget panel, final Dragging dragging) {
        final Position position = new Position();
        panel.sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEDOWN | Event.ONMOUSEMOVE | Event.ONMOUSEUP);
        panel.addDomHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent mouseEvent) {
                position.reset();
                Event.releaseCapture(panel.getElement());
                mouseEvent.preventDefault();
            }
        }, MouseUpEvent.getType());
        panel.addDomHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent mouseEvent) {
                position.x = mouseEvent.getClientX();
                position.y = mouseEvent.getClientY();
                Event.setCapture(panel.getElement());
                mouseEvent.preventDefault();
            }
        }, MouseDownEvent.getType());
        panel.addDomHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove(MouseMoveEvent mouseEvent) {
                if(position.x != -1) {
                    dragging.onDrag(mouseEvent.getClientX() - position.x, mouseEvent.getClientY() - position.y);
                    position.x = mouseEvent.getClientX();
                    position.y = mouseEvent.getClientY();
                    mouseEvent.preventDefault();
                }
            }
        }, MouseMoveEvent.getType());
    }

}
