package com.geocento.webapps.eobroker.admin.server.servlets;

import com.geocento.webapps.eobroker.admin.server.util.UserUtils;
import com.geocento.webapps.eobroker.admin.shared.dtos.STATS_GRAPHS;
import com.geocento.webapps.eobroker.customer.server.utils.StatsHelper;
import com.google.gwt.http.client.RequestException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
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
    public StreamingOutput getViewStatsImage(@QueryParam("statsType") STATS_GRAPHS statsType, @QueryParam("width") int width, @QueryParam("height") int height, @QueryParam("dateOption") String dateOption) throws RequestException {
        String userName = UserUtils.verifyUserAdmin(request);
        // build statsd url
        String target = null;
        String prefix = "stats.";
        switch (statsType) {
            case userSignIn:
                target = prefix + StatsHelper.createBucket("users.signin");
                break;
            case userSignUp:
                target = prefix + StatsHelper.createBucket("users.signup");
                break;
            case userReset:
                target = prefix + StatsHelper.createBucket("users.reset");
                break;
            case userChangePassword:
                target = prefix + StatsHelper.createBucket("users.changepassword");
                break;
            case supplierSignIn:
                target = prefix + StatsHelper.createBucket("suppliers.signin");
                break;
            case productView:
                target = "sumSeries(" + prefix + StatsHelper.createBucket("view.products.*") + ")";
                break;
            case challengeView:
                target = "sumSeries(" + prefix + StatsHelper.createBucket("view.challenges.*") + ")";
                break;
        }

        String statsdUrl = StatsHelper.getStatsUrl(target, width, height, dateOption, null);
        try {
            logger.debug("Streaming stats from URL " + statsdUrl);
            InputStream inputStream = new URL(statsdUrl).openStream();
            return outputStream -> IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            throw new RequestException("Issue retrieving stats");
        }
    }

}
