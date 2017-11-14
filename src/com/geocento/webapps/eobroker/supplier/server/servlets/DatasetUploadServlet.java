package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.server.Utils.WMSCapabilities;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.google.gwt.http.client.RequestException;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
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
                // generate thumbnail
                try {
                    File imageFile = null;
                    File tempFile = null;
                    switch (extension) {
                        case "gif":
                        case "jpg":
                        case "jpeg":
                        case "png": {
                            imageFile = file;
                        } break;
                        case "pdf": {
                            PDDocument doc = PDDocument.load(file);
                            try {
                                PDFRenderer renderer = new PDFRenderer(doc);
                                BufferedImage bufferedImage = renderer.renderImage(0);
                                tempFile = new File(ServerUtil.getSettings().getDataDirectory() + "./tmp", file.getName() + ".jpg");
                                if(!new File(tempFile.getParent()).exists()) {
                                    new File(tempFile.getParent()).mkdir();
                                }
                                ImageIOUtil.writeImage(bufferedImage, tempFile.getAbsolutePath(), 100);
                                imageFile = tempFile;
                            } finally {
                                doc.close();
                            }
                        } break;
                        default:
                            break;
                    }
                    if(imageFile != null) {
                        String imagePath = keyGenerator.CreateKey() + ".jpg";
                        File thumbnailFile = new File(ServerUtil.getSettings().getDataDirectory() + "./img", imagePath);
                        Thumbnails.of(imageFile).width(200).height(200).keepAspectRatio(true).toFile(thumbnailFile);
                        datasetAccess.setImageUrl("./uploaded/img/" + imagePath);
                    }
                    if(tempFile != null) {
                        tempFile.delete();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            datasetAccess.setUri(filePath);
            datasetAccess.setSize((int) file.length());
            datasetAccess.setHostedData(false);
            return datasetAccess;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private DatasetAccessOGC publishShapefile(String workspaceName, File file) throws Exception {
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
            boolean hasShp = false;
            String resourceName = null;
            String projection = "EPSG:4326";
            for(File shapeFile : files) {
                if(shapeFile.getName().endsWith("shp")) {
                    hasShp = true;
                    resourceName = shapeFile.getName().replace(".shp", "");
                    // get CRS as the geoserver doesn't handle CRS automatically...
                    ShapefileDataStore store = new ShapefileDataStore(DataUtilities.fileToURL(shapeFile));
                    SimpleFeatureType storeSchema = store.getFeatureSource().getSchema();
                    if(storeSchema != null) {
                        CoordinateReferenceSystem crs = storeSchema.getCoordinateReferenceSystem();
                        if (crs != null) {
                            Integer epsgId = CRS.lookupEpsgCode(crs, false);
                            if(epsgId == null) {
                                // try analysing the actual srs returned
                                String srs = CRS.toSRS(crs);
                                //if(srs.startsWith())
                                if(srs == null) {
                                    throw new Exception("Shapefile's projection is not a valid projection");
                                }
                                projection = "EPSG:4326";
                            } else {
                                projection = "EPSG:" + epsgId;
                            }
                        }
                    }
                }
            }
            if(hasShp) {
                // we need to make sure zip file name, file name and layer name are the same
                resourceName = makeValid(resourceName);
                // create a zip input file
                String path = files.get(0).getParent();
                File zipShapeFile = new File(path + "/" + resourceName + ".zip");
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipShapeFile));
                for(File shapeFile : files) {
                    String shpFileName = shapeFile.getName();
                    ZipEntry zipEntry = new ZipEntry(resourceName + shpFileName.substring(shpFileName.lastIndexOf(".")));
                    zipOutputStream.putNextEntry(zipEntry);
                    FileUtils.copyFile(shapeFile, zipOutputStream);
                    zipOutputStream.closeEntry();
                }
                zipOutputStream.close();
                // now publish the shapefile
                try {
                    GeoServerRESTPublisher publisher = GeoserverUtils.getGeoserverPublisher();
                    if (!existsWorkpspace(workspaceName)) {
                        boolean created = publisher.createWorkspace(workspaceName);
                        if(!created) {
                            throw new Exception("Could not create workspace " + workspaceName);
                        }
                    }
                    boolean published = projection == null ? publisher.publishShp(workspaceName, resourceName, resourceName, zipShapeFile) :
                            publisher.publishShp(workspaceName, resourceName, resourceName, zipShapeFile, projection);
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
            datasetAccessOGC.setImageUrl(generateThumbnail(datasetAccessOGC, keyGenerator.CreateKey()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return datasetAccessOGC;
    }

    private String getGeoserverServerUrl(String workspaceName) {
        String geoserverUrl = ServerUtil.getSettings().getGeoserverOWS();
        // add the workspace in the path
        return geoserverUrl.replace("$workspace", workspaceName);
    }

    private HashMap<String, List<File>> extractShapeFiles(File tmpPath, File zipFile) throws Exception {
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

    private String makeValid(String resourceName) throws UnsupportedEncodingException {
        return URLEncoder.encode(resourceName.replaceAll(" ", "_"), "UTF-8");
    }

    private DatasetAccessOGC publishGeoTiff(String workspaceName, File file) throws Exception {
        try {
            GeoServerRESTPublisher publisher = GeoserverUtils.getGeoserverPublisher();
            if(!existsWorkpspace(workspaceName)) {
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
                    datasetAccessOGC.setImageUrl(generateThumbnail(datasetAccessOGC, resourceName));
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

    private String generateThumbnail(DatasetAccessOGC datasetAccessOGC, String fileName) throws Exception {
        // TODO - move to helper class
        String serverUrl = datasetAccessOGC.getServerUrl();
        // manage several layers
        Set<String> layerNames = new HashSet<String>();
        String layerName = datasetAccessOGC.getLayerName();
        // overwrite if it is the internal eobroker server
        // this is to optimise the call
        if(!datasetAccessOGC.isHostedData() && layerName.contains(":")) {
            int index = serverUrl.lastIndexOf("/");
            String workspace = layerName.substring(0, layerName.indexOf(":"));
            for(String layerNameValue : layerName.split(",")) {
                layerNames.add(layerNameValue.replace(workspace + ":", ""));
            }
            // force to 1.1.0 as there are problems with 1.3.0 and multiple layers
            serverUrl = serverUrl.substring(0, index) + "/" + workspace + serverUrl.substring(index) + "&version=1.1.1";
        } else {
            for(String layerNameValue : layerName.split(",")) {
                layerNames.add(layerNameValue);
            }
        }
        List<WMSCapabilities.WMSLayer> layers = new ArrayList<WMSCapabilities.WMSLayer>();
        // make WMS query
        WMSCapabilities wmsCapabilities = new WMSCapabilities();
        wmsCapabilities.extractWMSXMLResources(serverUrl + "&service=WMS&request=getCapabilities");
        for(WMSCapabilities.WMSLayer wmsLayer : wmsCapabilities.getLayersList()) {
            if(layerNames.contains(wmsLayer.getLayerName())) {
                layers.add(wmsLayer);
            }
        }
        if(layers.size() == 0) {
            throw new RequestException("Layer does not exist");
        }
        LayerInfoDTO layerInfoDTO = new LayerInfoDTO();
        layerInfoDTO.setName(datasetAccessOGC.getTitle());
        layerInfoDTO.setServerUrl(serverUrl);
        layerInfoDTO.setLayerName(ListUtil.toString(layers, new ListUtil.GetLabel<WMSCapabilities.WMSLayer>() {
            @Override
            public String getLabel(WMSCapabilities.WMSLayer value) {
                return value.getLayerName();
            }
        }, ","));
        layerInfoDTO.setDescription(datasetAccessOGC.getPitch());
        layerInfoDTO.setStyleName(datasetAccessOGC.getStyleName());
        layerInfoDTO.setCrs(layers.get(0).getSupportedSRS());
        Extent bounds = layers.get(0).getBounds();
        for(WMSCapabilities.WMSLayer layer : layers) {
            Extent layerBounds = layer.getBounds();
            bounds.setEast(Math.max(bounds.getEast(), layerBounds.getEast()));
            bounds.setNorth(Math.max(bounds.getNorth(), layerBounds.getNorth()));
            bounds.setWest(Math.min(bounds.getWest(), layerBounds.getWest()));
            bounds.setSouth(Math.min(bounds.getSouth(), layerBounds.getSouth()));
        }
        layerInfoDTO.setExtent(bounds);
        layerInfoDTO.setVersion(layers.get(0).getVersion());
        layerInfoDTO.setQueryable(layers.get(0).isQueryable());
        // now we need to make the call
        // make a square box
        double width = bounds.getEast() - bounds.getWest();
        double height = bounds.getNorth() - bounds.getSouth();
        double widthHeightRatio = width / height;
        if(widthHeightRatio > 1) {
            double newHeight = height * widthHeightRatio;
            double extra = (newHeight - height) / 2;
            bounds.setNorth(bounds.getNorth() + extra);
            bounds.setSouth(bounds.getSouth() - extra);
        } else {
            double newWidth = width / widthHeightRatio;
            double extra = (newWidth - width) / 2;
            bounds.setEast(bounds.getEast() + extra);
            bounds.setWest(bounds.getWest() - extra);
        }
        // add a bit of margin
        width = bounds.getEast() - bounds.getWest();
        height = bounds.getNorth() - bounds.getSouth();
        bounds.setSouth(bounds.getSouth() - height * 0.05);
        bounds.setNorth(bounds.getNorth() + height * 0.05);
        bounds.setWest(bounds.getWest() - width * 0.05);
        bounds.setEast(bounds.getEast() + width * 0.05);
        // make sure it is within limits
        bounds.setSouth(Math.max(bounds.getSouth(), -90.0));
        bounds.setNorth(Math.min(bounds.getNorth(), 90.0));
        bounds.setWest(Math.max(bounds.getWest(), -180.0));
        bounds.setEast(Math.min(bounds.getEast(), 180.0));
        // width higher so enlarge the height
        String bbox = bounds.getWest() + "%2C" +
                        bounds.getSouth() + "%2C" +
                        bounds.getEast() + "%2C" +
                        bounds.getNorth();
        String url = serverUrl + "&" +
                "SERVICE=WMS&" +
                "VERSION=1.1.1&" +
                "REQUEST=GetMap&" +
                "FORMAT=image%2Fjpeg&" +
                "LAYERS=" + layerName +
                "&SRS=EPSG%3A4326" +
                "&WIDTH=200" +
                "&HEIGHT=200&" +
                "BBOX=" + bbox;
        String imagePath = fileName + ".jpeg";
        File thumbnailFile = new File(ServerUtil.getSettings().getDataDirectory() + "./img", imagePath);
        FileUtils.copyURLToFile(new URL(url), thumbnailFile);
        return "./uploaded/img/" + imagePath;
    }

    private boolean existsWorkpspace(String workspaceName) throws MalformedURLException {
        return GeoserverUtils.getGeoserverReader().getWorkspaceNames().contains(workspaceName);
    }

    private void storeToPostgis() {
        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

        /*
         * SimpleFeatureStore has a method to add features from a
         * SimpleFeatureCollection object, so we use the ListFeatureCollection
         * class to wrap our list of features.
         */
            SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();

            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();

            } finally {
                transaction.close();
            }
            System.exit(0); // success!
        } else {
            System.out.println(typeName + " does not support read/write access");
            System.exit(1);
        }
    }

}