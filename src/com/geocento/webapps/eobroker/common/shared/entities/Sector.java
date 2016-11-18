package com.geocento.webapps.eobroker.common.shared.entities;

/**
 * Created by thomas on 06/06/2016.
 */
public enum Sector {

    all("All"),
    ecosystems("Ecosystems"),
    infrastructure("Infrastructure"),
    coastal("Coastal"),
    airquality("Air quality"),
    metocean("Metocean"),
    topography("Topography"),
    geology("Geology"),
    floods("Floods"),
    forests("Forests"),
    fires("Fires"),
    inlandwater("Inland water"),
    seaice("Sea-ice"),
    landcover("Land cover"),
    landuse("Land use");

    String name;

    Sector(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
