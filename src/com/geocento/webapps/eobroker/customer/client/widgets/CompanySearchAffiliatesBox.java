package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.Category;
import gwt.material.design.client.ui.MaterialSwitch;

/**
 * Created by thomas on 29/09/2017.
 */
public class CompanySearchAffiliatesBox extends CategorySearchBox {

    public CompanySearchAffiliatesBox() {
        setCategory(Category.companies);
        MaterialSwitch materialSwitch = new MaterialSwitch();
        materialSwitch.setHelperText("Limit to affiliated companies only");
        add(materialSwitch);
    }
}
