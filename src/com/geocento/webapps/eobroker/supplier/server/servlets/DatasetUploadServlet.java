package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SampleUploadDTO;
import com.google.gson.Gson;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.zip.ZipFile;

public class DatasetUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static private Logger logger = null;

    private String RESTURL  = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverUri);
    private String RESTUSER = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverUser);
    private String RESTPW   = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverPassword);

    private GeoServerRESTReader reader;
    private GeoServerRESTPublisher publisher;

    public DatasetUploadServlet() {
        logger = Logger.getLogger(DatasetUploadServlet.class);
        logger.info("Starting dataset upload servlet");

        try {
            reader = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        try {
            String logUserName = UserUtils.verifyUserSupplier(request);
            // resource id for storing
            Long resourceId = null;
            InputStream filecontent = null;
            String saveFile = null;
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            System.out.println("Items " + items.size());
            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString();
                    if(fieldName.equalsIgnoreCase("resourceId")) {
                        resourceId = Long.parseLong(fieldValue);
                    }
                } else {
                    // Process form file field (input type="file").
                    String fieldName = item.getFieldName();
                    System.out.println("field name " + fieldName);
                    saveFile = FilenameUtils.getName(item.getName());
                    System.out.println("file name " + saveFile);
                    filecontent = item.getInputStream();
                }
            }
            System.out.println("Processing " + saveFile);
            // check values
            if(filecontent == null) {
                throw new FileNotFoundException("no file provided");
            }
            if(resourceId == null) {
                throw new Exception("Resource id missing");
            }
            System.out.println("Reading file content");
            // Process the input stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[8192];
            while ((len = filecontent.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }

            EntityManager em = EMF.get().createEntityManager();
            try {
                final User user = em.find(User.class, logUserName);
                System.out.println("Processing resource");
                SampleUploadDTO sampleUploadDTO = processAndStoreResource(out, user, resourceId, saveFile);
                response.setStatus(200);
                response.setContentType("text/html");
                PrintWriter writer = response.getWriter();
                writer.print("<html><body><value>" + new Gson().toJson(sampleUploadDTO) + "</value></body></html>");
                writer.flush();
                writer.close();
            } finally {
                em.close();
            }
        } catch (FileUploadException e) {
            writeError(response, "Could not read file");
        } catch (Exception e) {
            writeError(response, "Error whilst processing and storing resource: " + e.getMessage());
        } finally {
            System.out.println("done");
        }
    }

    protected void writeError(HttpServletResponse response, String message) throws IOException {
        response.sendError(500, message);
    }

    protected SampleUploadDTO processAndStoreResource(ByteArrayOutputStream out, User user, Long resourceId, String resourceName) throws Exception {
        try {
            // save file to disk first
            Long companyId = user.getCompany().getId();
            String fileDirectory = "./datasets/" + companyId + "/" + resourceId + "/";
            String filePath =  fileDirectory + resourceName;
            int maxFileSize = 10 * (1024 * 1024); //10 megs max
            if (out.size() > maxFileSize) {
                throw new Exception("File is > than " + maxFileSize);
            }
            // store file
            // TODO - check input and output format and limit output formats to be jpg or png
            String diskDirectory = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.uploadPath) + fileDirectory;
            String diskPath = diskDirectory + resourceName;
            logger.info("Process and store file at " + diskPath);
            // force creation of directory if it does not exist
            FileUtils.forceMkdir(new File(diskDirectory));
            File file = new File(diskPath);
            FileUtils.writeByteArrayToFile(file, out.toByteArray());
            // now check extension and publish to geoserver if it is a geospatial file
            String workspaceName = companyId + "_" + resourceId;
            String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1).toLowerCase();
            String storeName = resourceName.substring(resourceName.lastIndexOf("/") + 1).substring(0, resourceName.lastIndexOf("."));
            String layerName = null;
            switch (extension) {
/*
                case "kml":
*/
                case "zip":
                    layerName = publishShapefile(workspaceName, storeName, file);
                    break;
                case "tiff":
                case "tif":
                    layerName = publishGeoTiff(workspaceName, storeName, file);
                    break;
                default:
                    layerName = null;
            }
            // create response
            SampleUploadDTO sampleUploadDTO = new SampleUploadDTO();
            sampleUploadDTO.setFileUri(filePath);
            sampleUploadDTO.setLayerName(layerName);
            return sampleUploadDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private String publishShapefile(String workspaceName, String storeName, File file) throws Exception {
        try {
            if (!existsWorkpspace(workspaceName)) {
                boolean created = publisher.createWorkspace(workspaceName);
                if(!created) {
                    throw new Exception("Could not create workspace " + workspaceName);
                }
            }
            String resourceName = null;
            ZipFile zipFile = new ZipFile(file.getPath());
            resourceName = zipFile.entries().nextElement().getName();
            resourceName = resourceName.substring(0, resourceName.lastIndexOf("."));
            // check layer does not already exist
            boolean published = publisher.publishShp(workspaceName, storeName, resourceName, file);
            if (published) {
                return resourceName;
            } else {
                throw new Exception("Could not publish layer");
            }
        } catch (Exception e) {
            throw new Exception("Could not publish layer, reason is " + e.getMessage());
        }
    }

    private String publishGeoTiff(String workspaceName, String storeName, File file) throws Exception {
        try {
            if(!existsWorkpspace(workspaceName)) {
                boolean created = publisher.createWorkspace(workspaceName);
                if(!created) {
                    throw new Exception("Could not create workspace " + workspaceName);
                }
            }
            boolean pc = publisher.publishGeoTIFF(workspaceName, storeName, file);
            if (pc) {
                return storeName;
            } else {
                throw new Exception("Could not publish layer");
            }
        } catch (Exception e) {
            throw new Exception("Could not publish layer, reason is " + e.getMessage());
        }
    }

    private boolean existsWorkpspace(String workspaceName) {
        return reader.getWorkspaceNames().contains(workspaceName);
    }

}