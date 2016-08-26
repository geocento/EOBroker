package com.geocento.webapps.eobroker.common.client.styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Resources;

public interface MyDataGridResources extends Resources {

	public MyDataGridResources INSTANCE =
		GWT.create(MyDataGridResources.class);

	/**
	 * The styles used in this widget.
	 */
	@Source("DataGrid.css")
	DataGrid.Style dataGridStyle();
}
