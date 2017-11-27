package com.geocento.webapps.eobroker.customer.server.servlets;


import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.customer.server.utils.StatsHelper;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;


@Path("download-data")
public class DataDownloadResource {

    @Context
    ServletContext servletContext;

    @Context
    HttpServletResponse response;

    @Context
    SecurityContext securityContext;

    // create the logger
    static Logger logger = Logger.getLogger(DataDownloadResource.class);

    public DataDownloadResource()
    {
        logger.info("Starting the product downloader api service");
    }

    /**
     *
     * Downloads the product requested by data access id if exists and available
     *
     * @return The product file
     */
    @GET
    @Path("/download/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput retrieveProductForDownload(@PathParam("id") String dataAccessId) throws Exception
    {
        EntityManager em = EMF.get().createEntityManager();

        try {
            DatasetAccess datasetAccess = em.find(DatasetAccess.class, Long.parseLong(dataAccessId));
            if(datasetAccess == null || datasetAccess.isHostedData()) {
                throw new WebApplicationException("File requested is not available", Response.Status.BAD_REQUEST);
            }

            StatsHelper.addCounter("download.samples", dataAccessId + "");
            // get product
            String productPath = ServerUtil.getDataFilePath(datasetAccess.getUri());
            logger.debug("Download file is at " + productPath);

            File fileToDownload = new File(productPath);
/*
            File fileToDownload = new File(new File(ServerUtil.getSettings().getDataFilePath()), productPath);
*/
            if(!fileToDownload.exists()) {
                throw new WebApplicationException("File requested is not available", Response.Status.BAD_REQUEST);
            }

            // set the content disposition
            response.setHeader("Content-Disposition", "attachment; filename=\""+ fileToDownload.getName() + "\"");
            // start the downloading
            return new StreamingOutput() {
                public void write(OutputStream output)  {

                    logger.debug("Starting the download...");
                    FileInputStream in = null;
                    try
                    {
                        in = new FileInputStream(fileToDownload);

                        byte[] buffer = new byte[4096];
                        int length;
                        while ((length = in.read(buffer)) > 0){
                            output.write(buffer, 0, length);
                        }
                        in.close();
                        output.flush();

                        logger.debug("Finished the download, now updating stats for stats " + dataAccessId);
                        // TODO - update stats
                    }
                    catch (FileNotFoundException e) {
                        logger.error(e.getMessage(), e);
                        throw new WebApplicationException("Product is available for download but the file had not been found", Response.Status.BAD_REQUEST);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        throw new WebApplicationException("Error downloading product file", Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }
            };
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e instanceof WebApplicationException ? e : new WebApplicationException("Error downloading product file", Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            em.close();
        }
    }

}
