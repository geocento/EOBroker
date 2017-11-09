package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.html.Option;

/**
 * Created by thomas on 08/11/2016.
 */
public class GeoinformationWidget extends Composite {

    interface GeoinformationWidgetUiBinder extends UiBinder<Widget, GeoinformationWidget> {
    }

    private static GeoinformationWidgetUiBinder ourUiBinder = GWT.create(GeoinformationWidgetUiBinder.class);

    @UiField
    MaterialListBox type;
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextBox description;
    @UiField
    MaterialButton remove;

    private final FeatureDescription featureDescription;

    public GeoinformationWidget(FeatureDescription featureDescription) {

        this.featureDescription = featureDescription;

        initWidget(ourUiBinder.createAndBindUi(this));

        for(FeatureType featureType : FeatureType.values()) {
            Option optionWidget = new Option();
            optionWidget.setText(featureType.getName());
            optionWidget.setValue(featureType.toString());
            type.add(optionWidget);
        }

        type.setSelectedValue(featureDescription.getFeatureType() == null ? FeatureType.UNSPECFIED.toString() : featureDescription.getFeatureType().toString());
        name.setText(featureDescription.getName());
        description.setText(featureDescription.getDescription());
    }

    public FeatureDescription getFeatureDescription() {
        // update values first
        featureDescription.setFeatureType(FeatureType.valueOf(type.getSelectedValue()));
        featureDescription.setName(name.getText());
        featureDescription.setDescription(description.getText());
        return featureDescription;
    }

    public HasClickHandlers getRemove() {
        return remove;
    }

}