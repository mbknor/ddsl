DDSL - Dynamic Distributed Service Locator
===================

DDSL is written in Scala and can be used by Scala, Java or any other Language on the JVM


(This is work in progress)


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


Have a look at the examples to see how it can be used:

The example
--------------------
The source can be found [here](https://github.com/mbknor/ddsl/tree/master/examples/ddsl-scala-examples/src/main/scala/ddslexamples)

To keep it as simple as possible, this example uses "telnet" as its communication-form, but it could easily be modified
into a full blown stack of REST/SOAP, WebApps etc..

It shows the following:

- how one or multiple clients can discover one or multiple servers and use them
- with automatic load balancing.
- you can add and remove servers and clients on the fly

### Guide explaining how to run example from scratch (on Mac/linux) ###

To follow this Guide you have to have *GIT* and [SBT](https://code.google.com/p/simple-build-tool/) installed

*Download and start ZooKeeper*

In our example we're only going to use one ZooKeeper node, but in production you would have several distributed around your network - no single point of failure..

Clone the ddsl project from GitHub to get a local copy

	git clone git@github.com:mbknor/ddsl.git

Go into the folder ddsl/ddsl-core

start sbt and execute *update* to download dependencies, then execute *publish-local* to build and depoly to your local ivy/sbt repo. Quit sbt

Go into the folder ddsl/examples/ddsl-scala-examples

start sbt and execute *update* and *compile*

We are now ready to start the first server:

in sbt, execute *run* and select *ddslexamples.DdslServiceProvider*

The server starts up, picks a random port and starts listening on it. then it broadcasts its location to ddsl.

To set up a client, just open a new terminal, go to the folder ddsl/examples/ddsl-scala-examples, start *sbt* and execute *run*, then select ddslexamples.DdslServiceConsumer

Now you can see them communicating

Now, open a new terminal and starts another server.. You will see that the client automatically starts to use both servers..

Then add some more clients, and servers, take some down... etc....

**Note:** If no server for a specific ServiceID is pressent in ddsl, the client will try to load location info from a file. Since this file is missing in this example the client will fail getting a location and will quit.


(More information soon)
