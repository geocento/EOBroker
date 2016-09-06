package com.geocento.webapps.eobroker.customer.client.widgets;

import gwt.material.design.client.base.HasProgress;
import gwt.material.design.client.base.mixin.ProgressMixin;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.MaterialButton;

/**
 * Created by thomas on 31/08/2016.
 */
public class ProgressButton extends MaterialButton implements HasProgress {

    private final ProgressMixin<ProgressButton> progressMixin = new ProgressMixin<>(this);

    @Override
    public void showProgress(ProgressType type) {
        progressMixin.showProgress(type);
    }

    @Override
    public void setPercent(double percent) {
        progressMixin.setPercent(percent);
    }

    @Override
    public void hideProgress() {
        progressMixin.hideProgress();
    }
}
