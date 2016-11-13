package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.shared.entities.ServiceType;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
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
import java.util.List;

public class DatasetUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static private Logger logger = null;

    public DatasetUploadServlet() {
        logger = Logger.getLogger(DatasetUploadServlet.class);
        logger.info("Starting dataset upload servlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        try {
            String logUserName = UserUtils.verifyUserSupplier(request);
            // default values for width and height
            ServiceType serviceType = null;
            String resourceName = null;
            InputStream filecontent = null;
            String saveFile = null;
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            System.out.println("Items " + items.size());
            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString();
                    if(fieldName.equalsIgnoreCase("servicetype")) {
                        serviceType = ServiceType.valueOf(fieldValue);
                    }
                    if(fieldName.equalsIgnoreCase("resourcename")) {
                        resourceName = fieldValue;
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
                String resourcePath = processAndStoreResource(out, user, serviceType, resourceName);
                System.out.println(resourcePath);
                // return the url of the file
                writer.println("<value>" + resourcePath + "</value>");
            } finally {
                em.close();
            }
        } catch (FileUploadException e) {
            writeError(writer, "Could not read file");
        } catch (Exception e) {
            writeError(writer, "Error whilst processing and storing resource: " + e.getMessage());
        } finally {
            writer.flush();
            writer.close();
            System.out.println("done");
        }
    }

    protected void writeError(PrintWriter writer, String message) {
        writer.println("<html><body>" + "error=" + message + "</body></html>");
    }

    protected String processAndStoreResource(ByteArrayOutputStream out, User user, ServiceType serviceType, String resourceName) throws Exception {
        try {
            String filePath =  "./datasets/" + user.getCompany().getName() + "/" + resourceName;
            int maxFileSize = 10 * (1024 * 1024); //10 megs max
            if (out.size() > maxFileSize) {
                throw new Exception("File is > than " + maxFileSize);
            }
            // resize image and store file
            // TODO - check input and output format and limit output formats to be jpg or png
            String realPath = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.uploadPath) + filePath;
            logger.info("Process and store file at " + realPath);
            FileUtils.writeByteArrayToFile(new File(realPath), out.toByteArray());
            return filePath;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

}