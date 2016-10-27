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

    private final String name;

    private Sector(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    static public Sector fromName(String value) {
        for(Sector sector : values()) {
            if(sector.equalsName(value)) {
                return sector;
            }
        }
        return null;
    }

}
