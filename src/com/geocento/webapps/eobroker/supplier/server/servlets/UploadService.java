package com.geocento.webapps.eobroker.supplier.server.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/upload")
public class UploadService {

    // assumes service is not a singleton
    @Context
    HttpServletRequest request;

/*
    @POST
    @Path("/style")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        try {
            String logUserName = UserUtils.verifyUserSupplier(request);
            EntityManager em = EMF.get().createEntityManager();
            User user = em.find(User.class, logUserName);
            // define the style
            String workspace = user.getCompany().getId() + "";
            String styleName = fileDetail.getName();
            String sldBody = IOUtils.toString(uploadedInputStream);
            // publish to GeoServer
            String RESTURL  = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverRESTUri);
            String RESTUSER = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverUser);
            String RESTPW   = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverPassword);
            GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
            publisher.publishStyleInWorkspace(workspace, sldBody, styleName);

            String output = styleName;
            return Response.status(200).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }
*/
}
