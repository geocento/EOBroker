package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import java.util.List;

public abstract class AsyncPagingCellList<T extends Object> extends FlowPanel implements RequiresResize {

    static public interface Presenter {
		void rangeChanged(int start, int length, String sortBy, boolean isAscending);
	}

	static SimplePager.Resources pagerResources = GWT
			.create(SimplePager.Resources.class);

	protected CellList<T> cellList;
	protected SimplePager pager;
	protected AsyncDataProvider<T> dataProvider;

	private Presenter presenter;

    public AsyncPagingCellList() {
        this(10, null);
    }

	public AsyncPagingCellList(int pageSize, CellList.Resources resources) {

        this.pager = new SimplePager();

		cellList = resources == null ?
                new CellList<T>(getCell()) :
                new CellList<T>(getCell(), resources);
		pager.setDisplay(cellList);
		dataProvider = new AsyncDataProvider<T>() {

			@Override
			protected void onRangeChanged(HasData<T> display) {
			    final Range range = display.getVisibleRange();
                if(presenter != null) {
                    presenter.rangeChanged(range.getStart(), range.getLength(), null, true);
                }
			}
		};
		cellList.setEmptyListWidget(new HTML("No Data to Display"));
		AsyncHandler sortHandler = new AsyncHandler(cellList);

		dataProvider.addDataDisplay(cellList);
		pager.setVisible(true);
		cellList.setVisible(true);

        cellList.setWidth("100%");
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(cellList);
		add(scrollPanel);
        SimplePanel pagerPanel = new SimplePanel(pager);
        pagerPanel.setStyleName("pagerPanel");
		add(pagerPanel);
	}

    protected abstract Cell<T> getCell();

    public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public void setRowData(int start, List<? extends T> values) {
        boolean hasMore = values.size() == pager.getPageSize();
        cellList.setRowCount(start + values.size() + (hasMore ? 1 : 0), !hasMore);
		cellList.setRowData(start, values);
	}
	
    public void refreshDisplay() {
        cellList.redraw();
    }

    public void setPagerSize(int limit) {
        pager.setPageSize(limit);
    }

    @Override
	public void onResize() {
	}

}
