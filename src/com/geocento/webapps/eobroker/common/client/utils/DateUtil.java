package com.geocento.webapps.eobroker.common.client.utils;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import java.util.Date;

/**
 * Created by thomas on 13/09/2016.
 */
public class DateUtil {

    static public enum BANDTYPE {YEAR, MONTH, WEEK, DAY, HOUR, MN, SEC, MS};

    public static DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);
    public static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);
    private static DateTimeFormat fmtSimple = DateTimeFormat.getFormat("MMM dd, yyyy 'at' HH:mm:ss 'UTC'");
    private static DateTimeFormat fmtDateOnly = DateTimeFormat.getFormat("MMM dd, yyyy");
    private static DateTimeFormat fmtMonthOnly = DateTimeFormat.getFormat("MMMM");
    private static DateTimeFormat fmtDayOnly = DateTimeFormat.getFormat("EEEE 'the' dd");
    private static DateTimeFormat fmtTimeOnly = DateTimeFormat.getFormat("HH:mm:ss");
    private static DateTimeFormat fmtUTC = DateTimeFormat.getFormat("yyyy-MM-ddTHH:mm:ssZ");
    private static DateTimeFormat fmtUTCMS = DateTimeFormat.getFormat("yyyy-MM-ddTHH:mm:ss.SSSZ");

    public static long secondInMs = 1000;
    public static long minuteInMs = 60 * secondInMs;
    public static long hourInMs = 60 * minuteInMs;
    public static long dayInMs = 24 * hourInMs;
    public static long monthInMs = 30 * dayInMs;
    public static long yearInMs = (long) 365.25 * dayInMs;

    public static String formatTimePeriod(Date start, Date stop) {
        return "from " + formatDateOnly(start) + " until " + formatDateOnly(stop);
    }

    public static String formatDateOnly(Date start) {
        return dateFormat.format(start);
    }

    public static String formatDateTime(Date start) {
        return dateTimeFormat.format(start);
    }

    public static String displaySimpleUTCDate(Date date) {
        return date == null ? "" : fmtSimple.format(date, TimeZone.createTimeZone(0));
    }

    public static String displayDateOnly(Date date) {
        return date == null ? "" : fmtDateOnly.format(date, TimeZone.createTimeZone(0));
    }

    public static String displayTimeOnly(Date date) {
        return date == null ? "" : fmtTimeOnly.format(date, TimeZone.createTimeZone(0));
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

    public static String getDetailedDuration(long duration) {
        String durationString = getDuration(duration);
        long remaining = 0;
        if(duration < secondInMs) {
        } else if(duration < minuteInMs) {
            remaining = duration % secondInMs;
        } else if(duration < hourInMs) {
            remaining = duration % minuteInMs - duration % secondInMs;
        } else if(duration < dayInMs) {
            remaining = duration % hourInMs - duration % minuteInMs;
        } else {
            remaining = duration % dayInMs - duration % hourInMs;
        }
        return durationString + (remaining > 0 ? " " + getDuration(remaining) : "");
    }

    // from http://www.timvasil.com/blog14/post/2008/03/23/Calendar-class-for-date-manipulation-in-GWT-or-JavaScript.aspx
    public static Date truncateToTime(Date date)
    {
        long time = date.getTime();
        return new Date(time - truncateToDay(date).getTime());
    }

    public static Date truncateToSeconds(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
    }

    public static Date truncateToMinutes(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), 0);
    }

    public static Date truncateToHour(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), 0, 0);
    }

    public native static double truncateToDayJs(double time) /*-{
        var date = new Date(time);
        date.setUTCHours(0);
        date.setUTCMinutes(0);
        date.setUTCSeconds(0);
        date.setUTCMilliseconds(0);
        return date.getTime();
    }-*/;

    public native static double truncateToMonthJs(double time) /*-{
        var date = new Date(time);
        date.setUTCDate(1);
        date.setUTCHours(0);
        date.setUTCMinutes(0);
        date.setUTCSeconds(0);
        date.setUTCMilliseconds(0);
        return date.getTime();
    }-*/;

    public native static double truncateToYearJs(double time) /*-{
        var date = new Date(time);
        date.setUTCMonth(0);
        date.setUTCDate(1);
        date.setUTCHours(0);
        date.setUTCMinutes(0);
        date.setUTCMilliseconds(0);
        return date.getTime();
    }-*/;

    public static Date truncateToDay(Date date) {
        return new Date((long) truncateToDayJs(date.getTime()));
    }

    public static Date truncateToMonth(Date date) {
        return new Date((long) truncateToMonthJs(date.getTime()));
    }

    public static Date truncateToYear(Date date) {
        return new Date((long) truncateToYearJs(date.getTime()));
    }

    public static Date addHours(Date date, int hours)
    {
        Date newDate = new Date(date.getTime());
        newDate.setHours(date.getHours() + hours);
        return newDate;
    }

    public static Date addDays(Date date, int days)
    {
        Date newDate = new Date(date.getTime());
        CalendarUtil.addDaysToDate(newDate, days);
        return newDate;
    }

    public static Date addMonths(Date date, int months)
    {
        Date newDate = new Date(date.getTime());
        CalendarUtil.addMonthsToDate(newDate, months);
        return newDate;
    }

    public static int getDaysInMonth(Date date)
    {
        return getDaysInMonth(date.getYear() + 1900, date.getMonth());
    }

    public static int getDaysInMonth(int year, int month)
    {
        switch (month)
        {
            case 1:
                return (((year % 4) == 0 && (year % 100) != 0) || (year % 400) == 0) ? 29 : 28;

            case 3:
            case 5:
            case 8:
            case 10:
                return 30;

            default:
                return 31;
        }
    }

    /**
     * Determines the number of days between two dates, always rounding up so a difference of 1 day 1 second
     * yield a return value of 2.
     */
    public static int dayDiff(Date endDate, Date startDate)
    {
        return (int)Math.ceil(((double)endDate.getTime() - startDate.getTime()) / dayInMs);
    }

    public static String displayUTCDate(Date date) {
        if(date == null) {
            return "Undefined";
        }
        return fmtUTC.format(date);
    }

    public static Date parseUTCDate(String startTimeString) {
        if(startTimeString == null) {
            return null;
        }
        if(startTimeString.endsWith("Z")) {
            startTimeString = startTimeString.substring(0, startTimeString.length() - 1) + "GMT-00:00";
        }
        boolean withMs = startTimeString.contains(".");
        if(withMs) {
            return fmtUTCMS.parse(startTimeString);
        } else {
            return fmtUTC.parse(startTimeString);
        }
    }

    public static BANDTYPE getTimeBand(Date startTime, Date stopTime, int minBands) {
        long duration = stopTime.getTime() - startTime.getTime();
        // draw the bands
        // find out which band is the best division
        if(duration > minBands * yearInMs) {
            // add a band for each second year
            return BANDTYPE.YEAR;
        } else if(duration > minBands * monthInMs) {
            // add a band for each second year
            return BANDTYPE.MONTH;
//		} else if(duration > minBands * weekInMs) {
//			// add a band for each second year
//			return BANDTYPE.WEEK);
        } else if(duration > minBands * dayInMs) {
            // add a band for each second year
            return BANDTYPE.DAY;
        } else if(duration > minBands * hourInMs) {
            // add a band for each second year
            return BANDTYPE.HOUR;
        } else if(duration > minBands * minuteInMs) {
            // add a band for each second year
            return BANDTYPE.MN;
        } else if(duration > minBands * secondInMs) {
            // add a band for each second year
            return BANDTYPE.SEC;
        } else {
            return BANDTYPE.MS;
        }
    }

}
