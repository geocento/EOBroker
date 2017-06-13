package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessFile;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SampleUploadDTO;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
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
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DatasetUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static private Logger logger = null;

/*
    private GeoServerRESTReader reader;
    private GeoServerRESTPublisher publisher;
*/

    public DatasetUploadServlet() {
        logger = Logger.getLogger(DatasetUploadServlet.class);
        logger.info("Starting dataset upload servlet");

/*
        try {
            reader = GeoserverUtils.getGeoserverReader();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        publisher = GeoserverUtils.getGeoserverPublisher();
*/
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
            String parameterValue = request.getParameter("resourceId");
            if(parameterValue != null) {
                try {
                    resourceId = Long.parseLong(parameterValue);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
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
                DatasetAccess dataAccess = processAndStoreResource(out, user, resourceId, saveFile);
                response.setStatus(200);
                response.setContentType("text/html");
                PrintWriter writer = response.getWriter();
                ObjectMapper objectMapper = new ObjectMapper();
                writer.print("<html><body><value>" + objectMapper.writeValueAsString(dataAccess) + "</value></body></html>");
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
        response.setStatus(200);
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.print("<html><body><error>" + message + "</error></body></html>");
        writer.flush();
        writer.close();
    }

    protected DatasetAccess processAndStoreResource(ByteArrayOutputStream out, User user, Long resourceId, String resourceName) throws Exception {
        try {
            // save file to disk first
            Long companyId = user.getCompany().getId();
            String fileDirectory = "./datasets/" + companyId + "/" + resourceId + "/";
            String filePath =  fileDirectory + resourceName;
            int maxFileSize = ServerUtil.getSettings().getMaxSampleSizeMB() * (1024 * 1024);
            if (out.size() > maxFileSize) {
                throw new Exception("File is > than " + maxFileSize);
            }
            // store file
            // TODO - check input and output format and limit output formats to be jpg or png
            String diskDirectory = ServerUtil.getSettings().getDataDirectory() + fileDirectory;
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
            // create response
            DatasetAccess datasetAccess = null;
            SampleUploadDTO sampleUploadDTO = new SampleUploadDTO();
            switch (extension) {
                case "kml":
                    // TODO - get KML file and convert to shapefile
                    // issue is with the styling which needs to be saved as well
                    break;
                case "zip": {
                    layerName = publishShapefile(workspaceName, storeName, file);
                    DatasetAccessOGC datasetAccessOGC = new DatasetAccessOGC();
                    datasetAccessOGC.setUri(layerName);
                    datasetAccessOGC.setServerUrl(ServerUtil.getSettings().getGeoserverOWS());
                    datasetAccessOGC.setCorsEnabled(true);
                    datasetAccessOGC.setStyleName("geometry");
                    datasetAccess = datasetAccessOGC;
                } break;
                case "tiff":
                case "tif": {
                    layerName = publishGeoTiff(workspaceName, storeName, file);
                    DatasetAccessOGC datasetAccessOGC = new DatasetAccessOGC();
                    datasetAccessOGC.setUri(layerName);
                    datasetAccessOGC.setServerUrl(ServerUtil.getSettings().getGeoserverOWS());
                    datasetAccessOGC.setCorsEnabled(true);
                    datasetAccessOGC.setStyleName("raster");
                    // WCS is automatically published
                    datasetAccessOGC.setWcsServerUrl(ServerUtil.getSettings().getGeoserverOWS());
                    datasetAccessOGC.setWcsResourceName(layerName);
                    datasetAccess = datasetAccessOGC;
                } break;
                case "pdf":
                case "csv":
                case "ppt":
                case "doc": {
                    DatasetAccessFile datasetAccessFile = new DatasetAccessFile();
                    datasetAccessFile.setUri(filePath);
                    // TODO - add file size
                    //datasetAccessFile.setSize(file.getTotalSpace())
                    datasetAccess = datasetAccessFile;
                } break;
                default:
                    throw new Exception("File format '" + extension + "' not supported");
            }
            datasetAccess.setHostedData(false);
            return datasetAccess;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private String publishShapefile(String workspaceName, String storeName, File file) throws Exception {
        ZipFile zipFile = new ZipFile(file.getPath());
        int folders = 0;
        int shpFiles = 0;
        // inspect zip file to see if it contains folders
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if(entry.isDirectory()) {
                folders++;
            } else if(entry.getName().endsWith("shp")) {
                shpFiles++;
            }
        }
        String layerName = null;
        if(folders > 0 || shpFiles > 1) {
            List<String> layerNames = new ArrayList<String>();
            // we need to rewrite the file as geoserver doesn't support directories or multiple shapefiles
            String directoryPath = file.getParent() + "/" + new Date().getTime();
            File tmpPath = new File(directoryPath);
            if(!tmpPath.mkdirs()) {
                throw new Exception("Could not create shapefile directory");
            }
            List<File> files = extractShapeFiles(tmpPath, file);
            // for now take the first directory
            for(File shpFile : files) {
                String fileName = shpFile.getName();
                layerNames.add(publishShapefile(workspaceName, storeName, fileName.substring(0, fileName.lastIndexOf(".")), shpFile));
            }
            layerName = StringUtils.join(layerNames, ",");
            FileUtils.deleteDirectory(tmpPath);
        } else {
            String resourceName = zipFile.entries().nextElement().getName();
            resourceName = resourceName.substring(0, resourceName.lastIndexOf("."));
            layerName = publishShapefile(workspaceName, storeName, resourceName, file);
        }
        return layerName;
    }

    private List<File> extractShapeFiles(File tmpPath, File zipFile) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry = zipInputStream.getNextEntry();
        // create directory to store the shapefiles
        try {
            // the standard shapefile with the shp extension
            ArrayList<File> shapeFiles = new ArrayList<File>();
            HashMap<String, List<File>> listShapeFiles = new HashMap<String, List<File>>();
            while(entry != null) {
                String name = entry.getName();
                logger.debug("Reading: " + name);

                if(name.endsWith("/"))
                    name += " ";

                String[] zipInnerPath = name.split("/");
                String zipPath = "";
                for(int i = 0; i <  zipInnerPath.length; i++)
                {
                    if(zipInnerPath[i].trim().length()==0)
                        break;

                    zipPath += zipInnerPath[i] + "/";
                    File file = new File(tmpPath, zipPath);
                    //isDirectory() method returns true if directory exists and it is a directory
                    //in this case it is not created yet so always will return false, so we check
                    //if it is the last item in path (file) or not (directory)
                    if (i < (zipInnerPath.length - 1)/*entry.isDirectory()*/) {
                        if(!file.exists())
                            file.mkdir();
                    } else {
/*
                        FileUtils.copyInputStreamToFile(zipInputStream, file);
*/
                        int count;
                        byte data[] = new byte[4096];
                        // write the files to the disk
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            while ((count = zipInputStream.read(data)) != -1) {
                                fos.write(data, 0, count);
                            }
                            fos.flush();
                        } finally {
                            if (fos != null) {
                                fos.close();
                            }
                        }
                        // add to zipped collection of files
                        String shapeFileName = file.getName();
                        shapeFileName = shapeFileName.substring(0, shapeFileName.lastIndexOf("."));
                        List<File> listFiles = listShapeFiles.get(shapeFileName);
                        if(listFiles == null) {
                            listFiles = new ArrayList<File>();
                            listShapeFiles.put(shapeFileName, listFiles);
                        }
                        listFiles.add(file);
                    }
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
            // now we have a list of files to zip
            for(String fileName : listShapeFiles.keySet()) {
                String resourceName = makeValid(fileName) + "_" + new Date().getTime();
                List<File> files = listShapeFiles.get(fileName);
                // look for the shp file
                boolean hasShp = false;
                for(File file : files) {
                    if(file.getName().endsWith("shp")) {
                        hasShp = true;
                    }
                }
                if(hasShp) {
                    // create a zip input file
                    String path = files.get(0).getParent();
                    File zipFileExtracted = new File(path + "/" + resourceName + ".zip");
                    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFileExtracted));
                    for(File file : files) {
                        String shpFileName = file.getName();
                        ZipEntry zipEntry = new ZipEntry(resourceName + shpFileName.substring(shpFileName.lastIndexOf(".")));
                        zipOutputStream.putNextEntry(zipEntry);
                        FileUtils.copyFile(file, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                    zipOutputStream.close();
                    // add to the list of shapefiles
                    shapeFiles.add(zipFileExtracted);
                }
            }
            return shapeFiles;
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        }
    }

    private String publishShapefile(String workspaceName, String storeName, String resourceName, File file) throws Exception {
        try {
            GeoServerRESTPublisher publisher = GeoserverUtils.getGeoserverPublisher();
            if (!existsWorkpspace(workspaceName)) {
                boolean created = publisher.createWorkspace(workspaceName);
                if(!created) {
                    throw new Exception("Could not create workspace " + workspaceName);
                }
            }
            // make sure we have valid values for storeName and resourceName
            storeName = makeValid(storeName);
            resourceName = makeValid(resourceName);
            boolean published = publisher.publishShp(workspaceName, storeName, resourceName, file);
            if (published) {
                return workspaceName + ":" + resourceName;
            } else {
                throw new Exception("Could not publish layer");
            }
        } catch (Exception e) {
            throw new Exception("Could not publish layer, reason is " + e.getMessage());
        }
    }

    private String makeValid(String resourceName) throws UnsupportedEncodingException {
        return URLEncoder.encode(resourceName.replaceAll(" ", "_"), "UTF-8");
    }

    private String publishGeoTiff(String workspaceName, String storeName, File file) throws Exception {
        try {
            GeoServerRESTPublisher publisher = GeoserverUtils.getGeoserverPublisher();
            if(!existsWorkpspace(workspaceName)) {
                boolean created = publisher.createWorkspace(workspaceName);
                if(!created) {
                    throw new Exception("Could not create workspace " + workspaceName);
                }
            }
            boolean pc = publisher.publishGeoTIFF(workspaceName, storeName, file);
            if (pc) {
                return workspaceName + ":" + storeName;
            } else {
                throw new Exception("Could not publish layer");
            }
        } catch (Exception e) {
            throw new Exception("Could not publish layer, reason is " + e.getMessage());
        }
    }

    private boolean existsWorkpspace(String workspaceName) throws MalformedURLException {
        return GeoserverUtils.getGeoserverReader().getWorkspaceNames().contains(workspaceName);
    }

}