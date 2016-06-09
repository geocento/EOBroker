/**
 * Created by thomas on 01/06/2016.
 */

var arcgisMap = function(callback) {

    var self = this;

    require([
        "esri/map",
        "esri/toolbars/draw",
        "esri/graphic",
        "esri/geometry/Polygon",
        "esri/symbols/SimpleMarkerSymbol",
        "esri/symbols/SimpleLineSymbol",
        "esri/symbols/SimpleFillSymbol",
        "esri/Color"
    ], function(Map, Draw, Graphic,
                Polygon,
                SimpleMarkerSymbol, SimpleLineSymbol, SimpleFillSymbol, Color) {

        self.createDraw = function(map) {
            return new Draw(map);
        }

        self.createMap = function(baseMap, lat, lng, zoom, mapContainer, onload) {
            var map = new Map(
                mapContainer, {
                    basemap: baseMap,
                    center: [lng, lat],
                    zoom: zoom
            });
            map.on("load", onload(map));
        }

        self.createLineSymbol = function(color, width) {
            return new SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID, Color.fromString(color), width);
        }

        self.createFillSymbol = function(strokeColor, strokeWidth, fillColor) {
            return new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID, self.createLineSymbol(strokeColor, strokeWidth), Color.fromString(fillColor));
        }

        self.createPolygon = function(wktString) {
            // convert wkt string to coordinates
            var rings = [], index, latLngString, latLngArray;
            var latLngStrings = wktString.trim().split(",");
            for(index = 0; index < latLngStrings.length; index++) {
                latLngString = latLngStrings[index];
                latLngArray = latLngString.trim().split(" ");
                rings[index] = [parseFloat(latLngArray[0]), parseFloat(latLngArray[1])];
            }
            return new Polygon(rings);
        }

        function addToMap(evt) {
            var symbol;
            toolbar.deactivate();
            map.showZoomSlider();
            switch (evt.geometry.type) {
                case "point":
                case "multipoint":
                    symbol = new SimpleMarkerSymbol();
                    break;
                case "polyline":
                    symbol = new SimpleLineSymbol();
                    break;
                default:
                    symbol = new SimpleFillSymbol();
                    break;
            }
            var graphic = new Graphic(evt.geometry, symbol);
            map.graphics.add(graphic);
        }

        // now call the success method
        callback();

    });

}