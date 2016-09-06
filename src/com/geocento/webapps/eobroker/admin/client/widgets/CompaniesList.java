package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingCellTable;
import com.geocento.webapps.eobroker.common.client.widgets.ImageCell;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Created by thomas on 21/06/2016.
 */
public class CompaniesList extends AsyncPagingCellTable<CompanyDTO> {

    private Column<CompanyDTO, String> editColumn;
    private TextColumn<CompanyDTO> nameColumn;
    private Column<CompanyDTO, String> thumbnailColumn;

    @Override
    public void initTableColumns(CellTable<CompanyDTO> dataGrid) {

        editColumn = new Column<CompanyDTO, String>(new ButtonCell()) {
            @Override
            public String getValue(CompanyDTO object) {
                return "Edit";
            }
        };
        addColumn(editColumn, "Action", "100px");
        editColumn.setFieldUpdater(new FieldUpdater<CompanyDTO, String>() {

            @Override
            public void update(int index, final CompanyDTO companyDTO, String value) {
                Admin.clientFactory.getPlaceController().goTo(new CompanyPlace(companyDTO));
            }
        });

        thumbnailColumn = new Column<CompanyDTO, String>(new ImageCell(30, 30)) {

            @Override
            public String getValue(CompanyDTO object) {
                return object.getIconURL();
            }
        };
        addColumn(thumbnailColumn, "Image", "100px");

        nameColumn = new TextColumn<CompanyDTO>() {
            @Override
            public String getValue(CompanyDTO object) {
                return object.getName();
            }
        };
        addResizableColumn(nameColumn, "User Name", "100px");
        nameColumn.setSortable(true);

    }
}
