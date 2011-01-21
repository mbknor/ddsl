package com.kjetland.ddsl.examples

import org.apache.log4j.Logger
import com.kjetland.ddsl.{DdslDefaults, DdslConfigSysEnvReloading}

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/20/11
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */

object DdslConfigEnvLoaderMain{


  def main(args : Array[String]){
    val log = Logger.getLogger(getClass)
    try{
      doStuff
    }catch{
      case e:Exception => log.error("error", e)
    }
  }

  def doStuff{

    //System.setProperty(DdslDefaults.configSystemEnvironmentName, "ddsl_config.properties")

    val config = new DdslConfigSysEnvReloading

    println("host: " + config.hosts)

    println("config loaded successfully via env")

  }



}