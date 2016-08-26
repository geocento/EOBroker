package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public abstract class ClickableImageCell<T extends Object> extends AbstractCell<T> {

    public static interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<div name=\"{0}\" title=\"{1}\" style=\"{2}\">{3}</div>")
        SafeHtml cell(String name, String title, SafeStyles styles, SafeHtml value);
    }

    protected SafeStyles imgStyle = SafeStylesUtils
            .fromTrustedString("display:inline-block; cursor:pointer; padding-left: 0px;");

    public ClickableImageCell() {
        super("click");
    }

    /**
     * Create a singleton instance of the templates used to render the cell.
     */
    protected static Templates templates = GWT.create(Templates.class);

    /**
     * Called when an event occurs in a rendered instance of this Cell. The
     * parent element refers to the element that contains the rendered cell, NOT
     * to the outermost element that the Cell rendered.
     */
    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
                               Element parent, T value, NativeEvent event,
                               com.google.gwt.cell.client.ValueUpdater<T> valueUpdater) {

        // Let AbstractCell handle the keydown event.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        // Handle the click event.
        if ("click".equals(event.getType())) {

            // Ignore clicks that occur outside of the outermost element.
            EventTarget eventTarget = event.getEventTarget();

            if (parent.isOrHasChild(Element.as(eventTarget))) {
                // if (parent.getFirstChildElement().isOrHasChild(
                // Element.as(eventTarget))) {

                // use this to get the selected element!!
                Element el = Element.as(eventTarget);

                // check if we really click on the image
                if (el.getNodeName().equalsIgnoreCase("IMG")) {
                    String action = el.getParentElement().getAttribute("name");
                    performAction(action, value, event);
                    doAction(value, valueUpdater);
                }

            }
        }

    };

    abstract protected void performAction(String action, T value, NativeEvent event);

    /**
     * Intern action
     *
     * @param value
     *            selected value
     * @param valueUpdater
     *            value updater or the custom value update to be called
     */
    private void doAction(T value, ValueUpdater<T> valueUpdater) {
        // Trigger a value updater. In this case, the value doesn't actually
        // change, but we use a ValueUpdater to let the app know that a value
        // was clicked.
        if (valueUpdater != null)
            valueUpdater.update(value);
    }

    /**
     * Make icons available as SafeHtml
     *
     * @param resource
     * @return
     */
    protected static SafeHtml makeImage(ImageResource resource) {
        AbstractImagePrototype proto = AbstractImagePrototype.create(resource);

        return proto.getSafeHtml();
    }

}
