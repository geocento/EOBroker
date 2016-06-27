package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Resources;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract PaggingDataGrid class to set initial GWT DataGrid and Simple Pager with ListDataProvider
 * 
 * @author Ravi Soni
 *
 * @param <T>
 */
public abstract class PagingDataGrid<T> extends Composite implements ProvidesResize, RequiresResize {

    protected DataGrid<T> dataGrid;
    private SimplePanel pagerPanel;
	protected SimplePager pager;
	private String height;
	protected ListDataProvider<T> dataProvider;
	protected DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
	protected ListHandler<T> sortHandler;

	static SimplePager.Resources pagerResources = GWT
			.create(SimplePager.Resources.class);
	
	public PagingDataGrid() {
		this(0, null);
	}
	
	public PagingDataGrid(int pageSize, Resources resources) {
		this(pageSize, resources, new SimplePager(TextLocation.CENTER, pagerResources, false, 0,true));
	}
	
	public PagingDataGrid(int pageSize, Resources resources, SimplePager pager) {
		this.pager = pager;
		
		initWidget(dock);
		if(resources != null) {
			dataGrid = new DataGrid<T>(pageSize, resources);
		} else {
			dataGrid = new DataGrid<T>();
		}

		pager.setDisplay(dataGrid);
		dataProvider = new ListDataProvider<T>();
		dataProvider.setList(new ArrayList<T>());
		dataGrid.setEmptyTableWidget(new HTML("No Data to Display"));
		sortHandler = new ListHandler<T>(dataProvider.getList());

		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider.addDataDisplay(dataGrid);
		pager.setVisible(true);
		dataGrid.setVisible(true);

		pagerPanel = new SimplePanel(pager);
		dock.addSouth(pagerPanel, 45);
		dock.add(dataGrid);
	}

    public void setPagerHeight(int height) {
        dock.setWidgetSize(pagerPanel, height);
        dock.forceLayout();
    }

    public void setPagerPanelVisible(boolean visible) {
        dock.setWidgetHidden(pagerPanel, !visible);
    }

    /**
	 * 
	 * Abstract Method to implements for adding Column into Grid
	 * 
	 * @param dataGrid
	 * @param sortHandler
	 */
	public abstract void initTableColumns(DataGrid<T> dataGrid,	ListHandler<T> sortHandler);

	public void addColumn(Column<T, ?> column, String title, String width) {
		dataGrid.addColumn(column, title);
		dataGrid.setColumnWidth(column, width);
	}

	public void addColumn(Column<T, ?> column, String title) {
		dataGrid.addColumn(column, title);
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
		dataGrid.setHeight(height);
	}

	public List<T> getDataList() {
		return dataProvider.getList();
	}

	public void setDataList(List<T> dataList) {
		List<T> list = dataProvider.getList();
		list.clear();
		list.addAll(dataList);
		dataProvider.refresh();
		pager.firstPage();
	}

	public ListDataProvider<T> getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(ListDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
	}
	
	protected void refreshGrid() {
		dataGrid.redraw();
	}

	@Override
	public void onResize() {
		dataGrid.onResize();
	}
	
}


