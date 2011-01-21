package com.kjetland.ddsl.examples

import com.kjetland.ddsl.DdslConfigSysEnvReloading
import org.apache.log4j.Logger

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

    val config = new DdslConfigSysEnvReloading

    println("host: " + config.hosts)

    println("config loaded successfully via env")

  }



}