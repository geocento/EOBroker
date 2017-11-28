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

    private static CharSequence createBucket(String bucket) {
        return Configuration.getProperty(Configuration.APPLICATION_SETTINGS.bucketprefix) + "." + bucket;
    }

    private static void sendStatsdMessage(String message) throws IOException {
        DatagramPacket packet = new DatagramPacket(
                message.getBytes(), message.length(), address, 8125
        );
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.send(packet);
    }

    public static String getStatsUrl(String bucket, List<String> buckets, int width, int height, String dateOptions) {
        return Configuration.getProperty(Configuration.APPLICATION_SETTINGS.graphiteUrl) +
                "?width=" + width +
                "&height=" + height +
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

}
