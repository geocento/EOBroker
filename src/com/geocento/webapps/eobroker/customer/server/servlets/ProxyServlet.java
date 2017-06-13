/*
 * polymap.org
 * Copyright 2009, Polymap GmbH, and individual contributors as indicated
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package com.geocento.webapps.eobroker.customer.server.servlets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * For JavaScript security reasons ( cross domain ) we sometimes 
 * need a proxy to load data ( e.g. for WFS )
 * This servlet is suposed to be a replacement for the proxy.cgi provided
 * by OpenLayers which is written in python. This Servlet (written in Java) fits
 * better into the RAP enviroment. 
 * 
 * @author Marcus -LiGi- B&uuml;schleb < mail: ligi (at) polymap (dot) de >
 * 
 */

public class ProxyServlet extends HttpServlet {

	private static final long serialVersionUID = -6526120143460180643L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doProxy(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO checkme - only GET is tested right now
		doProxy(request, response);
	}

	private void doProxy(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String url_param = null;
		url_param = request.getParameter("url");

		if (url_param == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"URL Parameter Missing");
			return;
		}

        url_param = URLDecoder.decode(url_param, "UTF-8");

		try {
			URL url = new URL(url_param);
            // make sure the query string is escaped
            if(url.getQuery() != null) {
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = new URL(uri.toASCIIString());
            }

            boolean hostFiltering = getServletConfig().getInitParameter("host_list") != null;
			/* Check if the host is in the list of allowed hosts */
			if (hostFiltering) {
                List<String> host_list = Arrays.asList(getServletConfig()
                        .getInitParameter("host_list").split(","));

                // check source calling
                String host = request.getHeader("X-FORWARDED-FOR");
                if(host == null) {
                    host = request.getRemoteHost();
                }
                if(!host_list.contains(host)) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "URL Parameter Bad - the Host " + host
                                    + " is not in my list of valid Hosts!");
                    return;
                }
			}

			URLConnection con = url.openConnection();

            copyRequestHeaders(request, con);

            if (con.getContentType() != null)
				response.setContentType(con.getContentType());
			else
				response.setContentType("text/xml");

			/* check if the client accepts gzip Encoding */
			boolean client_accepts_gzip = false; // be pessimistic

			if (request.getHeader("Accept-Encoding") != null) {
				List<String> encoding_list = Arrays.asList(request.getHeader(
						"Accept-Encoding").split(","));
				client_accepts_gzip = (encoding_list.contains("gzip"));
			}
			response.setDateHeader("Date", System.currentTimeMillis());

			if (client_accepts_gzip) {
				response.setHeader("Content-Encoding", "gzip");
				ByteArrayOutputStream output_to_tmp = new ByteArrayOutputStream();
                InputStream conStream = con.getInputStream();
				IOUtils.copy(conStream, output_to_tmp);
		
				OutputStream output_to_response = new GZIPOutputStream(response
						.getOutputStream());
				output_to_response.write(output_to_tmp.toByteArray());
				output_to_response.close();

			} else { // client will not accept gzip -> dont compress
				IOUtils.copy(con.getInputStream(), response.getOutputStream());
			}
		} catch (Exception e) {
			System.out.println("Err" + e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Err:" + e);
        }

    }

    /**
     * Copy request headers from the servlet client to the proxy request.
     * This is easily overridden to add your own.
     */
    protected void copyRequestHeaders(HttpServletRequest servletRequest, URLConnection proxyRequest) {
        // Get an Enumeration of all of the header names sent by the client
        @SuppressWarnings("unchecked")
        Enumeration<String> enumerationOfHeaderNames = servletRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = enumerationOfHeaderNames.nextElement();
            copyRequestHeader(servletRequest, proxyRequest, headerName);
        }
    }

    /** These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set&lt;String&gt; because this
     * approach does case insensitive lookup faster.
     */
    protected static final HeaderGroup hopByHopHeaders;
    static {
        hopByHopHeaders = new HeaderGroup();
        String[] headers = new String[] {
                "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
                "TE", "Trailers", "Transfer-Encoding", "Upgrade", "Cookie", "Accept-Encoding" };
        for (String header : headers) {
            hopByHopHeaders.addHeader(new BasicHeader(header, null));
        }
    }

    protected static final HeaderGroup headersToAdd;
    static {
        headersToAdd = new HeaderGroup();
        String[] headers = new String[] {
                "Host", "User-Agent", "Referer"
                };
        for (String header : headers) {
            headersToAdd.addHeader(new BasicHeader(header, null));
        }
    }

    /**
     * Copy a request header from the servlet client to the proxy request.
     * This is easily overridden to filter out certain headers if desired.
     */
    protected void copyRequestHeader(HttpServletRequest servletRequest, URLConnection proxyRequest,
                                     String headerName) {
        //Instead the content-length is effectively set via InputStreamEntity
        if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))
            return;
        if (hopByHopHeaders.containsHeader(headerName))
            return;
        if(!headersToAdd.containsHeader(headerName))
            return;

        @SuppressWarnings("unchecked")
        Enumeration<String> headers = servletRequest.getHeaders(headerName);
        while (headers.hasMoreElements()) {
            String headerValue = headers.nextElement();
            proxyRequest.setRequestProperty(headerName, headerValue);
        }
    }

}