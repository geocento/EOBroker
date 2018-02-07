package com.geocento.webapps.eobroker.customer.server.utils;

import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by thomas on 27/11/2017.
 */
public class StatsHelper {

    static Logger logger = Logger.getLogger(StatsHelper.class);

    static InetAddress address = null;
    static {
        try {
            address = InetAddress.getByName(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.statsdserver));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static String messageTemplate = "$bucket:$value|$type";

    public static void addCounter(String category, String id) {
        addCounter(category + "." + id, 1);
    }

    public static void addCounter(String category, String id, int value) {
        addCounter(category + "." + id, value);
    }

    public static void addCounter(String bucket, int value) {
        try {
            String message = messageTemplate
                    .replace("$bucket", createBucket(bucket))
                    .replace("$value", value + "")
                    .replace("$type", "c")
                    ;
            sendStatsdMessage(message);
        } catch (Exception e) {
            logger.error("Failed to send message to statsd, reason is: " + e.getMessage(), e);
        }
    }

    public static String createBucket(String bucket) {
        return Configuration.getProperty(Configuration.APPLICATION_SETTINGS.bucketprefix) + "." + bucket;
    }

    private static void sendStatsdMessage(String message) throws IOException {
        DatagramPacket packet = new DatagramPacket(
                message.getBytes(), message.length(), address, 8125
        );
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.send(packet);
    }

    public static String getStatsUrl(List<String> targets, int width, int height, String dateOption, String title) {
        // check the date option value and adjust the time interval to have 5px width per point
        int points = width / 5;
        String unit = dateOption.replaceAll("[^A-Za-z]+", "");
        if(unit.endsWith("s")) {
            unit = unit.substring(0, unit.length() - 1);
        }
        int value = Integer.parseInt(dateOption.replaceAll("[^0-9]+", ""));
        double intervalValue = ((double) value) / points;
        String intervalString = getIntervalString(intervalValue, unit);
        return Configuration.getProperty(Configuration.APPLICATION_SETTINGS.graphiteUrl) +
                "?width=" + width +
                "&height=" + height +
                "&bgcolor=white&fgcolor=black" +
                "&from=" + dateOption +
                "&hideLegend=true" +
                (title == null ? "" : "&vtitle=" + title) +
                ListUtil.toString(targets, new ListUtil.GetLabel<String>() {
                    @Override
                    public String getLabel(String target) {
                        return "&target=" + "hitcount(" + target + ",\"" + intervalString + "\", true)";
                    }
                }, "");
    }

    public static String getStatsUrl(String target, int width, int height, String dateOptions, String title) {
        return getStatsUrl(ListUtil.toList(target), width, height, dateOptions, title);
    }

    public static String getStatsUrl(String bucket, List<String> buckets, int width, int height, String dateOptions) {
        return Configuration.getProperty(Configuration.APPLICATION_SETTINGS.graphiteUrl) +
                "?width=" + width +
                "&height=" + height +
                "&bgcolor=white&fgcolor=black" +
                "&from=" + dateOptions +
                "&hideLegend=true" +
                "&vtitle=Number of views" +
                ListUtil.toString(buckets, new ListUtil.GetLabel<String>() {
                    @Override
                    public String getLabel(String value) {
                        return "&target=stats." + createBucket(bucket + "." + value);
                    }
                }, "");
    }

    public static void addSearchCounter(Long companyId, String category, String id) {
        addCounter(companyId + ".search." + category, id);
    }


    private static String getIntervalString(double intervalValue, String unit) {
        if (intervalValue > 1) {
            return (int) intervalValue + unit;
        } else {
            int multiplier = 0;
            switch (unit) {
                case "year":
                    multiplier = 12;
                    unit = "month";
                    break;
                case "month":
                    multiplier = 30;
                    unit = "day";
                    break;
                case "week":
                    multiplier = 7;
                    unit = "day";
                    break;
                case "day":
                    multiplier = 24;
                    unit = "hour";
                    break;
                case "hour":
                    multiplier = 60;
                    unit = "min";
                    break;
                case "min":
                    multiplier = 60;
                    unit = "second";
                    break;
                default:
                    return "10second";
            }
            intervalValue = intervalValue * multiplier;
            return getIntervalString(intervalValue, unit);
        }
    }

}
