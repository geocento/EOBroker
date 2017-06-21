package com.geocento.webapps.eobroker.common.client.styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;

public interface MyCellTableResources extends CellTable.Resources {

	public MyCellTableResources INSTANCE =
		GWT.create(MyCellTableResources.class);

	/**
	 * The styles used in this widget.
	 */
	@Source("CellTable.css")
	CellTable.Style cellTableStyle();
}
