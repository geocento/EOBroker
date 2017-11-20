package com.geocento.webapps.eobroker.supplier.server.util;

import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.server.Utils.WMSCapabilities;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessFile;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.google.gwt.http.client.RequestException;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by thomas on 16/11/2017.
 */
public class DatasetAccessUtils {

    static private KeyGenerator keyGenerator = new KeyGenerator(16);

    public static String generateThumbnail(DatasetAccess datasetAccess) throws Exception {
        String imagePath = keyGenerator.CreateKey() + ".jpeg";
        File thumbnailFile = new File(ServerUtil.getSettings().getDataDirectory() + "./img", imagePath);
        if(datasetAccess instanceof DatasetAccessOGC) {
            generateThumbnailOGC((DatasetAccessOGC) datasetAccess, thumbnailFile);
        } else if(datasetAccess instanceof DatasetAccessFile) {
            generateThumbnailFile((DatasetAccessFile) datasetAccess, thumbnailFile);
        }
        return "./uploaded/img/" + imagePath;
    }

    private static void generateThumbnailFile(DatasetAccessFile datasetAccess, File thumbnailFile) throws Exception {
        if(datasetAccess.isHostedData()) {
            throw new Exception("Not supported for external files yet");
        }
        File file = new File(ServerUtil.getDataFilePath(datasetAccess.getUri()));
        if(!file.exists()) {
            throw new Exception("File is not on server yet");
        }
        String fileName = datasetAccess.getUri();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
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
        if(imageFile == null) {
            throw new Exception("Could not generate image for this file");
        }
        Thumbnails.of(imageFile).width(200).height(200).keepAspectRatio(true).toFile(thumbnailFile);
        if(tempFile != null) {
            tempFile.delete();
        }
    }

    public static void generateThumbnailOGC(DatasetAccessOGC datasetAccessOGC, File thumbnailFile) throws Exception {
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
        FileUtils.copyURLToFile(new URL(url), thumbnailFile);
    }

}
