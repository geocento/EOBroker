package com.geocento.webapps.eobroker.common.client.widgets.table.celltable;

import com.geocento.webapps.eobroker.common.client.widgets.ClickableImageCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.builder.shared.DivBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.view.client.SelectionModel;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public abstract class SubrowTableBuilder <T> extends MyDefaultCellTableBuilder<T>
{
    private static Logger logger = Logger.getLogger( "" );

    Set<T> deployedElements = new HashSet<T>();

    public SubrowTableBuilder(AbstractCellTable<T> cellTable)
    {
        super( cellTable );
    }

    @Override
    public void buildRowImpl( T rowValue, int absRowIndex )
    {
        buildDefaultRow( rowValue, absRowIndex );
        buildChildrenRow( rowValue, absRowIndex );
    }
    public void addDeployableColumn(final ImageResource deployed, final ImageResource collapsed)
    {
        Column<T, T> column = new Column<T, T>(new ClickableImageCell<T>() {
            @Override
            protected void performAction(String action, T value, NativeEvent event) {
                setDeployed(!isDeployed(value), value);
                cellTable.redraw();
            }

            @Override
            public void render(Context context, T value, SafeHtmlBuilder sb) {
                sb.append(templates.cell("Show information on this product", "Show information on this product", imgStyle,
                        isDeployed(value) ? makeImage(deployed) : makeImage(collapsed)));
            }
        }) {
            @Override
            public T getValue(T object) {
                return object;
            }
        };
        cellTable.addColumn(column);
        cellTable.setColumnWidth(column, 30, Style.Unit.PX);
    }

    private void setDeployed(boolean deployed, T element) {
        if(deployed) {
            deployedElements.add(element);
        } else {
            deployedElements.remove(element);
        }
    }

    private boolean isDeployed(T object) {
        return deployedElements.contains(object);
    }

    private void buildChildrenRow( T parent, int absRowIndex )
    {
        if ( isDeployed(parent) )
        {
            // Calculate the row styles.
            SelectionModel<? super T> selectionModel = cellTable.getSelectionModel();
            boolean isSelected =
                ( selectionModel == null || parent == null ) ? false : selectionModel.isSelected( parent );
            boolean isEven = absRowIndex % 2 == 0;
            StringBuilder trClasses = new StringBuilder("dataGridChildCell");
            if ( isSelected )
            {
                trClasses.append( selectedRowStyle );
            }
            buildChildRow( parent, isEven, isSelected, trClasses );
        }
    }

    protected void buildChildRow( T parent, boolean isEven, boolean isSelected,
                                  StringBuilder trClasses )
    {
        // only way to get subindex without modifying the abstractcellbuilder
        Context baseContext = createContext( 0 );

        // Build the row.
        TableRowBuilder tr = startRow();
        tr.className( trClasses.toString() );

        String evenOrOddStyle = "dataGridChildCell";

        /*
         * deployable column
         */
        TableCellBuilder td = initChildTd( tr, evenOrOddStyle, 0, isSelected );
        DivBuilder div = td.startDiv();
        div.style().outlineStyle( OutlineStyle.NONE ).endStyle();
        // Empty cell
        div.endDiv();
        td.endTD();

        /*
         * number column
         */
        Context context = new Context( baseContext.getIndex(), 1, parent, baseContext.getSubIndex() );
        td = initChildTd( tr, evenOrOddStyle, 1, isSelected );
        td.colSpan(cellTable.getColumnCount() - 1);
        div = td.startDiv();
        div.style().outlineStyle( OutlineStyle.NONE ).endStyle();
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        div.html( SafeHtmlUtils.fromTrustedString(getInformation(parent)) );
        div.endDiv();
        td.endTD();

        // End the row.
        tr.endTR();
    }

    protected abstract String getInformation(T parent);

    private TableCellBuilder initChildTd( TableRowBuilder tr, String evenOrOddStyle, int columnIndex, boolean isSelected )
    {
        Column<T, ?> column = cellTable.getColumn( columnIndex );
        HorizontalAlignmentConstant hAlign = column.getHorizontalAlignment();
        VerticalAlignmentConstant vAlign = column.getVerticalAlignment();
        StringBuilder tdClasses = new StringBuilder( cellStyle );
        tdClasses.append( evenOrOddStyle );
        if ( columnIndex == 0 )
        {
            tdClasses.append( firstColumnStyle );
        }
        if ( isSelected )
        {
            tdClasses.append( selectedCellStyle );
        }
        if ( columnIndex + 1 == cellTable.getColumnCount() )
        {
            tdClasses.append( lastColumnStyle );
        }
        TableCellBuilder td = tr.startTD();
        if ( hAlign != null )
        {
            td.align( hAlign.getTextAlignString() );
        }
        if ( vAlign != null )
        {
            td.vAlign( vAlign.getVerticalAlignString() );
        }
        td.className( tdClasses.toString() );
        return td;
    }

}
