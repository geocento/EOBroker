package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.server.Utils.GISUtils;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessFile;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.supplier.server.util.DatasetAccessUtils;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DatasetUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static private Logger logger = null;

    static private KeyGenerator keyGenerator = new KeyGenerator(16);

    public DatasetUploadServlet() {
        logger = Logger.getLogger(DatasetUploadServlet.class);
        logger.info("Starting dataset upload servlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        try {
            String logUserName = UserUtils.verifyUserSupplier(request);
            // resource id for storing
            Long resourceId = null;
            boolean publish = false;
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
                    if(fieldName.equalsIgnoreCase("publish")) {
                        publish = Boolean.parseBoolean(fieldValue);
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
            parameterValue = request.getParameter("publish");
            if(parameterValue != null) {
                try {
                    publish = Boolean.parseBoolean(parameterValue);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            System.out.println("Reading file content");

            EntityManager em = EMF.get().createEntityManager();
            try {
                final User user = em.find(User.class, logUserName);
                System.out.println("Processing resource");
                DatasetAccess dataAccess = processAndStoreResource(filecontent, user, resourceId, saveFile, publish);
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

    protected DatasetAccess processAndStoreResource(InputStream filecontent, User user, Long resourceId, String resourceName, boolean publish) throws Exception {
        try {
            int maxFileSize = ServerUtil.getSettings().getMaxSampleSizeMB() * (1024 * 1024);
            // Process the input stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[8192];
            while ((len = filecontent.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
                if (out.size() > maxFileSize) {
                    throw new Exception("File is > than " + maxFileSize);
                }
            }
            // save file to disk first
            Long companyId = user.getCompany().getId();
            String fileDirectory = "./datasets/" + companyId + "/" + resourceId + "/";
            String filePath =  fileDirectory + resourceName;
            // store file
            // TODO - check input and output format and limit output formats to be jpg or png
            String diskDirectory = ServerUtil.getDataFilePath(fileDirectory);
            String diskPath = new File(diskDirectory, resourceName).getAbsolutePath();
            logger.info("Process and store file at " + diskPath);
            // force creation of directory if it does not exist
            FileUtils.forceMkdir(new File(diskDirectory));
            File file = new File(diskPath);
            FileUtils.writeByteArrayToFile(file, out.toByteArray());
            out.close();
            // now check extension and publish to geoserver if it is a geospatial file
            String workspaceName = companyId + "_" + resourceId;
            String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1).toLowerCase();
            // create response
            DatasetAccess datasetAccess = null;
            datasetAccess.setUri(filePath);
            datasetAccess.setSize((int) file.length());
            datasetAccess.setHostedData(false);
            if(publish) {
                switch (extension) {
                    case "zip": {
                        datasetAccess = publishShapefile(workspaceName, file);
                    } break;
                    case "tiff":
                    case "tif": {
                        datasetAccess = publishGeoTiff(workspaceName, file);
                    } break;
                    default:
                        throw new Exception("File format '" + extension + "' not supported for publishing");
                }
            } else {
                switch (extension) {
                    case "zip":
                    case "gz":
                    case "tar":
                    case "gif":
                    case "jpg":
                    case "jpeg":
                    case "tiff":
                    case "tif":
                    case "png":
                    case "kml":
                    case "pdf":
                    case "csv":
                    case "ppt":
                    case "doc": {
                        DatasetAccessFile datasetAccessFile = new DatasetAccessFile();
                        datasetAccess = datasetAccessFile;
                    } break;
                    default:
                        throw new Exception("File format '" + extension + "' not supported");
                }
                // generate thumbnail when everything is configured
                try {
                    datasetAccess.setImageUrl(DatasetAccessUtils.generateThumbnail(datasetAccess));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            return datasetAccess;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    static public DatasetAccessOGC publishShapefile(String workspaceName, File file) throws Exception {
        String layerName = null;
        List<String> layerNames = new ArrayList<String>();
        // we need to rewrite the file as geoserver doesn't support directories or multiple shapefiles
        String directoryPath = file.getParent() + "/" + new Date().getTime();
        File tmpPath = new File(directoryPath);
        if(!tmpPath.mkdirs()) {
            throw new Exception("Could not create shapefile directory");
        }
        // extract shapefiles to directory
        HashMap<String, List<File>> listShapeFiles = extractShapeFiles(tmpPath, file);
        // now we have a list of files to analyse and zip
        for(String fileName : listShapeFiles.keySet()) {
            List<File> files = listShapeFiles.get(fileName);
            // look for the shp file
            File shapeFile = ListUtil.findValue(files, value -> value.getName().endsWith("shp"));
            boolean hasShp = shapeFile != null;
            if(hasShp) {
                String path = files.get(0).getParent();
                // get CRS as the geoserver doesn't handle CRS automatically...
                ShapefileDataStore store = new ShapefileDataStore(DataUtilities.fileToURL(shapeFile));
                SimpleFeatureType storeSchema = store.getFeatureSource().getSchema();
                if(storeSchema != null) {
                    CoordinateReferenceSystem crs = storeSchema.getCoordinateReferenceSystem();
                    try {
                        CoordinateReferenceSystem geo = CRS.decode("EPSG:4326", false);
                        if(!crs.equals(geo)) {
                            // reproject shape files into a temporary directory
                            path += "/tmp";
                            File tmpDir = new File(path);
                            tmpDir.mkdir();
                            GISUtils.reprojectShapefile(store, geo, new File(path, shapeFile.getName()));
                            // update files
                            files.clear();
                            files.addAll(FileUtils.listFiles(tmpDir, FileFilterUtils.trueFileFilter(), FileFilterUtils.falseFileFilter()));
                                    //new String[]{"shp", ""}, false));
                        }
                    } catch (FactoryException e) {
                        throw new Exception("Server conversion error");
                    }
                }
                // we need to make sure zip file name, file name and layer name are the same
                String resourceName = shapeFile.getName().replace(".shp", "");
                resourceName = makeValid(resourceName);
                // create a zip input file
                File zipShapeFile = new File(path + "/" + resourceName + ".zip");
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipShapeFile));
                for(File fileToZip : files) {
                    String shpFileName = fileToZip.getName();
                    ZipEntry zipEntry = new ZipEntry(resourceName + shpFileName.substring(shpFileName.lastIndexOf(".")));
                    zipOutputStream.putNextEntry(zipEntry);
                    FileUtils.copyFile(fileToZip, zipOutputStream);
                    zipOutputStream.closeEntry();
                }
                zipOutputStream.close();
                // now publish the shapefile
                try {
                    GeoServerRESTPublisher publisher = GeoserverUtils.getGeoserverPublisher();
                    if (!GeoserverUtils.existsWorkpspace(workspaceName)) {
                        boolean created = publisher.createWorkspace(workspaceName);
                        if(!created) {
                            throw new Exception("Could not create workspace " + workspaceName);
                        }
                    }
                    boolean published = publisher.publishShp(workspaceName, resourceName, resourceName, zipShapeFile, "EPSG:4326");
                    if (published) {
                        layerNames.add(workspaceName + ":" + resourceName);
                    } else {
                        throw new Exception("Could not publish layer");
                    }
                } catch (Exception e) {
                    throw new Exception("Could not publish layer, reason is " + e.getMessage());
                }
            }
        }
        if(layerNames.size() == 0) {
            throw new Exception("No valid shape file found");
        }
        layerName = StringUtils.join(layerNames, ",");
        FileUtils.deleteDirectory(tmpPath);
        DatasetAccessOGC datasetAccessOGC = new DatasetAccessOGC();
        datasetAccessOGC.setLayerName(layerName);
        datasetAccessOGC.setServerUrl(ServerUtil.getSettings().getGeoserverOWS());
        datasetAccessOGC.setCorsEnabled(true);
        datasetAccessOGC.setStyleName("geometry");
        // generate thumbnail
        try {
            datasetAccessOGC.setImageUrl(DatasetAccessUtils.generateThumbnail(datasetAccessOGC));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return datasetAccessOGC;
    }

    public static HashMap<String, List<File>> extractShapeFiles(File tmpPath, File zipFile) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry = zipInputStream.getNextEntry();
        // create directory to store the shapefiles
        try {
            // the standard shapefile with the shp extension
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
            return listShapeFiles;
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        }
    }

    static private String makeValid(String resourceName) throws UnsupportedEncodingException {
        return URLEncoder.encode(resourceName.replaceAll(" ", "_"), "UTF-8");
    }

    private DatasetAccessOGC publishGeoTiff(String workspaceName, File file) throws Exception {
        try {
            GeoServerRESTPublisher publisher = GeoserverUtils.getGeoserverPublisher();
            if(!GeoserverUtils.existsWorkpspace(workspaceName)) {
                boolean created = publisher.createWorkspace(workspaceName);
                if(!created) {
                    throw new Exception("Could not create workspace " + workspaceName);
                }
            }
            String resourceName = keyGenerator.CreateKey();
            boolean pc = publisher.publishGeoTIFF(workspaceName, resourceName, file);
            if (pc) {
                String layerName = workspaceName + ":" + resourceName;
                DatasetAccessOGC datasetAccessOGC = new DatasetAccessOGC();
                datasetAccessOGC.setLayerName(layerName);
                datasetAccessOGC.setServerUrl(ServerUtil.getSettings().getGeoserverOWS());
                datasetAccessOGC.setCorsEnabled(true);
                datasetAccessOGC.setStyleName("raster");
                // WCS is automatically published
                datasetAccessOGC.setWcsServerUrl(ServerUtil.getSettings().getGeoserverOWS());
                datasetAccessOGC.setWcsResourceName(layerName.replace(":", "__"));
                // generate thumbnail
                try {
                    datasetAccessOGC.setImageUrl(DatasetAccessUtils.generateThumbnail(datasetAccessOGC));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return datasetAccessOGC;
            } else {
                throw new Exception("Could not publish layer");
            }
        } catch (Exception e) {
            throw new Exception("Could not publish layer, reason is " + e.getMessage());
        }
    }

}