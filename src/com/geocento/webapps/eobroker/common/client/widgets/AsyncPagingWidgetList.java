package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialRow;

import java.util.List;

public abstract class AsyncPagingWidgetList<T extends Object> extends Composite implements RequiresResize {

    static public interface Presenter {
		void loadMore();
	}

	static SimplePager.Resources pagerResources = GWT
			.create(SimplePager.Resources.class);

	protected MaterialRow cellList;
    private MaterialColumn hasMoreColumn;
    private HasMoreWidget hasMoreWidget;

    protected int pageSize;
    protected int small;
    protected int medium;
    protected int large;

	private Presenter presenter;

    public AsyncPagingWidgetList() {
        this(10, 12, 6, 4);
    }

	public AsyncPagingWidgetList(int pageSize, int small, int medium, int large) {

        this.pageSize = pageSize;
        this.small = small;
        this.medium = medium;
        this.large = large;

        HTMLPanel htmlPanel = new HTMLPanel("");
		cellList = new MaterialRow();
        hasMoreColumn = new MaterialColumn(small, medium, large);
        hasMoreWidget = new HasMoreWidget();
        hasMoreWidget.getLoadMore().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cellList.remove(hasMoreColumn);
                presenter.loadMore();
            }
        });
        hasMoreColumn.add(hasMoreWidget);

        htmlPanel.add(cellList);

        initWidget(htmlPanel);

	}

    protected abstract Widget getItemWidget(T value);

    public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

    public void clearData() {
        cellList.clear();
    }

    public void addData(List<? extends T> values, boolean hasMore) {
        cellList.remove(hasMoreColumn);
        for(T value : values) {
            MaterialColumn materialColumn = new MaterialColumn(small, medium, large);
            materialColumn.add(getItemWidget(value));
            cellList.add(materialColumn);
        }
        if(hasMore) {
            cellList.add(hasMoreColumn);
        }
	}

    public void setLoading(boolean loading) {
        hasMoreWidget.setLoading(loading);
        cellList.add(hasMoreColumn);
    }

    @Override
	public void onResize() {
	}

}
