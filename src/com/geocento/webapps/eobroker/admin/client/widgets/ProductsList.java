package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.places.ProductPlace;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingCellTable;
import com.geocento.webapps.eobroker.common.client.widgets.ImageCell;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Created by thomas on 21/06/2016.
 */
public class ProductsList extends AsyncPagingCellTable<ProductDTO> {

    private Column<ProductDTO, String> editColumn;
    private TextColumn<ProductDTO> nameColumn;
    private Column<ProductDTO, String> thumbnailColumn;
    private TextColumn<ProductDTO> themeColumn;
    private TextColumn<ProductDTO> sectorColumn;

    @Override
    public void initTableColumns(CellTable<ProductDTO> dataGrid) {

        editColumn = new Column<ProductDTO, String>(new ButtonCell()) {
            @Override
            public String getValue(ProductDTO object) {
                return "Edit";
            }
        };
        addColumn(editColumn, "Action", "100px");
        editColumn.setFieldUpdater(new FieldUpdater<ProductDTO, String>() {

            @Override
            public void update(int index, final ProductDTO productDTO, String value) {
                Admin.clientFactory.getPlaceController().goTo(new ProductPlace(productDTO.getId()));
            }
        });

        thumbnailColumn = new Column<ProductDTO, String>(new ImageCell(30, 30)) {

            @Override
            public String getValue(ProductDTO object) {
                return object.getImageUrl();
            }
        };
        addColumn(thumbnailColumn, "Image", "50px");

        nameColumn = new TextColumn<ProductDTO>() {
            @Override
            public String getValue(ProductDTO object) {
                return object.getName();
            }
        };
        addResizableColumn(nameColumn, "Name", "250px");
        nameColumn.setSortable(true);

        themeColumn = new TextColumn<ProductDTO>() {
            @Override
            public String getValue(ProductDTO object) {
                return object.getThematic().toString();
            }
        };
        addResizableColumn(themeColumn, "Theme", "100px");
        themeColumn.setSortable(true);

        sectorColumn = new TextColumn<ProductDTO>() {
            @Override
            public String getValue(ProductDTO object) {
                return object.getSector().toString();
            }
        };
        addResizableColumn(sectorColumn, "Sector", "100px");
        sectorColumn.setSortable(true);

        // add dummy column for resize
        addColumn(new TextColumn<ProductDTO>() {
            @Override
            public String getValue(ProductDTO object) {
                return "";
            }
        }, "");

    }

    // TODO - set the ordering to be the one selected
    public void setOrderBy(String orderby) {
        switch (orderby) {
            case "name":
                break;
        }
    }
}
