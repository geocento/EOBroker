package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.events.EditUser;
import com.geocento.webapps.eobroker.admin.shared.dtos.UserDescriptionDTO;
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
public class UsersList extends AsyncPagingCellTable<UserDescriptionDTO> {

    public UsersList() {
    }

    private Column<UserDescriptionDTO, String> editColumn;
    private TextColumn<UserDescriptionDTO> nameColumn;
    private TextColumn<UserDescriptionDTO> statusColumn;
    private Column<UserDescriptionDTO, String> thumbnailColumn;

    @Override
    public void initTableColumns(CellTable<UserDescriptionDTO> dataGrid) {

        nameColumn = new TextColumn<UserDescriptionDTO>() {
            @Override
            public String getValue(UserDescriptionDTO object) {
                return object.getName();
            }
        };
        addResizableColumn(nameColumn, "User Name", "100px");
        nameColumn.setSortable(true);

        statusColumn = new TextColumn<UserDescriptionDTO>() {
            @Override
            public String getValue(UserDescriptionDTO object) {
                return object.getStatus() == null ? "Undefined" : object.getStatus().toString();
            }
        };
        addResizableColumn(statusColumn, "User Status", "100px");
        statusColumn.setSortable(true);

        TextColumn<UserDescriptionDTO> emailColumn = new TextColumn<UserDescriptionDTO>() {
            @Override
            public String getValue(UserDescriptionDTO object) {
                return object.getEmail();
            }
        };
        addResizableColumn(emailColumn, "Email", "100px");
        emailColumn.setSortable(true);

        TextColumn<UserDescriptionDTO> roleColumn = new TextColumn<UserDescriptionDTO>() {
            @Override
            public String getValue(UserDescriptionDTO object) {
                return object.getUserRole().toString();
            }
        };
        addResizableColumn(roleColumn, "Role", "100px");
        roleColumn.setSortable(true);

        thumbnailColumn = new Column<UserDescriptionDTO, String>(new ImageCell(30, 30)) {

            @Override
            public String getValue(UserDescriptionDTO object) {
                return object.getCompanyDTO() == null ? null : object.getCompanyDTO().getIconURL();
            }
        };
        addColumn(thumbnailColumn, "Company", "100px");

        editColumn = new Column<UserDescriptionDTO, String>(new ButtonCell()) {
            @Override
            public String getValue(UserDescriptionDTO object) {
                return "Edit";
            }
        };
        addColumn(editColumn, "Action", "100px");
        editColumn.setFieldUpdater(new FieldUpdater<UserDescriptionDTO, String>() {

            @Override
            public void update(int index, final UserDescriptionDTO userDescriptionDTO, String value) {
                Admin.clientFactory.getEventBus().fireEvent(new EditUser(userDescriptionDTO));
            }
        });

    }

}
