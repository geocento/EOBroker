/**
 * Created by thomas on 01/06/2016.
 */

var arcgisMap = function(callback) {

    var self = this;

    require([
        "esri/config",
        "esri/urlUtils",
        "esri/map",
        "esri/toolbars/draw", "esri/toolbars/edit",
        "esri/graphic",
        "esri/geometry/Polygon", "esri/geometry/Point", "esri/geometry/Extent",
        "esri/layers/WMSLayer",
        "esri/symbols/SimpleMarkerSymbol",
        "esri/symbols/SimpleLineSymbol",
        "esri/symbols/SimpleFillSymbol",
        "esri/Color",
        'esri/layers/WMSLayerInfo',
        'esri/geometry/geodesicUtils',
        "esri/dijit/Search",
        "esri/dijit/BasemapGallery",
        "esri/dijit/BasemapToggle",
        "esri/dijit/InfoWindow",
        // additional ones which need locading but no referencing
        "esri/geometry/ScreenPoint"
    ], function(esriConfig, urlUtils, Map, Draw, Edit, Graphic,
                Polygon, Point, Extent,
                WMSLayer,
                SimpleMarkerSymbol, SimpleLineSymbol, SimpleFillSymbol, Color, WMSLayerInfo, geodesicUtils,
                Search, BasemapGallery, BasemapToggle,
                InfoWindow
        ) {

/*
        urlUtils.addProxyRule({
            urlPrefix: "192.168.92.100:8088",
            proxyUrl: "/proxy.jsp"
        });
*/

        var infoWindow;

        self.createMap = function(baseMap, lat, lng, zoom, mapContainer, onload) {
            var map = new Map(
                mapContainer, {
                    basemap: baseMap,
                    center: [lng, lat],
                    zoom: zoom,
                    autoResize: true,
                    wrapAround180: true
            });
            map.on("load", function(){onload(map);});
        }

        self.createDraw = function(map) {
            return new Draw(map);
        }

        self.createEdit = function(map) {
            return new Edit(map);
        }

        self.createBaseMaps = function(map, element) {
            var basemapGallery = new BasemapGallery({
                showArcGISBasemaps: true,
                map: map
            }, element);
            basemapGallery.startup();

            basemapGallery.on("error", function(msg) {
                console.log("basemap gallery error:  ", msg);
            });
        }

        self.createBaseMapToggle = function(map, element) {
            var toggle = new BasemapToggle({
                map: map,
                basemap: "satellite"
            }, element);
            toggle.startup();
        }

        self.addSearch = function(map, div) {
            // add search bar
            var search = new Search({
                enableHighlight: false,
                enableInfoWindow: false,
                map: map
            }, div);
            search.startup();
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

        self.createExtent = function(xmin, ymin, xmax, ymax) {
            return new Extent(xmin, ymin, xmax, ymax, "EPSG:4326");
        }

        self.createGeometry = function(wktString) {
            var wkt = new Wkt.parse(wktString);
            if(wkt.type == 'MultiPolygon') {
                var coordinates = [];
                for(var index = 0; index < wkt.coordinates.length; index++) {
                    coordinates[index] = wkt.coordinates[index][0];
                }
                var polygon = new Polygon(coordinates);
                return polygon;
            } else if(wkt.type == 'Polygon') {
                var polygon = new Polygon(wkt.coordinates);
                return polygon;
            }
        }

        self.displayInfoWindow = function(map, title, content, location) {
            if(!infoWindow) {
                var domNode = document.createElement('div');
                map.root.appendChild(domNode);
                infoWindow = new InfoWindow({}, domNode);
                infoWindow.setMap(map);
                infoWindow.startup();
            }
            infoWindow.setTitle(title);
            infoWindow.setContent(content);
            infoWindow.show(
                location,
                //new Point({"x": location[0], "y": location[1], "spatialReference": {"wkid": 4326 } }),
                InfoWindow.ANCHOR_UPPERRIGHT);
        }

        self.hideInfoWindow = function(map) {
            infoWindow.hide();
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
        callback(self);

    });

    var Wkt = function() {
        var numberRegexp = /[-+]?([0-9]*\.[0-9]+|[0-9]+)([eE][-+]?[0-9]+)?/;
// Matches sequences like '100 100' or '100 100 100'.
        var tuples = new RegExp('^' + numberRegexp.source + '(\\s' + numberRegexp.source + '){1,}');

        /*
         * Parse WKT and return GeoJSON.
         *
         * @param {string} _ A WKT geometry
         * @return {?Object} A GeoJSON geometry object
         */
        this.parse = function(input) {
            var parts = input.split(';');
            var _ = parts.pop();
            var srid = (parts.shift() || '').split('=').pop();

            var i = 0;

            function $ (re) {
                var match = _.substring(i).match(re);
                if (!match) return null;
                else {
                    i += match[0].length;
                    return match[0];
                }
            }

            function crs (obj) {
                if (obj && srid.match(/\d+/)) {
                    obj.crs = {
                        type: 'name',
                        properties: {
                            name: 'urn:ogc:def:crs:EPSG::' + srid
                        }
                    };
                }

                return obj;
            }

            function white () { $(/^\s*/); }

            function multicoords () {
                white();
                var depth = 0;
                var rings = [];
                var stack = [rings];
                var pointer = rings;
                var elem;

                while (elem =
                    $(/^(\()/) ||
                    $(/^(\))/) ||
                    $(/^(,)/) ||
                    $(tuples)) {
                    if (elem === '(') {
                        stack.push(pointer);
                        pointer = [];
                        stack[stack.length - 1].push(pointer);
                        depth++;
                    } else if (elem === ')') {
                        // For the case: Polygon(), ...
                        if (pointer.length === 0) return null;

                        pointer = stack.pop();
                        // the stack was empty, input was malformed
                        if (!pointer) return null;
                        depth--;
                        if (depth === 0) break;
                    } else if (elem === ',') {
                        pointer = [];
                        stack[stack.length - 1].push(pointer);
                    } else if (!elem.split(/\s/g).some(isNaN)) {
                        Array.prototype.push.apply(pointer, elem.split(/\s/g).map(parseFloat));
                    } else {
                        return null;
                    }
                    white();
                }

                if (depth !== 0) return null;

                return rings;
            }

            function coords () {
                var list = [];
                var item;
                var pt;
                while (pt =
                    $(tuples) ||
                    $(/^(,)/)) {
                    if (pt === ',') {
                        list.push(item);
                        item = [];
                    } else if (!pt.split(/\s/g).some(isNaN)) {
                        if (!item) item = [];
                        Array.prototype.push.apply(item, pt.split(/\s/g).map(parseFloat));
                    }
                    white();
                }

                if (item) list.push(item);
                else return null;

                return list.length ? list : null;
            }

            function point () {
                if (!$(/^(point(\sz)?)/i)) return null;
                white();
                if (!$(/^(\()/)) return null;
                var c = coords();
                if (!c) return null;
                white();
                if (!$(/^(\))/)) return null;
                return {
                    type: 'Point',
                    coordinates: c[0]
                };
            }

            function multipoint () {
                if (!$(/^(multipoint)/i)) return null;
                white();
                var newCoordsFormat = _
                    .substring(_.indexOf('(') + 1, _.length - 1)
                    .replace(/\(/g, '')
                    .replace(/\)/g, '');
                _ = 'MULTIPOINT (' + newCoordsFormat + ')';
                var c = multicoords();
                if (!c) return null;
                white();
                return {
                    type: 'MultiPoint',
                    coordinates: c
                };
            }

            function multilinestring () {
                if (!$(/^(multilinestring)/i)) return null;
                white();
                var c = multicoords();
                if (!c) return null;
                white();
                return {
                    type: 'MultiLineString',
                    coordinates: c
                };
            }

            function linestring () {
                if (!$(/^(linestring(\sz)?)/i)) return null;
                white();
                if (!$(/^(\()/)) return null;
                var c = coords();
                if (!c) return null;
                if (!$(/^(\))/)) return null;
                return {
                    type: 'LineString',
                    coordinates: c
                };
            }

            function polygon () {
                if (!$(/^(polygon(\sz)?)/i)) return null;
                white();
                var c = multicoords();
                if (!c) return null;
                return {
                    type: 'Polygon',
                    coordinates: c
                };
            }

            function multipolygon () {
                if (!$(/^(multipolygon)/i)) return null;
                white();
                var c = multicoords();
                if (!c) return null;
                return {
                    type: 'MultiPolygon',
                    coordinates: c
                };
            }

            function geometrycollection () {
                var geometries = [];
                var geometry;

                if (!$(/^(geometrycollection)/i)) return null;
                white();

                if (!$(/^(\()/)) return null;
                while (geometry = root()) {
                    geometries.push(geometry);
                    white();
                    $(/^(,)/);
                    white();
                }
                if (!$(/^(\))/)) return null;

                return {
                    type: 'GeometryCollection',
                    geometries: geometries
                };
            }

            function root () {
                return point() ||
                    linestring() ||
                    polygon() ||
                    multipoint() ||
                    multilinestring() ||
                    multipolygon() ||
                    geometrycollection();
            }

            return crs(root());
        }

        /**
         * Stringifies a GeoJSON object into WKT
         */
        function stringify (gj) {
            if (gj.type === 'Feature') {
                gj = gj.geometry;
            }

            function pairWKT (c) {
                return c.join(' ');
            }

            function ringWKT (r) {
                return r.map(pairWKT).join(', ');
            }

            function ringsWKT (r) {
                return r.map(ringWKT).map(wrapParens).join(', ');
            }

            function multiRingsWKT (r) {
                return r.map(ringsWKT).map(wrapParens).join(', ');
            }

            function wrapParens (s) { return '(' + s + ')'; }

            switch (gj.type) {
                case 'Point':
                    return 'POINT (' + pairWKT(gj.coordinates) + ')';
                case 'LineString':
                    return 'LINESTRING (' + ringWKT(gj.coordinates) + ')';
                case 'Polygon':
                    return 'POLYGON (' + ringsWKT(gj.coordinates) + ')';
                case 'MultiPoint':
                    return 'MULTIPOINT (' + ringWKT(gj.coordinates) + ')';
                case 'MultiPolygon':
                    return 'MULTIPOLYGON (' + multiRingsWKT(gj.coordinates) + ')';
                case 'MultiLineString':
                    return 'MULTILINESTRING (' + ringsWKT(gj.coordinates) + ')';
                case 'GeometryCollection':
                    return 'GEOMETRYCOLLECTION (' + gj.geometries.map(stringify).join(', ') + ')';
                default:
                    throw new Error('stringify requires a valid GeoJSON Feature or geometry object as input');
            }
        }

        return this;
    } ();

}

