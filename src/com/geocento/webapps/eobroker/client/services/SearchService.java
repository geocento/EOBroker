package com.geocento.webapps.eobroker.client.services;

import com.geocento.webapps.eobroker.shared.Suggestion;
import com.geocento.webapps.eobroker.shared.entities.Category;
import com.geocento.webapps.eobroker.shared.entities.SearchResult;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

public interface SearchService extends DirectRestService {

    @GET
    @Path("/complete")
    @Produces("application/json")
    public List<Suggestion> complete(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoi") String aoi);

    @GET
    @Path("/search")
    @Produces("application/json")
    public SearchResult getMatchingServices(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoiId") Long aoiId) throws RequestException;

}
