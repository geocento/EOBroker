package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.WMSUtils;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessKML;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.events.GetFeatureInfo;
import com.geocento.webapps.eobroker.customer.client.events.GetFeatureInfoHandler;
import com.geocento.webapps.eobroker.customer.client.events.WCSRequest;
import com.geocento.webapps.eobroker.customer.client.events.WCSRequestHandler;
import com.geocento.webapps.eobroker.customer.client.places.VisualisationPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.VisualisationView;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetVisualisationDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceVisualisationDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class VisualisationActivity extends TemplateActivity implements VisualisationView.Presenter {

    public LayerInfoDTO currentLayer;

    private VisualisationView visualisationView;

    private DatasetAccess datasetAccess;

    private ArrayList<DatasetAccess> datasetAccesses;

    public VisualisationActivity(VisualisationPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        visualisationView = clientFactory.getVisualisationView();
        visualisationView.setPresenter(this);
        setTemplateView(visualisationView.asWidget());
        setTitleText("View data");
        displayMenu(false);
        Window.setTitle("Earth Observation Broker");
        bind();
        visualisationView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                handleHistory();
            }
        });
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long dataAccessId = null;
        if(tokens.containsKey(VisualisationPlace.TOKENS.dataAccessId.toString())) {
            try {
                dataAccessId = Long.parseLong(tokens.get(VisualisationPlace.TOKENS.dataAccessId.toString()));
            } catch (Exception e) {

            }
        }
        Long productDatasetId = null;
        if(tokens.containsKey(VisualisationPlace.TOKENS.productDatasetId.toString())) {
            try {
                productDatasetId = Long.parseLong(tokens.get(VisualisationPlace.TOKENS.productDatasetId.toString()));
            } catch (Exception e) {

            }
        }
        Long productServiceId = null;
        if(tokens.containsKey(VisualisationPlace.TOKENS.productServiceId.toString())) {
            try {
                productServiceId = Long.parseLong(tokens.get(VisualisationPlace.TOKENS.productServiceId.toString()));
            } catch (Exception e) {

            }
        }
        final Long finalDataAccessId = dataAccessId;
        if(productDatasetId != null) {
            try {
                REST.withCallback(new MethodCallback<ProductDatasetVisualisationDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, ProductDatasetVisualisationDTO productDatasetVisualisationDTO) {
                        setProductDatasetVisualisation(productDatasetVisualisationDTO, finalDataAccessId);
                    }
                }).call(ServicesUtil.assetsService).getProductDatasetVisualisation(productDatasetId);
            } catch (RequestException e) {
            }
        } else if(productServiceId != null) {
            try {
                REST.withCallback(new MethodCallback<ProductServiceVisualisationDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, ProductServiceVisualisationDTO productServiceVisualisationDTO) {
                        setProductServiceVisualisation(productServiceVisualisationDTO, finalDataAccessId);
                    }
                }).call(ServicesUtil.assetsService).getProductServiceVisualisation(productServiceId);
            } catch (RequestException e) {
            }
        }
    }

    private void setProductDatasetVisualisation(ProductDatasetVisualisationDTO productDatasetVisualisationDTO, final Long datasetId) {
        visualisationView.setProductDataset(productDatasetVisualisationDTO);
        // configure the list of datasets
        datasetAccesses = new ArrayList<DatasetAccess>();
        if(productDatasetVisualisationDTO.getDatasetAccess() != null) {
            datasetAccesses.addAll(productDatasetVisualisationDTO.getDatasetAccess());
        }
        if(productDatasetVisualisationDTO.getSamples() != null) {
            datasetAccesses.addAll(productDatasetVisualisationDTO.getSamples());
        }
        // select a dataset
        DatasetAccess datasetAccess = null;
        if(datasetId != null) {
            datasetAccess = ListUtil.findValue(datasetAccesses, new ListUtil.CheckValue<DatasetAccess>() {
                @Override
                public boolean isValue(DatasetAccess value) {
                    return value.getId().equals(datasetId);
                }
            });
        }
        if(datasetAccess == null) {
            datasetAccess = productDatasetVisualisationDTO.getDatasetAccess().size() > 0 ? productDatasetVisualisationDTO.getDatasetAccess().get(0) :
                    productDatasetVisualisationDTO.getSamples().size() > 0 ? productDatasetVisualisationDTO.getSamples().get(0) : null;
        }
        setDataAccess(datasetAccess);
    }

    private void setProductServiceVisualisation(ProductServiceVisualisationDTO productServiceVisualisationDTO, final Long datasetId) {
        visualisationView.setProductService(productServiceVisualisationDTO);
        // configure the list of datasets
        datasetAccesses = new ArrayList<DatasetAccess>();
        if(productServiceVisualisationDTO.getSamples() != null) {
            datasetAccesses.addAll(productServiceVisualisationDTO.getSamples());
        }
        // select a dataset
        DatasetAccess datasetAccess = null;
        if(datasetId != null) {
            datasetAccess = ListUtil.findValue(productServiceVisualisationDTO.getSamples(), new ListUtil.CheckValue<DatasetAccess>() {
                @Override
                public boolean isValue(DatasetAccess value) {
                    return value.getId().equals(datasetId);
                }
            });
        }
        if(datasetAccess == null) {
            datasetAccess = productServiceVisualisationDTO.getSamples().size() > 0 ? productServiceVisualisationDTO.getSamples().get(0) : null;
        }
        setDataAccess(datasetAccess);
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(GetFeatureInfo.TYPE, new GetFeatureInfoHandler() {

            int latest = 0;

            @Override
            public void onGetFeatureInfo(GetFeatureInfo event) {
                latest++;
                final int current = latest;
                String title = "Layer feature information";
                //visualisationView.displayMapInfoLoading(event.getMapPoint(), title, "Querying...");
                visualisationView.displayGetFeatureInfoLoading("Querying...");
                // TODO - add version as part of the dataAccess?
                try {
                    WMSUtils.getFeatureInfo(currentLayer.getServerUrl(),
                            currentLayer.getLayerName(),
                            "1.1.0",
                            "",
                            event.getExtent(),
                            event.getWidth(),
                            event.getHeight(),
                            (int) event.getPoint()[0],
                            (int) event.getPoint()[1],
                            new AsyncCallback<String>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    if(current == latest) {
                                        visualisationView.displayGetFeatureInfoContent("Error querying layer...");
                                    }
                                }

                                @Override
                                public void onSuccess(String result) {
                                    // only display the latest one
                                    if(current == latest) {
                                        //visualisationView.displayMapInfoContent(event.getMapPoint(), "Layer feature information", result);
                                        visualisationView.displayGetFeatureInfoContent(result);
                                    }
                                }
                            }
                            );
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        activityEventBus.addHandler(WCSRequest.TYPE, new WCSRequestHandler() {
            @Override
            public void onWCSRequest(WCSRequest event) {
                // replace with CRS aware extent
                Extent bounds = AoIUtil.getExtent(event.getAoi());
                String serverUrl = ((DatasetAccessOGC) datasetAccess).getWcsServerUrl();
                serverUrl = serverUrl + (serverUrl.contains("?") ? "" : "?");
                Window.open(serverUrl +
                        "service=WCS&" +
                        "request=GetCoverage&" +
                        "version=2.0.1&" +
                        "coverageid=" + ((DatasetAccessOGC) datasetAccess).getWcsResourceName() + "&" +
                        "SUBSET=Long(" + bounds.getWest() + "," + bounds.getEast() + ")&" +
                        "SUBSET=Lat(" + bounds.getSouth() + "," + bounds.getNorth() + ")&" +
                        // TODO - add bands selection
                        "SUBSETTINGCRS=http://www.opengis.net/def/crs/EPSG/0/4326&" +
                        "Format=geotiff", "_blank", null);

            }
        });

        handlers.add(visualisationView.getDownloadSample().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if(datasetAccess != null && datasetAccess.getUri() != null) {
                    Window.open(datasetAccess.getUri(), "_blank", null);
                }
            }

        }));

        handlers.add(visualisationView.getAddToFavourites().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO - check if added
                visualisationView.setAddToFavouritesLoading(true);
                try {
                    REST.withCallback(new MethodCallback<Void>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            visualisationView.setAddToFavouritesLoading(false);
                            displaySuccess("Could not add layers to your saved layers");
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            visualisationView.setAddToFavouritesLoading(false);
                            displaySuccess("Layer added to your saved layers");
                            visualisationView.setAddedToFavourites(true);
                        }
                    }).call(ServicesUtil.assetsService).addSavedLayer(datasetAccess.getId());
                } catch (RequestException e) {
                }
            }
        }));
    }

    @Override
    public void datasetAccessSelected(DatasetAccess datasetAccess) {
        String token = History.getToken();
        token = token.substring(0, token.lastIndexOf("=") + 1) + (datasetAccess.getId() + "");
        History.newItem(token, false);
        setDataAccess(datasetAccess);
    }

    private void setDataAccess(DatasetAccess datasetAccess) {
        this.datasetAccess = datasetAccess;
        visualisationView.selectDataAccess(datasetAccess);
        visualisationView.setDataAccessDescription(datasetAccess.getPitch());
        visualisationView.displayDownload(datasetAccess.getUri() != null);
        visualisationView.setDownloadTooltip("Download file" +
                (datasetAccess.getSize() == null ? "" : ", size is " + Utils.displayFileSize(Long.valueOf(datasetAccess.getSize()))));
        visualisationView.setLoadingInformation("Loading...");
        visualisationView.setAdditionalDatasets(ListUtil.filterValues(datasetAccesses, new ListUtil.CheckValue<DatasetAccess>() {
            @Override
            public boolean isValue(DatasetAccess value) {
                return !value.getId().equals(datasetAccess.getId());
            }
        }));
        if(datasetAccess instanceof DatasetAccessOGC) {
            // TODO - load layer information
            try {
                REST.withCallback(new MethodCallback<LayerInfoDTO>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        visualisationView.hideLoadingInformation();
                        visualisationView.displayInformationError(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, LayerInfoDTO layerInfoDTO) {
                        VisualisationActivity.this.currentLayer = layerInfoDTO;
                        visualisationView.hideLoadingInformation();
                        // add layer to map
                        visualisationView.setWMSLayer(layerInfoDTO);
                        visualisationView.displayLayerInfo(layerInfoDTO);
                        visualisationView.enableGetFeatureInfo(layerInfoDTO.isQueryable());
                        String wcsServerUrl = ((DatasetAccessOGC) datasetAccess).getWcsServerUrl();
                        visualisationView.enableWCS(wcsServerUrl != null && wcsServerUrl.length() > 0);
                    }
                }).call(ServicesUtil.assetsService).getLayerInfo(datasetAccess.getId());
            } catch (RequestException e) {
            }
        } else if(datasetAccess instanceof DatasetAccessKML) {
            // donwload file

        }
    }
}
