package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.shared.entities.ApplicationSettings;
import com.geocento.webapps.eobroker.common.shared.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerUtil {

    public static long minuteInMs = 60 * 1000L;
    public static long hoursInMs = minuteInMs * 60;

    public static String getUrlData(String urlString) throws Exception {
			String response = "";
			HttpURLConnection connection = null;
			InputStream connectionIstream = null; //output for the target is input for the connection
			
			try {
				// step 1: initialize
				URL targetURL = new URL(urlString);
				connection = (HttpURLConnection) targetURL.openConnection();
				connection.setRequestMethod("GET");
//				connection.setRequestProperty( "Content-Type", "application/xml");
				//step 2: proxy requests
				//default for setDoInput is true
				connectionIstream = connection.getInputStream();
				//step 3: return output
				//can output be the same for GET/POST? or different return headers?
				//servlet may return 3 things: status code, response headers, response body
				// status code and headers have to be set before response body
				int bufferSize = 4*4*1024;//same buffer size as in Jetty utils (2*8192)
				byte[] buffer = new byte[bufferSize];
				int read = 0;
				while ((read = connectionIstream.read(buffer)) != -1) {
					response += new String(buffer, 0, read);
				}
			// if not targetURL send page that targetURL is required param
			} finally {
				if(connectionIstream != null) { connectionIstream.close(); }
			}

			return response;
		}

    public static String postUrlData(String webURL, String urlParameters) throws IOException {
        return postUrlData(webURL, urlParameters, "application/x-www-form-urlencoded");
    }

    public static String postUrlData(String webURL, String payload, String contentType) throws IOException {
        String response = "";

        HttpURLConnection connection = null;
        InputStream connectionIstream = null;

        // clear the double slashes if needed
        webURL = webURL.replaceAll("(?<!(http:|https:))//", "/");

        // add some debug log information
        try {
            URL targetURL = new URL(webURL);
            connection = (HttpURLConnection) targetURL.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Content-Length", payload.length() + "");
            connection.setUseCaches(false);
            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();
            //Get Response
            connectionIstream = connection.getInputStream();
            int bufferSize = 4*4*1024;//same buffer size as in Jetty utils (2*8192)
            byte[] buffer = new byte[bufferSize];
            int read = 0;
            while ((read = connectionIstream.read(buffer)) != -1) {
                response += new String(buffer, 0, read);
            }
        } catch (Exception e){
            throw e;
        } finally {
            if(connectionIstream != null) { connectionIstream.close(); }
        }
        return response;
    }

    public static void copyInputStreamToFile(InputStream filecontent, File file, int maxFileSize) throws IOException {
        int size = filecontent.available();
        if(size > maxFileSize) {
            throw new IOException("File too long, maximum " + maxFileSize + " bytes files allowed");
        }
        byte[] buffer = new byte[filecontent.available()];
        filecontent.read(buffer);
        OutputStream outStream = new FileOutputStream(file);
        outStream.write(buffer);
        outStream.close();
    }

    public static String getClientIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static Date parseDate(String dateString) throws ParseException {
        if(dateString == null) {
            return null;
        }
        dateString = dateString.toLowerCase().trim();
        String dateFormat = determineDateFormat(dateString);
        if(dateFormat == null) {
            return null;
        }
        Date date = new SimpleDateFormat(dateFormat, new Locale("en")).parse(dateString.toUpperCase());
        if(date.before(new Date(0))) {
            int year = date.getYear();
            date.setYear((year < 50 ? 2000 : 1900) + year);
        }
        return date;
    }

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
        put("^\\d{8}$", "yyyyMMdd");
        put("^\\d{4}$", "yyyy");
        put("^[a-z]{3} \\d{4}$", "MMM yyyy");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
        put("^\\d{1,2}-\\d{1,2}-\\d{2}$", "dd-MM-yy");
        put("^\\d{1,2}-[a-z]{3}-\\d{2}$", "dd-MMM-yy");
        put("^\\d{1,2}-\\d{2}$", "MM-yy");
        put("^[a-z]{3}-\\d{2}$", "MMM-yy");
        put("^\\d{1,2}/\\d{1,2}/\\d{2}$", "dd/MM/yy");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
    }};

    static private void initiateDates() {
        for(String delimiter : new String[]{" ", "-", "/"}) {
            DATE_FORMAT_REGEXPS.put("^\\d{1,2}" + delimiter + "\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy");
        }
    }

    /**
     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     * @param dateString The date string to determine the SimpleDateFormat pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
     * @see java.text.SimpleDateFormat
     */
    public static String determineDateFormat(String dateString) {
        if(dateString == null) {
            return null;
        }
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.matches(regexp)) {
                return DATE_FORMAT_REGEXPS.get(regexp);
            }
        }
        return null; // Unknown format.
    }

    public static Date getMorningDate(Date date) {
        // make sure the start date starts at the beginning of the day and the stop date at the end
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMidnightDate(Date date) {
        // make sure the start date starts at the beginning of the day and the stop date at the end
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static String getRequestBody(HttpServletRequest request) throws IOException {
        // Read from request
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public static ApplicationSettings getSettings() {
        EntityManager em = EMF.get().createEntityManager();
        try {
            return em.find(ApplicationSettings.class, 1L);
        } finally {
            em.close();
        }
    }

    public static List<User> getUsersAdministrator() {
        EntityManager em = EMF.get().createEntityManager();
        TypedQuery<User> query = em.createQuery("Select u from users u where u.role = :userRole", User.class);
        query.setParameter("userRole", User.USER_ROLE.administrator);
        return query.getResultList();
    }

    public static String getDataFilePath(String filePath) {
        return getDataFile(filePath).getAbsolutePath();
    }

    public static File getDataFile(String filePath) {
        return new File(ServerUtil.getSettings().getDataDirectory(), filePath);
    }
}
