DDSL - Dynamic Distributed Service Locator
===================

(This is work in progress)


Where does DDSL help?
-------------------------

In many big companies you have a lot of services (SOAP, REST, etc) spread across many servers on 
several different Web Containers/Application Servers/ESB (Weblogic, Glassfish, Tomcat, Jetty, Mule, etc).
At least this is the situation where I work. You also have different environment:
test, preprod, prod etc with different servers and databases etc.

One service might use several other services.

You might also have (or want) several different versions of one service to run at the same time.

All those service locations... This means a lot of configuring

This is where DDSL helps..


What is DDSL ?
------------------

DDSl - Dynamic Distributed Service Locator


- Dynamic
-- No admin needed
-- You don't have to manually add your service / version to the repository
-- Your application can automatically register its location.
--- It can also register it's "quality" (Clients will preferred locations with better "quality"
-- Locations on "localhost" will be preferred
-- You can mix several "environments" (prod, test) within the same DDSL-repository
-- Automatically load balancing between multiple locations with same "quality"

- Distributed
-- DDSL has no single point of failure
-- It uses ZooKeeper (https://hadoop.apache.org/zookeeper/) as its dynamic distributed storage

- Service Locator
-- A repository of services (with version) and their current locations


(More information soon)
