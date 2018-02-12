# EOBroker
The Earth Observation Broker platform as part of the ESA funded BT4EEO project

The EO Broker is a marketplace for finding, discovering and requesting information on earth observation products, data, services and suppliers.

The EO broker implements three user roles to which correspond a deicated application:

1) The O&G customer, it can be a project manager, a contractor... They are looking for what EO can do for their challenges, how EO can contribute and the limitations, who can provide the data or the services, what are their performances, etc... Eventually a customer and a supplier can establish a pre-procurement conversation which could lead to commercial activity.

![eo broker customer](https://user-images.githubusercontent.com/1318091/36095873-3ede0658-0ff4-11e8-8d1c-8276488ecd0b.png)

2) The EO supplier, it is a company which provides EO based solutions for the O&G industry. Solutions are either off the shelf products, bespoke services, software solution or R&D project. They are looking to market their offering to the O&G industry. They use the supplier application to specify their company profile and offerings, as well as respond to customer queries in their pre-procurement phase. They also have access to market insight thanks for aggregated statistics on the eo broker use by customers.

![eo broker supplier](https://user-images.githubusercontent.com/1318091/36096405-8eb4fa72-0ff6-11e8-922e-a9163a972912.png)

3) The EO Administrator, these are users in charge of administering the application, eg configuring the product categories, accepting users, checking application state and statistics, answering feedback from users...

![eo broker admin](https://user-images.githubusercontent.com/1318091/36096496-eebcbefa-0ff6-11e8-85c1-3fe5f14c3f55.png)


The EO Broker as a whole is made up of the following components:
1) EO Broker webpage, a webpage introducing the main concepts of the eo broker, the project, etc... available @ http://eobroker.com
2) EO Broker web application, this project repository. Available @ http://eobroker.com/market
3) Sample map server, a Geoserver instance to host and display any map based sample of products for the user to view
4) Statsd and Graphite for the statistics collection
5) Postgresql database with postgis extension, used by the web application for persistence and searches
6) Jetty for the web application server
7) NGINX, for the proxying of services

This repository includes the code related to the implementation of the EO Broker web application.

The technology used is free and open source technology.

The web application is built in Java with GWT + GWTMaterialDesign for the client coding part.
