package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.i18n.client.DefaultLocalizedNames;
import gwt.material.design.client.ui.MaterialListBox;

public class CountryEditor extends MaterialListBox {

	private static DefaultLocalizedNames names = new DefaultLocalizedNames();

	public CountryEditor() {
		// fill the list with countries names
		for(String countryCode : names.loadSortedRegionCodes()) {
			addItem(countryCode, getDisplayName(countryCode));
		}
		setSelectedIndex(0);
	}
	
	static public String getDisplayName(String countryCode) {
		return names.getRegionName(countryCode);
	}

	public String getCountry() {
		return getValue(getSelectedIndex());
	}
	
	public void selectCountry(String countryCode) {
		setSelectedValue(countryCode);
	}

}
