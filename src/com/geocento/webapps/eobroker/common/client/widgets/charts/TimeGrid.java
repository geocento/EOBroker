package com.geocento.webapps.eobroker.common.client.widgets.charts;

import com.geocento.webapps.eobroker.common.client.utils.DateUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 18/02/2015.
 */
public class TimeGrid extends Composite implements ResizeHandler {

    interface TimeGridUiBinder extends UiBinder<HTMLPanel, TimeGrid> {
    }

    private static TimeGridUiBinder ourUiBinder = GWT.create(TimeGridUiBinder.class);

    @UiField Style style;

    public interface Style extends CssResource {

        String timeLabel();

        String tickItem();

        String grow();
    };

    @UiField
    AbsolutePanel timeBands;
    @UiField
    AbsolutePanel timeLabels;
    @UiField
    AbsolutePanel timeTicks;

    private Date minDate;
    private Date maxDate;

    public TimeGrid() {

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    public void setTimeFrame(Date minDate, Date maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
        drawBands();
    }

    private void drawBands() {
        // clear the bands
        timeBands.clear();
        timeLabels.clear();

        Date nowTime = new Date();
        // draw bands based on duration with minimum 3 bands
        DateUtil.BANDTYPE bandType = DateUtil.getTimeBand(minDate, maxDate, 3);
        // find the position of the next band
        Date currentDate;
        // start with 150 to make sure the first label is added
        int maxDistance = 150;
        switch(bandType) {
            case YEAR:
                currentDate = DateUtil.truncateToYear(minDate);
                maxDistance = 100;
                break;
            case MONTH:
                currentDate = DateUtil.truncateToMonth(minDate);
                maxDistance = 100;
                break;
            case DAY:
                currentDate = DateUtil.truncateToDay(minDate);
                maxDistance = 100;
                break;
            case HOUR:
                currentDate = DateUtil.truncateToHour(minDate);
                maxDistance = 120;
                break;
            case MN:
                currentDate = DateUtil.truncateToMinutes(minDate);
                maxDistance = 120;
                break;
            case SEC:
                currentDate = DateUtil.truncateToSeconds(minDate);
                maxDistance = 120;
                break;
            default:
                currentDate = minDate;
                maxDistance = 150;
        }
        int numBands = 0;
        boolean first = true;
        int distance = 0;
        for(; currentDate.before(maxDate); numBands++) {
            // update the year values
            Date previousDate = new Date(currentDate.getTime());
            switch(bandType) {
                case YEAR:
                    currentDate.setYear(currentDate.getYear() + 1);
                    break;
                case MONTH:
                    currentDate.setMonth(currentDate.getMonth() + 1);
                    break;
                case DAY:
                    currentDate = new Date(currentDate.getTime() + DateUtil.dayInMs);
                    break;
                case HOUR:
                    currentDate = new Date(currentDate.getTime() + DateUtil.hourInMs);
                    break;
                case MN:
                    currentDate = new Date(currentDate.getTime() + DateUtil.minuteInMs);
                    break;
                case SEC:
                    currentDate = new Date(currentDate.getTime() + DateUtil.secondInMs);
                    break;
                default:
                    currentDate = new Date(currentDate.getTime() + 10);
            }
            // calculate the two sides of the band
            int startPos = getDatePosition(previousDate);
            int stopPos = getDatePosition(currentDate);
            // try to place a label every maxDistance pixels
            if(distance == 0) {
                String labelString = "";
                switch(bandType) {
                    case YEAR:
                        labelString = DateUtil.displayDateOnly(currentDate);
                        break;
                    case MONTH:
                        labelString = DateUtil.displayDateOnly(currentDate);
                        break;
                    case DAY:
                        labelString = DateUtil.displayDateOnly(currentDate);
                        break;
                    default:
                        labelString = DateUtil.displayTimeOnly(currentDate);
                        if(first) {
                            labelString = DateUtil.displaySimpleUTCDate(currentDate);
                            first = false;
                        }
                }
                int position = (int) getDatePosition(currentDate);
                // add tick for the label
                HTMLPanel timeLabel = new HTMLPanel(labelString);
                timeLabel.addStyleName(style.timeLabel());
                timeLabels.add(timeLabel, position, 2);
            }
            // add the width
            distance += stopPos - startPos;
            // reset if we have reached the max distance
            if(distance > maxDistance) {
                distance = 0;
            }
            // draw one band out of two
            if(numBands%2 == 1) {
                SimplePanel panel = new SimplePanel();
                panel.getElement().getStyle().setBackgroundColor(currentDate.before(nowTime) ? "#f8f8f0" : "#f0f0f8");
                panel.setWidth(stopPos - startPos + "px");
                panel.setHeight("100%");
                timeBands.add(panel, startPos, 0);
            }
        }
    }

    public int getDatePosition(Date date) {
        double duration = maxDate.getTime() - minDate.getTime();
        double width = timeBands.getOffsetWidth();
        return (int) (((double) date.getTime() - minDate.getTime()) / duration * width);
    }

    public void addTick(HTMLPanel tickPanel, Date date, int top) {
        addTick(tickPanel, getDatePosition(date), top);
    }

    public void addTick(HTMLPanel tickPanel, int left, int top) {
        timeTicks.add(tickPanel, left, top);
    }

    public void clearAll() {
        timeBands.clear();
        timeLabels.clear();
        clearTicks();
    }

    public void clearTicks() {
        timeTicks.clear();
    }

    public void setDates(List<Date> dates) {
        for (Date date : dates) {
            HTMLPanel tickPanel = new HTMLPanel("");
            tickPanel.addStyleName(style.tickItem());
            tickPanel.addStyleName(style.grow());
            addTick(tickPanel, date, 15);
        }
    }

    @Override
    public void onResize(ResizeEvent event) {
        drawBands();
    }

}