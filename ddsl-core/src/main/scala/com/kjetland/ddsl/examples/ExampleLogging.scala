package com.kjetland.ddsl.examples



/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/22/11
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */


trait ExampleLogging{
  //configuring log4j with none-standard filename located on disk, but not
  //included in jar files.
  //we don't want users of ddsl to end up using our log4j config
  //DOMConfigurator.configure("ddsl_example_log4j.xml");
}