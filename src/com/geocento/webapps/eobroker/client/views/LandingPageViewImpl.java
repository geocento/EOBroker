package com.geocento.webapps.eobroker.client.views;

import com.geocento.webapps.eobroker.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.client.widgets.MaterialSuggestion;
import com.geocento.webapps.eobroker.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.client.widgets.maps.resources.ArcgisMapJSNI;
import com.geocento.webapps.eobroker.client.widgets.maps.resources.DrawEventJSNI;
import com.geocento.webapps.eobroker.client.widgets.maps.resources.DrawJSNI;
import com.geocento.webapps.eobroker.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.shared.LatLng;
import com.geocento.webapps.eobroker.shared.Suggestion;
import com.geocento.webapps.eobroker.shared.entities.AoI;
import com.geocento.webapps.eobroker.shared.entities.Category;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialAnchorButton;
import gwt.material.design.client.ui.MaterialDropDown;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialNavBar;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class LandingPageViewImpl extends Composite implements LandingPageView {

    private Presenter presenter;

    interface LandingPageUiBinder extends UiBinder<Widget, LandingPageViewImpl> {
    }

    private static LandingPageUiBinder ourUiBinder = GWT.create(LandingPageUiBinder.class);

    @UiField
    MaterialLink signIn;
    @UiField
    MaterialNavBar navBar;
    @UiField
    ArcGISMap mapContainer;
/*
    @UiField
    MaterialAutoComplete searchBar;
*/
    @UiField
    MaterialSuggestion textSearch;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialLink categories;
    @UiField
    MaterialDropDown categoriesDropdown;
    @UiField
    MaterialLink allCategories;
    @UiField
    MaterialAnchorButton clearAoIs;

    public MapJSNI map;

    public LandingPageViewImpl(final ClientFactoryImpl clientFactory) {
        initWidget(ourUiBinder.createAndBindUi(this));
        mapContainer.loadArcGISMap(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                mapContainer.createMap("streets", new LatLng(40.0, -4.0), 3, new com.geocento.webapps.eobroker.client.widgets.maps.resources.Callback<MapJSNI>() {

                    @Override
                    public void callback(final MapJSNI mapJSNI) {
                        final ArcgisMapJSNI arcgisMap = mapContainer.arcgisMap;
                        LandingPageViewImpl.this.map = mapJSNI;
                        final DrawJSNI drawJSNI = arcgisMap.createDraw(mapJSNI);
                        drawJSNI.onDrawEnd(new com.geocento.webapps.eobroker.client.widgets.maps.resources.Callback<DrawEventJSNI>() {

                            @Override
                            public void callback(DrawEventJSNI result) {
                                drawJSNI.deactivate();
                                AoI aoi = AoIUtil.createAoI(arcgisMap.convertsToGeographic(result.getGeometry()));
                                displayAoI(aoi);
                                presenter.aoiChanged(aoi);
                            }
                        });
                        drawPolygon.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                mapJSNI.getGraphics().clear();
                                drawJSNI.activate("polygon");
                            }
                        });
                        clearAoIs.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                mapJSNI.getGraphics().clear();
                                presenter.aoiChanged(null);
                            }
                        });
                    }
                });
            }
        });
        textSearch.addKeyUpHandler(new KeyUpHandler() {

            private Timer fetchTimer;
            private String currentText = "";

            @Override
            public void onKeyUp(final KeyUpEvent event) {
                if (fetchTimer != null) {
                    fetchTimer.cancel();
                    fetchTimer = null;
                }
                // create a timer to make sure we don't query too soon
                fetchTimer = new Timer() {

                    @Override
                    public void run() {
                        // make sure we don't refresh options if the text hasn't changed
                        String text = textSearch.getText();
                        if (text.contentEquals(currentText)) {
                            return;
                        }
                        currentText = text;
                        presenter.textChanged(currentText);
                        fetchTimer = null;
                    }
                };
                // start the timer to make sure we waited long enough
                fetchTimer.schedule(300);
            }

        });

        // add categories
        for(final Category category : Category.values()) {
            MaterialLink categoryLink = new MaterialLink(category.getName());
            categoryLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    displayCategory(category);
                    presenter.categoryChanged(category);
                }
            });
            categoriesDropdown.add(categoryLink);
        }
        allCategories.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayCategory(null);
                presenter.categoryChanged(null);
            }
        });
    }

    @Override
    public void displayAoI(AoI aoi) {
        map.getGraphics().clear();
        if(aoi != null) {
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void displayCategory(Category category) {
        if(category == null) {
            categories.setText("All categories");
        } else {
            categories.setText(category.getName());
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayListSuggestions(List<Suggestion> searchObjects) {
        textSearch.displayListSearches(searchObjects);
    }

    @Override
    public void displayText(String text) {
        textSearch.setText(text);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}