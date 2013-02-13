DDSL Command Line Tool
================

What is this?
----------------

[Ddsl](https://github.com/mbknor/ddsl) is implemented using Scala and is easy to include in your Scala/Java application,
but if you are using a none-JVM-language (Eg. Ruby on Rails) for your application you can use this **ddsl-cmdline-tool**.

How to use it
---------------

You can launch **ddsl-cmdline-tool** in a childprocess from your own app/process.
As long as you make sure that you kill (or it dies) if you die or quit, then you are fine.

If you are using **serviceUp** it is important to kill/quit **ddsl-cmdline-tool** if your app dies/quits,
to make sure your service is taken offline.

You communicate with **ddsl-cmdline-tool** using stdin/stdout in a simple telnet-ish protocol using json.

You can of course lanch it directly from the shell to test it.

All commands are newline-terminated.

All commands reply with only one line! If success, it is on the form

    ok <result>\n

And if not success:

    error <error text>\n



Some examples
--------------

Download it or [build it](build-info.md)...


**Start it**:

    java -jar ddsl-cmdline-tool_2.10-0.3.4-SNAPSHOT-one-jar.jar

If you want to customize where to find the ddsl_config.properties you can eigher set it as system environment variable like this:

    export DDSL_CONFIG_PATH=../ddsl-core/ddsl_config.properties

or you can start it like this:

    java -DDDSL_CONFIG_PATH=../ddsl-core/ddsl_config.properties -jar target/scala-2.10/ddsl-cmdline-tool_2.10-0.3.4-SNAPSHOT-one-jar.jar

**How to get help**?

Type the command 'help' (then enter):

    help

Which prints out this help with available commands and example json:

    ok Available commands:
    getBestServiceLocation {"sid":{"environment":"test","serviceType":"telnet","name":"telnetServer","version":"0.1"},"cid":{"environment":"Client env","name":"client name","version":"version","ip":"ip-address"}}
    getServiceLocations {"sid":{"environment":"test","serviceType":"telnet","name":"telnetServer","version":"0.1"},"cid":{"environment":"Client env","name":"client name","version":"version","ip":"ip-address"}}
    getAllAvailableServices
    serviceUp {"id":{"environment":"test","serviceType":"http","name":"cmd-tool","version":"0.1"},"sl":{"url":"http://localhost:4321/hi","quality":1.0,"lastUpdated":1347398923243,"ip":"127.0.0.1"}}
    serviceDown {"id":{"environment":"test","serviceType":"http","name":"cmd-tool","version":"0.1"},"sl":{"url":"http://localhost:4321/hi","quality":1.0,"lastUpdated":1347398923243,"ip":"127.0.0.1"}}
    help
    exit


The commands are a one-to-one-mapping to the methods found in [DdslClient](http://mbknor.github.com/ddsl-scaladoc/com/kjetland/ddsl/DdslClient.html)

Example:

    Send:     getAllAvailableServices\n
    Receive:  ok [{"id":{"environment":"test","serviceType":"telnet","name":"telnetServer","version":"0.1"},"locations":[{"url":"telnet://localhost:40039","quality":0.0,"lastUpdated":1347396898000,"ip":"10.0.0.7"},{"url":"telnet://localhost:40080","quality":0.0,"lastUpdated":1347396537000,"ip":"10.0.0.7"}]}]

Good luck :)


