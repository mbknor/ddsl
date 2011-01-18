package com.kjetland.ddsl

import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/18/11
 * Time: 10:25 PM
 * To change this template use File | Settings | File Templates.
 */

trait DdslConfig {
  def hosts : String
}

/**
 * Use this config class when you want to specify all config manually
 */
class DdslConfigManualImpl(val hosts : String) extends DdslConfig {

  if( hosts == null ) throw new Exception("hosts cannot be null")

}

/**
 * Use this config class if you want to load config from system environment variables
 */
class DdslConfigSysEnvImpl extends DdslConfig{

  private val log = Logger.getLogger(getClass)

  val hostsEnvName = "ZOOKEEPER_HOSTS"

  val hosts = resolveHosts()


  private def resolveHosts() : String = {

    log.info("Reading hosts from system environment variable '"+hostsEnvName+"'")
    //first try java properties
    val h = System.getProperty( hostsEnvName) match {
      case s : String => s
      case _ => System.getenv( hostsEnvName )
    }

    h match {
      case s : String => {
        log.info("Hostslist read from sys env: " + s)
        s
      }
      case _ => throw new Exception("Invalid ddsl config. hosts-list not found in system env '"+hostsEnvName+"' (or java env)")
    }


  }
 
}