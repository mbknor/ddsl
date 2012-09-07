package com.kjetland.ddsl.examples


import com.kjetland.ddsl.model._
import com.kjetland.ddsl.config._
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/20/11
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */

object DdslConfigEnvLoaderMain extends ExampleLogging{


  def main(args : Array[String]){
    val log = LoggerFactory.getLogger(getClass)
    try{
      doStuff()
    }catch{
      case e:Exception => log.error("error", e)
    }
  }

  def doStuff(){

    //System.setProperty(DdslDefaults.configSystemEnvironmentName, "ddsl_config.properties")

    val config = new DdslConfigSysEnvReloading

    println("host: " + config.hosts)

    println("config loaded successfully via env")

  }



}