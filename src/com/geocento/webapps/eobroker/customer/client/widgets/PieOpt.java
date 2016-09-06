package com.geocento.webapps.eobroker.customer.client.widgets;

import com.google.gwt.core.client.JsArray;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.corechart.PieChartTooltip;
import com.googlecode.gwt.charts.client.options.*;

/**
 * Adding a Donut Pie Functionality
	 * 
	 * @author DES
	 * 
	 */
public class PieOpt {
		private PieChartOptions _this;

		public PieOpt() {
			_this = PieChartOptions.create();
		}

		public PieOpt newInstance() {
			return new PieOpt();
		}

		public PieChartOptions get() {
			return _this;
		}

		public final void setBackgroundColor(BackgroundColor backgroundColor) {
			_this.setBackgroundColor(backgroundColor);
		}

		public final void setBackgroundColor(String color) {
			_this.setBackgroundColor(color);
		}

		public final void setChartArea(ChartArea chartArea) {
			_this.setChartArea(chartArea);
		}

		public final void setColors(String... colors) {
			_this.setColors(colors);
		}

		public final void setFontName(String name) {
			_this.setFontName(name);
		}

		public final void setFontSize(double size) {
			_this.setFontSize(size);
		}

		public final void setIs3D(boolean is3D) {
			_this.setIs3D(is3D);
		}

		public final void setLegend(Legend legend) {
			_this.setLegend(legend);
		}

		public final void setPieResidueSliceColor(String pieResidueSliceColor) {
			_this.setPieResidueSliceColor(pieResidueSliceColor);
		}

		public final void setPieResidueSliceLabel(String pieResidueSliceLabel) {
			_this.setPieResidueSliceLabel(pieResidueSliceLabel);
		}

		public final void setPieSliceBorderColor(String pieSliceBorderColor) {
			_this.setPieSliceBorderColor(pieSliceBorderColor);
		}

		public final void setPieSliceText(PieSliceText pieSliceText) {
			_this.setPieSliceText(pieSliceText);
		}

		public final void setPieSliceTextStyle(TextStyle pieSliceTextStyle) {
			_this.setPieSliceTextStyle(pieSliceTextStyle);
		}

		public final void setReverseCategories(boolean reverseCategories) {
			_this.setReverseCategories(reverseCategories);
		}

		public final void setSlices(JsArray<Slice> slices) {
			_this.setSlices(slices);
		}

		public final void setSliceVisibilityThreshold(double sliceVisibilityThreshold) {
			_this.setSliceVisibilityThreshold(sliceVisibilityThreshold);
		}

		public final void setTitle(String title) {
			_this.setTitle(title);
		}

		public final void setTitleTextStyle(TextStyle textStyle) {
			_this.setTitleTextStyle(textStyle);
		}

		public final void setTooltip(PieChartTooltip tooltip) {
			_this.setTooltip(tooltip);
		}

		public void setHole(double d) {
			setHole(get(), d);
		}

		private final native void setHole(PieChartOptions chartOptions, double d) /*-{
			chartOptions.pieHole = d;
		}-*/;
	}
