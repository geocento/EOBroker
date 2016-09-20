package com.geocento.webapps.eobroker.common.client.utils;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Created by thomas on 13/09/2016.
 */
public class DateUtils {

    public static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);

    public static long secondInMs = 1000;
    public static long minuteInMs = 60 * secondInMs;
    public static long hourInMs = 60 * minuteInMs;
    public static long dayInMs = 24 * hourInMs;

    public static String formatTimePeriod(Date start, Date stop) {
        return "from " + formatDateOnly(start) + " until " + formatDateOnly(stop);
    }

    public static String formatDateOnly(Date start) {
        return dateTimeFormat.format(start);
    }

    public static String getDuration(Date time) {

        if(time == null) {
            return "moments ago";
        }

        long elapsedtime = (long) Math.floor((new Date()).getTime() - time.getTime());

        return elapsedtime < 5 * minuteInMs ? "moments ago" : getDuration((long) (elapsedtime)) + " ago";
    }

    /*
     * returns a duration string based on duration
     * duration should be in seconds
     */
    public static String getDuration(long duration) {
        String durationString = "";

        if(duration < secondInMs) {
            durationString += duration + "ms";
        } else if(duration < minuteInMs) {
            durationString += Math.floor(duration / secondInMs) + "s";
        } else if(duration < hourInMs) {
            durationString += (int) Math.floor(duration / minuteInMs) + "mns";
        } else if(duration < dayInMs) {
            durationString += (int) Math.floor(duration / hourInMs) + "hrs";
        } else {
            durationString += (int) Math.floor(duration / dayInMs) + " days";
        }

        return durationString;
    }

}
