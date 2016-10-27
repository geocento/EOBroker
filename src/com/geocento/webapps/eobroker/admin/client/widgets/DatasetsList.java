package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.shared.dtos.DatasetProviderDTO;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingCellTable;
import com.geocento.webapps.eobroker.common.client.widgets.ImageCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Created by thomas on 21/06/2016.
 */
public class DatasetsList extends AsyncPagingCellTable<DatasetProviderDTO> {

    private Column<DatasetProviderDTO, String> editColumn;
    private TextColumn<DatasetProviderDTO> nameColumn;
    private Column<DatasetProviderDTO, String> thumbnailColumn;

    @Override
    public void initTableColumns(CellTable<DatasetProviderDTO> dataGrid) {

        editColumn = new Column<DatasetProviderDTO, String>(new ButtonCell()) {
            @Override
            public String getValue(DatasetProviderDTO object) {
                return "Edit";
            }
        };
        addColumn(editColumn, "Action", "100px");
        editColumn.setFieldUpdater(new FieldUpdater<DatasetProviderDTO, String>() {

            @Override
            public void update(int index, final DatasetProviderDTO datasetProviderDTO, String value) {
            }
        });

        thumbnailColumn = new Column<DatasetProviderDTO, String>(new ImageCell(30, 30)) {

            @Override
            public String getValue(DatasetProviderDTO object) {
                return object.getIconURL();
            }
        };
        addColumn(thumbnailColumn, "Image", "100px");

        nameColumn = new TextColumn<DatasetProviderDTO>() {
            @Override
            public String getValue(DatasetProviderDTO object) {
                return object.getName();
            }
        };
        addResizableColumn(nameColumn, "Name", "100px");
        nameColumn.setSortable(true);

    }
}
