package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.HasWaves;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.ui.MaterialCollapsible;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.html.ListItem;

/**
 * Created by thomas on 21/09/2016.
 */
public class MaterialSideNav extends gwt.material.design.client.ui.MaterialSideNav {

    @Override
    public Widget wrap(Widget child) {
        if(child instanceof MaterialImage) {
            child.getElement().getStyle().setProperty("border", "1px solid #e9e9e9");
            child.getElement().getStyle().setProperty("textAlign", "center");
        }

        // Check whether the widget is not selectable by default
        boolean isNotSelectable = false;
        if(child instanceof MaterialWidget) {
            MaterialWidget widget = (MaterialWidget) child;
            if (widget.getInitialClasses() != null) {
                if (widget.getInitialClasses().length > 0) {
                    String initialClass = widget.getInitialClasses()[0];
                    if(initialClass.contains("side-profile") || initialClass.contains("collapsible")) {
                        isNotSelectable = true;
                    }
                }
            }
        }

        if(!(child instanceof ListItem)) {
            // Direct list item not collapsible
            final ListItem listItem = new ListItem();
            if(child instanceof MaterialCollapsible) {
                listItem.getElement().getStyle().setBackgroundColor("transparent");
            }
            if(child instanceof HasWaves) {
                listItem.setWaves(((HasWaves) child).getWaves());
                ((HasWaves) child).setWaves(null);
            }
            listItem.add(child);

            child = listItem;
        }

        // Collapsible and Side Porfile should not be selectable
        final Widget finalChild = child;
        if(!isNotSelectable) {
            // Active click handler
            finalChild.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
/*
                    clearActive();
                    finalChild.addStyleName("active");
*/
                }
            }, ClickEvent.getType());
        }
        child.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        return child;
    }

}
