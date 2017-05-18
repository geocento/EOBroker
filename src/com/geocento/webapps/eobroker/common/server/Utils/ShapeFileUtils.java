package com.geocento.webapps.eobroker.common.server.Utils;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by thomas on 18/05/2017.
 */
public class ShapeFileUtils {

    static private Logger logger = Logger.getLogger(ShapeFileUtils.class);

    static public List<File> extractShapeFiles(byte[] fileContent, String path) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileContent));
        ZipEntry entry = zipInputStream.getNextEntry();
        // create directory to unzip the shapefiles
        File tmpPath = new File(path);
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
        return shapeFiles;
    }
}
