The Scala example
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

