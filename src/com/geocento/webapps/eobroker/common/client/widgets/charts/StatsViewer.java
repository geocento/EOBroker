package com.geocento.webapps.eobroker.common.client.widgets.charts;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialListValueBox;

import java.util.HashMap;

public class StatsViewer extends Composite {

    interface StatsViewerUiBinder extends UiBinder<Widget, StatsViewer> {
    }

    private static StatsViewerUiBinder ourUiBinder = GWT.create(StatsViewerUiBinder.class);

    @UiField
    MaterialLabel label;
    @UiField
    MaterialListBox viewCategory;
    @UiField
    MaterialListBox viewStatsDateOptions;
    @UiField
    MaterialImageLoading viewStats;

    public StatsViewer() {
        initWidget(ourUiBinder.createAndBindUi(this));

        populateDateOptions(viewStatsDateOptions);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setCategories(String[] names) {
        setCategories(names, names);
    }

    public void setCategories(String[] names, String[] values) {
        HashMap<String, String> categories = new HashMap<String, String>();
        for(int index = 0; index < names.length; index++) {
            categories.put(names[index], values[index]);
        }
        setCategories(categories);
    }

    public void setCategories(HashMap<String, String> categories) {
        for (String statsName : categories.keySet()) {
            viewCategory.addItem(statsName, categories.get(statsName));
        }
    }

    public String getSelectedCategory() {
        return viewCategory.getSelectedValue();
    }

    public HasValueChangeHandlers<String> getCategorySelection() {
        return viewCategory;
    }

    public HasValueChangeHandlers<String> getDurationSelection() {
        return viewStatsDateOptions;
    }

    public String getCategorySelectionValue() {
        return viewCategory.getValue();
    }

    public String getDurationSelectionValue() {
        return viewStatsDateOptions.getValue();
    }

    public void setDuration(String duration) {
        viewStatsDateOptions.setValueSelected(duration, true);
    }

    private void populateDateOptions(MaterialListValueBox<String> dateOptions) {
        dateOptions.addItem("-10mins", "10 minutes");
        dateOptions.addItem("-1hours", "1 hour");
        dateOptions.addItem("-6hours", "6 hours");
        dateOptions.addItem("-12hours", "12 hours");
        dateOptions.addItem("-24hours", "24 hours");
        dateOptions.addItem("-3days", "3 days");
        dateOptions.addItem("-7days", "7 days");
        dateOptions.addItem("-1months", "1 month");
        dateOptions.addItem("-3months", "3 months");
        dateOptions.addItem("-6months", "6 months");
        dateOptions.addItem("-1years", "1 year");
    }

    public void setGraphImage(String url) {
        viewStats.setImageUrl(url);
    }

    public int getGraphWidth() {
        return viewStats.getOffsetWidth();
    }

    public int getGraphHeight() {
        return viewStats.getOffsetHeight();
    }

}