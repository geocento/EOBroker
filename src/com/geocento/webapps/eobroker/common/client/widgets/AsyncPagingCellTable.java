package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import java.util.List;

public abstract class AsyncPagingCellTable<T extends Object> extends FlowPanel implements RequiresResize {

    static public interface Presenter {
		void rangeChanged(int start, int length, Column<?, ?> column, boolean isAscending);
	}
	
	static SimplePager.Resources pagerResources = GWT
			.create(SimplePager.Resources.class);
	
	protected CellTable<T> dataGrid;
	protected SimplePager pager;
	protected AsyncDataProvider<T> dataProvider;
	
	private Presenter presenter;

    public AsyncPagingCellTable() {
        this(10, null);
    }

	public AsyncPagingCellTable(int pageSize, CellTable.Resources resources) {
/*
		this.pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0,true);
*/
        this.pager = new SimplePager();

		dataGrid = resources == null ?
                new CellTable<T>(pageSize) :
                new CellTable<T>(pageSize, resources);
		pager.setDisplay(dataGrid);
		dataProvider = new AsyncDataProvider<T>() {
			
			@Override
			protected void onRangeChanged(HasData<T> display) {
			    final Range range = display.getVisibleRange();
		        // Get the ColumnSortInfo from the table.
		        final ColumnSortList sortList = dataGrid.getColumnSortList();
		        boolean hasSortListInfo = sortList != null && sortList.size() > 0;
			    if(presenter != null) {
			    	presenter.rangeChanged(range.getStart(), range.getLength(), hasSortListInfo ? sortList.get(0).getColumn() : null, hasSortListInfo ? sortList.get(0).isAscending() : true);
			    }
			}
		};
		dataGrid.setEmptyTableWidget(new HTML("No Data to Display"));
		AsyncHandler sortHandler = new ColumnSortEvent.AsyncHandler(dataGrid);

		initTableColumns(dataGrid);

		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider.addDataDisplay(dataGrid);
		pager.setVisible(true);
		dataGrid.setVisible(true);

        dataGrid.setWidth("100%", true);
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(dataGrid);
		add(scrollPanel);
        SimplePanel pagerPanel = new SimplePanel(pager);
        pagerPanel.setStyleName("pagerPanel");
		add(pagerPanel);
		dataGrid.setEmptyTableWidget(new Label("No Data loaded..."));
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	abstract public void initTableColumns(CellTable<T> dataGrid);
	
	public void setRowData(int start, List<? extends T> values) {
        boolean hasMore = values.size() == pager.getPageSize();
        dataGrid.setRowCount(start + values.size() + (hasMore ? 1 : 0), !hasMore);
		dataGrid.setRowData(start, values);
	}
	
	public void addResizableColumn(Column<T, ?> column, String title) {
		dataGrid.addColumn(column, new ResizableHeader<T>(title, dataGrid, column));
	}

	public void addResizableColumn(Column<T, ?> column, String title, String width) {
		dataGrid.addColumn(column, new ResizableHeader<T>(title, dataGrid, column));
		dataGrid.setColumnWidth(column, width);
	}

	public void insertResizableColumn(int index, Column<T, ?> column, String title, String width) {
		dataGrid.insertColumn(index, column, new ResizableHeader<T>(title, dataGrid, column));
		dataGrid.setColumnWidth(column, width);
	}

	public void addColumn(Column<T, ?> column, String title, String width) {
		dataGrid.addColumn(column, title);
		dataGrid.setColumnWidth(column, width);
	}

	public void addColumn(Column<T, ?> column, String title) {
		dataGrid.addColumn(column, title);
	}

    public void clearData() {
    }

    public void refreshDisplay() {
        dataGrid.redraw();
    }

    public void setPagerSize(int limit) {
        pager.setPageSize(limit);
    }

    public void setLoading(boolean loading) {
    }

    @Override
	public void onResize() {
	}

}
