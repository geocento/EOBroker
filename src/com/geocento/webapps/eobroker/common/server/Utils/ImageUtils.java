package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.server.ServerUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by thomas on 22/11/2017.
 */
public class ImageUtils {

    static Logger logger = Logger.getLogger(ImageUtils.class);

    public static String processAndStoreImage(File file, String imageName, int width, int height) throws Exception {
        return processAndStoreImage(new FileInputStream(file), imageName, width, height);
    }

    public static String processAndStoreImage(InputStream inputStream, String imageName, int width, int height) throws Exception {
        try {
            // resize image and store file
            File imageFile = ServerUtil.getImageDataFile(imageName);
            logger.info("Process and store file at " + imageFile.getAbsolutePath());
            if(width > 0 && height > 0) {
                Thumbnails.of(inputStream).forceSize(width, height).toFile(imageFile);
            } else {
                // do not resize
                Thumbnails.of(inputStream).scale(1.0).toFile(imageFile);
                // TODO - add max width and height
            }
            return ServerUtil.getImageWebUrl(imageFile);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

}
