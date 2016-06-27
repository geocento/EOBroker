package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

public class ResizableHeader<T> extends Header<String> {

    private Column<T, ?> column = null;
	private AbstractCellTable<T> cellTable;
	private String title = "";
    private String tooltip;
	static private final int width = 10;

	public ResizableHeader(String title, String tooltip, AbstractCellTable<T> cellTable, Column<T, ?> column) {
		super(new HeaderCell());
		this.title  = title;
        this.tooltip = tooltip;
		this.cellTable = cellTable;
		this.column = column;
	}

    public ResizableHeader(String title, AbstractCellTable<T> cellTable, Column<T, ?> column) {
        this(title, null, cellTable, column);
    }

    @Override
	public String getValue() 
	{ 
		return title;
	}
	
	@Override
	public void onBrowserEvent(Context context, Element target, NativeEvent event) {
	    String eventType = event.getType();
		int clientX = event.getClientX();
		int absoluteLeft = target.getAbsoluteLeft();
		int offsetWidth = target.getOffsetWidth();
		if (clientX > absoluteLeft + offsetWidth - width) {
			setCursor(target, Cursor.COL_RESIZE);
		} else {
			if(column.isSortable()) {
				setCursor(target, Cursor.POINTER);
			} else {
				setCursor(target, Cursor.DEFAULT);
			}
		}
    	if(eventType.equals("mousedown")) {
			if (clientX > absoluteLeft + offsetWidth - width) {
	            new ColumnResizeHelper<T>(cellTable, column, target);
			}
	        event.preventDefault();
	        event.stopPropagation();
	    } else {
	    	return;
	    }
	}

	private void setCursor(Element element, Cursor cursor) {
		element.getStyle().setCursor(cursor);
	}

class ColumnResizeHelper<T> implements NativePreviewHandler {

	  private HandlerRegistration handler;
	  private AbstractCellTable<T> table;
	  private Column<T, ?> col;
	  private Element el;

	  public ColumnResizeHelper(AbstractCellTable<T> table, Column<T, ?> col, Element el) {
	    this.el = el;
	    this.table = table;
	    this.col = col;
	    handler = Event.addNativePreviewHandler(this);
	  }

	  @Override
	  public void onPreviewNativeEvent(NativePreviewEvent event) {
	    NativeEvent nativeEvent = event.getNativeEvent();
	    nativeEvent.preventDefault();
	    nativeEvent.stopPropagation();

	    if (nativeEvent.getType().equals("mousemove")) {
	      int absoluteLeft = el.getAbsoluteLeft();
	      int clientX = nativeEvent.getClientX();
	      int newWidth = clientX - absoluteLeft;
	      newWidth = newWidth < 10 ? 10 : newWidth;
	      table.setColumnWidth(col, newWidth + "px");
	    } else if (nativeEvent.getType().equals("mouseup")) {
	      handler.removeHandler();
	    }
	  }
	}

	static class HeaderCell extends AbstractCell<String> {
	
		public HeaderCell() {
			super("click", "mousedown", "mousemove", "mouseover", "mouseout");
		}
		
		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
			sb.append(
					new SafeHtmlBuilder()
			        .append(SafeHtmlUtils.fromString(value))
			        .toSafeHtml());
		}
		
	}
};


