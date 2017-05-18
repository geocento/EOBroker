package com.geocento.webapps.eobroker.common.server.servlets;

import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.WKTWriter;
import de.micromata.opengis.kml.v_2_2_0.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GeometryUploadServlet extends HttpServlet {

    static private Logger logger = null;

    static CoordinateReferenceSystem geo = null;

    public GeometryUploadServlet() {
        logger = Logger.getLogger(GeometryUploadServlet.class);
        logger.info("Starting image upload servlet");
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
		try {
            String userName = UserUtils.verifyUser(request);
	        System.out.println("Start");
			// default values for width and height
			InputStream filecontent = null;
			String fileName = null;
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
	        System.out.println("Items " + items.size());
		    for (FileItem item : items) {
		        if (item.isFormField()) {
		            // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
		            String fieldName = item.getFieldName();
		            String fieldValue = item.getString();
		        } else {
		            // Process form file field (input type="file").
		            String fieldName = item.getFieldName();
			        System.out.println("field name " + fieldName);
		            fileName = FilenameUtils.getName(item.getName());
			        System.out.println("file name " + fileName);
                    filecontent = item.getInputStream();
		        }
		    }
	        System.out.println("Processing " + fileName);
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

	        System.out.println("Processing geometry file");
            // get the file format specified
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            List<AoI> aois = null;
            byte[] fileContent = out.toByteArray();
            switch (extension) {
                case "kml":
                    aois = convertFromKML(fileContent);
                    break;
                case "kmz":
                    aois = convertFromKMZ(fileContent);
                    break;
                case "zip":
                    aois = convertFromShpZip(fileContent);
                    break;
                default:
                    throw new IOException();
            }
            if(aois != null && aois.size() > 0) {
/*
                // save AoI
                EntityManager em = EMF.get().createEntityManager();
                try {
                    em.getTransaction().begin();
                    User user = em.find(User.class, userName);
                    aois.setUser(user);
                    em.persist(aois);
                    em.getTransaction().commit();
                } finally {
                    em.close();
                }
*/
                // send response
                AoI aoi = aois.get(0);
                AoIDTO aoIDTO = new AoIDTO();
                aoIDTO.setId(aoi.getId());
                aoIDTO.setName(aoi.getName());
                aoIDTO.setWktGeometry(aoi.getGeometry());
                response.setStatus(200);
                response.setContentType("text/html");
                response.getWriter().print("<html><body><value>" + new Gson().toJson(aoIDTO) + "</value></body></html>");
            } else {
                throw new Exception("Could not create AoI");
            }
		} catch (FileUploadException e) {
			writeError(response, "Could not read file");
	    } catch(IOException e) {
	    	writeError(response, "File type not supported!");
		} catch (Exception e) {
			writeError(response, "Error whilst processing and storing image: " + e.getMessage());
		} finally {
	        System.out.println("done");
		}
    }

    private List<AoI> convertFromKML(byte[] fileContent) throws Exception {
        final Kml kml = Kml.unmarshal(new ByteArrayInputStream(fileContent, 0, fileContent.length));
        Feature rootFeature = kml.getFeature();
        return parseKMLFeature(rootFeature);
    }

    static List<AoI> parseKMLFeature(Feature feature) throws Exception {
        if(feature instanceof Document) {
            List<Feature> childrenFeature = ((Document) feature).getFeature();
            if(childrenFeature != null) {
                for(Feature childFeature : childrenFeature) {
                    List<AoI> aois = parseKMLFeature(childFeature);
                    if(aois != null) {
                        return aois;
                    }
                }
            }
        } else if(feature instanceof Folder) {
            List<Feature> childrenFeature = ((Folder) feature).getFeature();
            if(childrenFeature != null) {
                for(Feature childFeature : childrenFeature) {
                    List<AoI> aois = parseKMLFeature(childFeature);
                    if(aois != null) {
                        return aois;
                    }
                }
            }
        } else if(feature instanceof Placemark) {
            Placemark placemark = (Placemark) feature;
            Geometry geometry = placemark.getGeometry();
            AoI aoi = parseKMLGeometry(geometry, placemark.getName());
            if(aoi != null) {
                return ListUtil.toList(aoi);
            }

        }
        return null;
    }

    private static AoI parseKMLGeometry(de.micromata.opengis.kml.v_2_2_0.Geometry geometry, String name) throws Exception {
        if(geometry instanceof de.micromata.opengis.kml.v_2_2_0.Point) {
        } else if(geometry instanceof de.micromata.opengis.kml.v_2_2_0.LineString) {
        } else if(geometry instanceof de.micromata.opengis.kml.v_2_2_0.Polygon) {
            AoI aoi = new AoI();
            aoi.setName(name);
            aoi.setGeometry(convertCoordinates((Polygon) geometry));
            return aoi;
        } else if(geometry instanceof de.micromata.opengis.kml.v_2_2_0.MultiGeometry) {
        }
        return null;
    }

    static public List<AoI> convertFromKMZ(byte[] fileContent) throws Exception {
        // create directory to unzip the shapefiles
        File tmpPath = new File(ServerUtil.getSettings().getDataDirectory() + "geometry/kmz/file_" + ((int) (Math.random() * 100)) + "_" + new Date().getTime());
        if(!tmpPath.mkdirs()) {
            throw new Exception("Could not create kmz directory");
        }
        File file = new File(tmpPath, "file.kmz");
        FileUtils.writeByteArrayToFile(file, fileContent);
        final Kml[] kmls = Kml.unmarshalFromKmz(file);
        List<AoI> features = new ArrayList<AoI>();
        for(Kml kml : kmls) {
            Feature rootFeature = kml.getFeature();
            features.addAll(parseKMLFeature(rootFeature));
        }
        return features;
    }

    static public List<AoI> convertFromShpZip(byte[] fileContent) throws Exception {
        // expect a zipped shape file
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileContent));
        ZipEntry entry = zipInputStream.getNextEntry();
        // create directory to unzip the shapefiles
        File tmpPath = new File(ServerUtil.getSettings().getDataDirectory() + "geometry/shp/features_" + ((int) (Math.random() * 100)) + "_" + new Date().getTime());
        if(!tmpPath.mkdirs()) {
            throw new Exception("Could not create shapefile directory");
        }
        // the standard shapefile with the shp extension
        ArrayList<File> shapeFiles = new ArrayList<File>();
        try {
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
                        if (file.getName().toLowerCase().endsWith(".shp")) {
                            shapeFiles.add(file);
                        }

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
                    }
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        }

        if (shapeFiles.isEmpty()) {
            FileUtils.deleteDirectory(tmpPath);
            throw new IOException("Could not find any file with .shp extension in the zip file");
        } else {

            ArrayList<AoI> aois = new ArrayList<AoI>();
            for(File shapeFile : shapeFiles) {
                ShapefileDataStore store = new ShapefileDataStore(DataUtilities.fileToURL(shapeFile));
                SimpleFeatureCollection features = store.getFeatureSource().getFeatures();
                // create the observations based on the features
                aois.addAll(convertToAoIs(features));
            }
            return aois;
        }
    }

    private static List<AoI> convertToAoIs(SimpleFeatureCollection features) throws Exception {
        SimpleFeatureIterator iterator = features.features();
        List<AoI> observations = new ArrayList<AoI>();
        while(iterator.hasNext()) {
            SimpleFeature feature = iterator.next();
            observations.addAll(convertToAoIs(feature));
        }
        return observations;
    }

    private static List<AoI> convertToAoIs(SimpleFeature feature) throws Exception {
        List<AoI> observations = new ArrayList<AoI>();

        Object geometryObj =  feature.getDefaultGeometry();

        if(geometryObj == null || !(geometryObj instanceof com.vividsolutions.jts.geom.Geometry))
        {
            //Try to get geometry from attributes first
            int attCount = feature.getAttributeCount();

            for (int i=0; i < attCount; i++)
            {
                if(feature.getAttribute(i) instanceof com.vividsolutions.jts.geom.Geometry) {
                    geometryObj = feature.getAttribute(i);
                    break;
                }
            }

            if(geometryObj==null)
                return observations; //Empty
        }
        // check if a crs conversion is required
        if(geo == null) {
            try {
                geo = CRS.decode("EPSG:4326", true);
            } catch (FactoryException e) {
                throw new Exception("Server conversion error");
            }
        }
        CoordinateReferenceSystem crs = feature.getFeatureType().getCoordinateReferenceSystem();
        if(crs == null) {
            try {
                crs = CRS.decode("EPSG:4326", true);
            } catch (FactoryException e) {
                throw new Exception("Server conversion error");
            }
        }
        MathTransform transform = null;
        if(!crs.equals(geo)) {
            try {
                transform = CRS.findMathTransform(crs, geo, true);
            } catch (FactoryException e) {
                throw new Exception("Unsupported projection type");
            }
        }
        // this is a standard shape file
        // derive feature type from the type of geometry
        com.vividsolutions.jts.geom.Geometry geometry = (com.vividsolutions.jts.geom.Geometry)geometryObj;
        // convert if required
        if(transform != null) {
            try {
                geometry = JTS.transform(geometry, transform);
            } catch (MismatchedDimensionException | TransformException e) {
                throw new Exception("Server transform error");
            }
        }
        // use the feature id for the name
        String name = feature.getID();
        // create observations
        if(geometry instanceof com.vividsolutions.jts.geom.Polygon || geometry instanceof MultiPolygon) {
            // only one polygon is allowed and only the exteriro ring is used
            // TODO - simplify the polygon if too many points are provided
            if(geometry instanceof MultiPolygon && ((MultiPolygon) geometry).getNumGeometries() > 0) {
                // merge into one polygon instead
                MultiPolygon multiPolygon = (MultiPolygon) geometry;
                for(int index = 0; index < multiPolygon.getNumGeometries(); index++) {
                    com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) multiPolygon.getGeometryN(index);
                    observations.add(extractPolygonObservation(polygon, name + "_" + index));
                }
            } else {
                observations.add(extractPolygonObservation((com.vividsolutions.jts.geom.Polygon) geometry, name));
            }
        } else {
            return observations; //Empty
        }

        return observations;
    }

    private static AoI extractPolygonObservation(com.vividsolutions.jts.geom.Polygon polygon, String name) {
        AoIPolygon observation = new AoIPolygon();
        com.vividsolutions.jts.geom.Coordinate[] coordinates = polygon.getExteriorRing().getCoordinates();
        if(coordinates.length > 200) {
            try {
                // TODO - add a simplify method that returns a fixed amount of points
                coordinates = simplify(coordinates, 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GeometryFactory fact = new GeometryFactory();
        com.vividsolutions.jts.geom.LinearRing linear = new GeometryFactory().createLinearRing(coordinates);
        com.vividsolutions.jts.geom.Polygon poly = new com.vividsolutions.jts.geom.Polygon(linear, null, fact);
        observation.setGeometry(new WKTWriter().write(poly));
        observation.setName(name);
        return observation;
    }

    private static String convertCoordinates(Polygon polygon) throws Exception {
        List<Coordinate> coordinates = polygon.getOuterBoundaryIs().getLinearRing().getCoordinates();
        if(coordinates == null || coordinates.size() == 0) {
            return null;
        }
        GeometryFactory fact = new GeometryFactory();
        com.vividsolutions.jts.geom.Coordinate[] polygonCoordinates = new com.vividsolutions.jts.geom.Coordinate[coordinates.size()];
        for(int index = 0; index < coordinates.size(); index++) {
            de.micromata.opengis.kml.v_2_2_0.Coordinate coordinate = coordinates.get(index);
            polygonCoordinates[index] = new com.vividsolutions.jts.geom.Coordinate(coordinate.getLongitude(), coordinate.getLatitude());
        }
        if(polygonCoordinates.length > 50) {
            polygonCoordinates = simplify(polygonCoordinates, 50);
        }
        com.vividsolutions.jts.geom.LinearRing linear = new GeometryFactory().createLinearRing(polygonCoordinates);
        com.vividsolutions.jts.geom.Polygon poly = new com.vividsolutions.jts.geom.Polygon(linear, null, fact);
        return new WKTWriter().write(poly);
    }

    // from EarthImages
    static class Triangle {

        com.vividsolutions.jts.geom.Coordinate previous;
        com.vividsolutions.jts.geom.Coordinate current;
        com.vividsolutions.jts.geom.Coordinate next;

        double area;

        public Triangle(com.vividsolutions.jts.geom.Coordinate first, com.vividsolutions.jts.geom.Coordinate second, com.vividsolutions.jts.geom.Coordinate third) throws Exception {
            super();
            this.previous = first;
            this.current = second;
            this.next = third;
            updateArea();
        }

        public void updateArea() throws Exception {
            // TODO - use an area preserving projection
            double[][] t = new double[][] {
                    projectMercator(previous),
                    projectMercator(current),
                    projectMercator(next)
            };
            area = Math.abs((t[0][0] - t[2][0]) * (t[1][1] - t[0][1]) - (t[0][0] - t[1][0]) * (t[2][1] - t[0][1])); //Math.abs((first.getLat() - third.getLat()) * (second.getLng() - first.getLng()) - (first.getLat() - second.getLat()) * (third.getLng() - first.getLng()));
        }

    }

    public static double[] projectMercator(com.vividsolutions.jts.geom.Coordinate latLng) throws Exception {
        if ((Math.abs(latLng.x) > 180 || Math.abs(latLng.y) > 90)) {
            throw new Exception("Lat lng in wrong range");
        }

        double num = latLng.x * 0.017453292519943295;
        double x = 6378137.0 * num;
        double a = latLng.y * 0.017453292519943295;

        return new double[] {x, 3189068.5 * Math.log((1.0 + Math.sin(a)) / (1.0 - Math.sin(a)))};
    }

    public static com.vividsolutions.jts.geom.Coordinate[] simplify(com.vividsolutions.jts.geom.Coordinate[] points, int numberOfPoints) throws Exception {
        // implement the Visvalingam-Whyatt algorithm for simplifying the points
        // create a list of triangles
        ArrayList<Triangle> triangles = new ArrayList<Triangle>();
        for(int index = 1; index < points.length - 1; index++) {
            Triangle triangle = new Triangle(points[index - 1], points[index], points[index + 1]);
            triangles.add(triangle);
        }
        int pointsToRemove = triangles.size() - numberOfPoints;
        // calculate the intervals at which we will update the progress status so that it makes an update every 100
        int progressIntervals = (int) pointsToRemove / 100;
        if(progressIntervals == 0) {
            progressIntervals = 1;
        }
        for(int count = 0; count < pointsToRemove; count++) {
            // find the triangle with the smallest area
            double minArea = triangles.get(0).area;
            int minIndex = 0;
            for(int index = 0; index < triangles.size(); index++) {
                Triangle triangle = triangles.get(index);
                if(minArea > triangle.area) {
                    minArea = triangle.area;
                    minIndex = index;
                }
            }
            // found the smallest area point
            Triangle minPoint = triangles.get(minIndex);
            // remove from the list and reconnect the two other ones
            if(minIndex > 0) {
                Triangle previousTriangle = triangles.get(minIndex - 1);
                previousTriangle.next = minPoint.next;
                previousTriangle.updateArea();
            }
            if(minIndex < triangles.size() - 1) {
                Triangle nextTriangle = triangles.get(minIndex + 1);
                nextTriangle.previous = minPoint.previous;
                nextTriangle.updateArea();
            }
            triangles.remove(minPoint);
        }
        // we have minimized our points
        // generate the new simplified list of points
        com.vividsolutions.jts.geom.Coordinate[] simplifiedPoints = new com.vividsolutions.jts.geom.Coordinate[triangles.size()];
        for(int index = 0; index < triangles.size(); index++) {
            simplifiedPoints[index] = triangles.get(index).current;
        }
        return simplifiedPoints;
    }

    protected void writeError(HttpServletResponse response, String message) throws IOException {
        response.sendError(500, message);
	}

}
