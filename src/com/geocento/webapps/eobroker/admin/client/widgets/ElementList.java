package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingCellTable;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/**
 * Created by thomas on 31/08/2016.
 */
public abstract class ElementList<T extends NewsItem> extends AsyncPagingCellTable<T> {

    private Column<T, T> viewColumn;
    private Column<T, String> editColumn;
    private Column<T, String> removeColumn;

    @Override
    public void initTableColumns(CellTable<T> dataGrid) {

        viewColumn = createElementCell();
        addColumn(viewColumn, "", "450px");

        editColumn = new Column<T, String>(new ButtonCell()) {
            @Override
            public String getValue(T object) {
                return "Edit";
            }
        };
        addColumn(editColumn, "", "100px");
        editColumn.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, final T element, String value) {
                editElement(element);
            }
        });

        removeColumn = new Column<T, String>(new ButtonCell()) {
            @Override
            public String getValue(T object) {
                return "Remove";
            }
        };
        addColumn(removeColumn, "", "100px");
        removeColumn.setFieldUpdater(new FieldUpdater<T, String>() {

            @Override
            public void update(int index, final T element, String value) {
                removeElement(element);
            }
        });

    }

    protected abstract Column<T,T> createElementCell();

    protected abstract void removeElement(T element);

    protected abstract void editElement(T element);
}
