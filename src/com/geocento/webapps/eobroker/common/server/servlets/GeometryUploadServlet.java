package com.geocento.webapps.eobroker.common.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTWriter;
import de.micromata.opengis.kml.v_2_2_0.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GeometryUploadServlet extends HttpServlet {

    static private Logger logger = null;

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
            AoI aoi = null;
            byte[] fileContent = out.toByteArray();
            switch (extension) {
                case "kml":
                    aoi = convertKML(fileContent);
                    break;
                case "zip":
                    // TODO - implement shapefile converter
/*
                    aoi = convertSHP(filecontent);
*/
                    break;
                default:
                    throw new IOException();
            }
            if(aoi != null) {
                // save AoI
                EntityManager em = EMF.get().createEntityManager();
                try {
                    em.getTransaction().begin();
                    User user = em.find(User.class, userName);
                    aoi.setUser(user);
                    em.persist(aoi);
                    em.getTransaction().commit();
                } finally {
                    em.close();
                }
                // send response
                AoIDTO aoIDTO = new AoIDTO();
                aoIDTO.setId(aoi.getId());
                aoIDTO.setName(aoi.getName());
                aoIDTO.setWktGeometry(aoi.getGeometry());
                response.setStatus(200);
                response.setContentType("text/html");
                response.getWriter().print("<html><body><value>" + new Gson().toJson(aoIDTO) + "</value></body></html>");
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

    private AoI convertKML(byte[] fileContent) throws Exception {
        final Kml kml = Kml.unmarshal(new ByteArrayInputStream(fileContent, 0, fileContent.length));
        Feature rootFeature = kml.getFeature();
        return parseKMLFeature(rootFeature);
    }

    static AoI parseKMLFeature(Feature feature) throws Exception {
        if(feature instanceof Document) {
            List<Feature> childrenFeature = ((Document) feature).getFeature();
            if(childrenFeature != null) {
                for(Feature childFeature : childrenFeature) {
                    AoI aoi = parseKMLFeature(childFeature);
                    if(aoi != null) {
                        return aoi;
                    }
                }
            }
        } else if(feature instanceof Folder) {
            List<Feature> childrenFeature = ((Folder) feature).getFeature();
            if(childrenFeature != null) {
                for(Feature childFeature : childrenFeature) {
                    AoI aoi = parseKMLFeature(childFeature);
                    if(aoi != null) {
                        return aoi;
                    }
                }
            }
        } else if(feature instanceof Placemark) {
            Placemark placemark = (Placemark) feature;
            de.micromata.opengis.kml.v_2_2_0.Geometry geometry = placemark.getGeometry();
            AoI aoi = parseKMLGeometry(geometry, placemark.getName());
            if(aoi != null) {
                return aoi;
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
