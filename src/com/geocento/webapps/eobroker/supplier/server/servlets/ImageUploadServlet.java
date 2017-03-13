package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;

public class ImageUploadServlet extends HttpServlet {

    static private Logger logger = null;

    public ImageUploadServlet() {
        logger = Logger.getLogger(ImageUploadServlet.class);
        logger.info("Starting image upload servlet");
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		try {
            String userName = UserUtils.verifyUser(request);
	        System.out.println("Start");
			// default values for width and height
			int width = 50;
			int height = 50;
			InputStream filecontent = null;
			String saveFile = null;
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
	        System.out.println("Items " + items.size());
		    for (FileItem item : items) {
		        if (item.isFormField()) {
		            // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
		            String fieldName = item.getFieldName();
		            String fieldValue = item.getString();
		            if(fieldName.equalsIgnoreCase("width")) {
		            	width = Integer.parseInt(fieldValue);
		            }
		            if(fieldName.equalsIgnoreCase("height")) {
		            	height = Integer.parseInt(fieldValue);
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

	        System.out.println("Processing image");
            String imagePath = "./uploaded/" + processAndStoreImage(out, saveFile, width, height);
            System.out.println(imagePath);
            
		    // return the url of the file
			writer.println("<value>" + imagePath + "</value>");
		} catch (FileUploadException e) {
			writeError(writer, "Could not read file");
	    } catch(IOException e) {
	    	writeError(writer, "Image type not supported!");
		} catch (Exception e) {
			writeError(writer, "Error whilst processing and storing image: " + e.getMessage());
		} finally {
			writer.flush();
			writer.close();
	        System.out.println("done");
		}
    }
	
	protected void writeError(PrintWriter writer, String message) {
		writer.println("<html><body>" + "error=" + message + "</body></html>");
	}

    protected String processAndStoreImage(ByteArrayOutputStream out, String imageName, int width, int height) throws Exception {
        try {
            String filePath =  "./img/" + "image" + new Date().getTime() + "_" + imageName;
            int maxFileSize = 10 * (1024 * 1024); //10 megs max
            if (out.size() > maxFileSize) {
                throw new Exception("File is > than " + maxFileSize);
            }
            // resize image and store file
            // TODO - check input and output format and limit output formats to be jpg or png
            String realPath = ServerUtil.getSettings().getDataDirectory() + filePath;
            logger.info("Process and store file at " + realPath);
            if(width > 0 && height > 0) {
                Thumbnails.of(new ByteArrayInputStream(out.toByteArray())).forceSize(width, height).toFile(realPath);
            } else {
                // do not resize
                Thumbnails.of(new ByteArrayInputStream(out.toByteArray())).scale(1.0).toFile(realPath);
            }
            return filePath;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

}
