package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.UserSession;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.server.utils.StatsHelper;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.google.gwt.http.client.RequestException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by thomas on 27/11/2017.
 */
@Path("stats")
public class StatsResource {

    Logger logger = Logger.getLogger(StatsResource.class);

    @Context
    HttpServletRequest request;

    @GET
    @Path("view")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getViewStatsImage(@QueryParam("ids") String ids, @QueryParam("width") int width, @QueryParam("height") int height, @QueryParam("dateOption") String dateOption) throws RequestException {
        UserSession userSession = UserUtils.verifyUserSupplierSession(request);
        // build statsd url
        String statsdUrl = StatsHelper.getStatsUrl("view." + userSession.getUserCompanyId(), ListUtil.toList(ids.split(",")), width, height, dateOption);
        try {
            logger.debug("Streaming stats from URL " + statsdUrl);
            InputStream inputStream = new URL(statsdUrl).openStream();
            return new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    IOUtils.copy(inputStream, outputStream);
                }
            };
        } catch (Exception e) {
            throw new RequestException("Issue retrieving stats");
        }
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getSearchStatsImage(@QueryParam("ids") String ids, @QueryParam("width") int width, @QueryParam("height") int height, @QueryParam("dateOption") String dateOption) throws RequestException {
        UserSession userSession = UserUtils.verifyUserSupplierSession(request);
        // build statsd url
        String statsdUrl = StatsHelper.getStatsUrl("search." + userSession.getUserCompanyId(), ListUtil.toList(ids.split(",")), width, height, dateOption);
        try {
            logger.debug("Streaming stats from URL " + statsdUrl);
            InputStream inputStream = new URL(statsdUrl).openStream();
            return new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    IOUtils.copy(inputStream, outputStream);
                }
            };
        } catch (Exception e) {
            throw new RequestException("Issue retrieving stats");
        }
    }

    @GET
    @Path("productsview")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getProductsViewStatsImage(@QueryParam("ids") String ids, @QueryParam("width") int width, @QueryParam("height") int height, @QueryParam("dateOption") String dateOption) throws RequestException {
        UserSession userSession = UserUtils.verifyUserSupplierSession(request);
        // build statsd url
        String statsdUrl = StatsHelper.getStatsUrl("view.products", ListUtil.toList(ids.split(",")), width, height, dateOption);
        try {
            logger.debug("Streaming stats from URL " + statsdUrl);
            InputStream inputStream = new URL(statsdUrl).openStream();
            return new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    IOUtils.copy(inputStream, outputStream);
                }
            };
        } catch (Exception e) {
            throw new RequestException("Issue retrieving stats");
        }
    }
}
