# EOBroker
The Earth Observation Broker platform as part of the ESA funded BT4EO project

The EO Broker is a marketplace for finding, discovering and requesting information on earth observation products, data, services and suppliers.

The EO Broker as a whole is made up of the following components:
1) EO Broker webpage, a webpage introducing the main concepts of the eo broker, the project, etc... available @ http://eobroker.com
2) EO Broker web application, this project repository. Available @ http://eobroker.com/market
3) Sample map server, a Geoserver instance to host and display any map based sample of products for the user to view
4) Postgresql database with postgis extension, used by the web application for persistence and searches
5) Jetty for the web application server
6) NGINX, for the proxying of services

This repository includes the code related to the implementation of the EO Broker web application.

The technology used is free and open source technology.

The web application is built in Java with GWT + GWTMaterialDesign for the client coding part.
