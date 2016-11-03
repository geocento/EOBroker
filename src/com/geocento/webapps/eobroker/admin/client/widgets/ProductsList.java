package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 21/06/2016.
 */
public class ProductsList extends AsyncPagingWidgetList<ProductDTO> {

    private int pagerSize;
    private String orderBy;

/*
    private Column<ProductDTO, String> editColumn;
    private TextColumn<ProductDTO> nameColumn;
    private Column<ProductDTO, String> thumbnailColumn;
    private TextColumn<ProductDTO> themeColumn;
    private TextColumn<ProductDTO> sectorColumn;
*/

/*
    @Override
    protected Cell<ProductDTO> getCell() {
        return new AbstractCell<ProductDTO>() {
            @Override
            public void render(Context context, ProductDTO value, SafeHtmlBuilder sb) {
                new ProductWidget(value).getElement().getInnerHTML();
            }
        };
    }
*/

    public ProductsList() {
        this(10);
    }

    public ProductsList(int pageSize) {
        super(pageSize, 12, 4, 3);
    }

    @Override
    protected Widget getItemWidget(ProductDTO value) {
        return new ProductWidget(value);
    }

/*
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
*/
}
