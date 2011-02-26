Lightningtalk
at
ROOTS CONFERENCE, MAY 23.- 25. 2011, BERGEN


Tags: deployment, java, scala, configuration, servicelocator, cluster,
zookeeper, REST

Title: Plug-and-play service locator with load balancing

Description:

Most corporations now have a multitude of services (SOAP, REST, etc)
running on a number of different servers, within several different web
containers / application servers / ESBs. Many of these services depend
on each other. There are also several different environments: test,
pre-production, production etc., each with different servers and
databases.

All those service locations require a lot of configuration and present many
opportunities for mistakes

In this lightning talk I will talk about DDSL, Dynamic Distributed
Service Locator (https://github.com/mbknor/ddsl), and how it can help
you. DDSL is very lightweight, using Apache zookeeper to provide
fault-tolerant infrastructure, achieving vendor independent service
discovery with features like:

 * No single point of failure
 * No configuration needed when adding new services
 * Automatic load balancing
 * Hot-swapping of services
 * Simultaneously deployment of different versions of same service

