DDSL - Dynamic Distributed Service Locator
===================

 * Service Discovery

 * Plug and play dynamic Cluster

 * Now also with modules for [Play Framework 1.x](https://github.com/mbknor/ddsl-playframework-module), [Play Framework 2](https://github.com/mbknor/ddsl-play2-module), [Dropwizard](https://github.com/mbknor/ddsl-dropwizard) and [Ruby](https://github.com/bmaland/ddslbg)(Thanks to Bjørn Arild Mæland)

DDSL is written in Scala and can be used by Scala, Java or any other Language on the JVM.

Have a look at [ddsl-cmdline-tool](https://github.com/mbknor/ddsl/tree/master/ddsl-cmdline-tool) if you are using a none-JVM-language or just likes the command-line.

Here you can find <a href="http://bit.ly/kEhVOc ">my Lightningtalk about DDSL</a> at Roots Conference in may, 2011.

Check out [ddslConfigWriter](https://github.com/mbknor/ddslConfigWriter) which automatically reconfigures nginx (or any other reverse proxy like apache, squid etc)


Project history
-------------------

 * 20130211 - Version 0.3.3 released - using Scala 2.10.0
 * 20121029 - Version 0.3.2 released
 * 20121029 - Fixes #6 - Prevent exception when querying a never-used zookeeper
 * 20121009 - Version 0.3.1 released - Fixes logger dependency issue
 * 20121004 - Version 0.3 released
 * 20121004 - Added persistent serviceUp - usefull for static resources like databases etc
 * 20120911 - Added [ddsl-cmdline-tool](https://github.com/mbknor/ddsl/tree/master/ddsl-cmdline-tool)
 * 20120907 - (0.3-SNAPSHOT) Upgraded to scala 2.9.2, sbt 0.12, replaced log4j with logback/slf4j, and upgraded to zookeper 3.4.3
 * 201105xx - Added Java example
 * 20110226 - version 0.2 released
 * 20110125 - version 0.1.RC1 released


Where does DDSL help?
-------------------------

In many big companies you have a lot of services (SOAP, REST, etc) spread across many servers on 
several different Web Containers/Application Servers/ESB (Weblogic, Glassfish, Tomcat, Jetty, Mule, etc).
At least this is the situation where I work. You also have several different environments:
test, preprod, prod etc with different servers and databases etc.

One service might use several other services.

You might also have (or want) several different versions of one service to run at the same time.

All those service locations... This means a lot of configuring

This is where DDSL helps..


What is DDSL ?
------------------

DDSL - Dynamic Distributed Service Locator

(Scaladoc can be found [here](http://mbknor.github.com/ddsl-scaladoc/))


### Dynamic ###

- No admin needed
- You don't have to manually add your service / version to the repository
- Your application can automatically register its location.
-- It can also register it's "quality" (Clients will preferred locations with better "quality"
- Locations on "localhost" will be preferred
- You can mix several "environments" (prod, test) within the same DDSL-repository
- Automatically load balancing between multiple locations with same "quality"
- Service is automatically removed from repository, if it crashes/go down

### Distributed ###

- DDSL has no single point of failure
- It uses [ZooKeeper](https://hadoop.apache.org/zookeeper/) as its dynamic distributed storage

### Service Locator ###

- A repository of services (with version) and their current locations


### Coming functionality ###

#### Distributed logging ####

Distributed logging will have no single point of failure and will make it possible to
look up which client is using / depending on which service...


How to use DDSL?
====================

The idea behind DDSL is really simple and dynamic / flexible - So is its usage.

API documentation can be found [here](http://mbknor.github.com/ddsl-scaladoc/)

Below you can find a simple and running example but first some highlights. 

Both servers and clients uses [DdslClient](http://mbknor.github.com/ddsl-scaladoc/com/kjetland/ddsl/DdslClient.html) to communicate with DDSL.
The client is created like this:

	val client = new DdslClientImpl
	
When a service wants to broadcast that it is available, this is how it is done:

	client.serviceUp( Service( serviceId, serviceLocation))

[ServiceId](http://mbknor.github.com/ddsl-scaladoc/com/kjetland/ddsl/model/ServiceId.html) specifies what kind of service it is, 
and [serviceLocation](http://mbknor.github.com/ddsl-scaladoc/com/kjetland/ddsl/model/ServiceLocation.html) specifies how clients can reach us.

When a client wants to get the best location of a specific service:

	val location = client.getBestServiceLocation( ServiceRequest(serviceId, clientId ))
	
It specifies the [ServiceId](http://mbknor.github.com/ddsl-scaladoc/com/kjetland/ddsl/model/ServiceId.html) describing what service it needs, and
[ClientId](http://mbknor.github.com/ddsl-scaladoc/com/kjetland/ddsl/model/ClientId.html) so DDSL can log which client is using which services.

When using DDSL to plumb your deployment together, you might also want to use DDSL for everything; including finding the database-server, or some other static service. In situations like this, you can save the location in DDSL permanent (Meaning it will not go offline when the DDSL-client disconnects):

	client.serviceUp( Service( serviceId, serviceLocation), true)



Have a look at the examples to see how it can be used:

 * [Scala example](https://github.com/mbknor/ddsl/tree/master/examples/ddsl-scala-examples)
 * [Java example](https://github.com/mbknor/ddsl/tree/master/examples/ddsl-java-examples)

----

Also, have a look at [DDSL-Status](https://github.com/mbknor/ddsl-status), a simple web-app showing status of all online services.
